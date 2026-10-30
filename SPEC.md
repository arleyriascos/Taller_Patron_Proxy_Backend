# SPEC - arley-arlynx-api

Especificación para que un agente reconstruya, ejecute y pruebe el backend sin más contexto.

## 1. Contexto

Taller individual de Patrones de Software: patrón **Proxy** aplicado a una plataforma de consumo de IA con rate limiting y cuotas. Marca: **Arlynx Gate**. Estudiante: Arley. Backend en Spring Boot, desplegado en Railway. El frontend vive en el repositorio `arley-arlynx-web`.

## 2. Requisitos funcionales

- Interfaz `AIGenerationService` con `generate(GenerationRequest): GenerationResponse`.
- `MockAIGenerationService`: `Thread.sleep(1200)` y texto aleatorio predefinido.
- `RateLimitProxyService`: máximo N requests por minuto según el plan; si se supera responde `429 Too Many Requests` con header `Retry-After`.
- `QuotaProxyService`: verifica la cuota mensual de tokens, descuenta tokens; si se agota responde `402 Payment Required`.
- Cadena: `RateLimitProxyService -> QuotaProxyService -> MockAIGenerationService`.
- Planes: FREE (10 req/min, 50.000 tokens/mes), PRO (60 req/min, 500.000 tokens/mes), ENTERPRISE (ilimitado en ambos).
- Tarea programada: resetea el rate limit cada minuto y la cuota mensual el primer día del mes (UTC).
- Usuario identificado por el header `X-User-Id`. Todo en memoria (`ConcurrentHashMap`), sin base de datos.

## 3. Reglas obligatorias de código

1. Todo en inglés (clases, métodos, variables, endpoints, mensajes).
2. Cero comentarios en cualquier archivo de código.
3. POO: interfaces, composición, encapsulamiento, una responsabilidad por clase.
4. Un archivo por clase.
5. Sin carpetas de organización propias: todas las clases viven en el paquete único `com.arley.arlynx`.
6. Sin código repetido; dependencias hacia interfaces.

## 4. Arquitectura

Patrón Proxy:

| Rol | Clase |
|---|---|
| Subject | `AIGenerationService` |
| RealSubject | `MockAIGenerationService` |
| Proxy 1 (exterior) | `RateLimitProxyService` |
| Proxy 2 (interior) | `QuotaProxyService` |

El ensamblado de la cadena ocurre en un único lugar: `ProxyChainConfiguration`. Los controladores reciben el bean `AIGenerationService` (la cadena completa).

Clases de apoyo: `SubscriptionPlan` (enum), `UserAccount` (plan y tokens usados, sincronizado), `UserAccountRepository` / `InMemoryUserAccountRepository`, `RequestWindowTracker` / `InMemoryRequestWindowTracker` (contador por usuario con CAS), `MinuteWindow` (segundos hasta el próximo minuto), `UsageHistory` / `InMemoryUsageHistory` / `DailyUsage`, `TokenEstimator` / `CharacterRatioTokenEstimator` (ceil(caracteres / 4)), `TextSource` / `RandomCannedTextSource`, `BillingCycle`, `QuotaStatusProvider`, `PlanUpgradeService`, `UserIdRules`, `ClockConfiguration` (Clock UTC), `WebCorsConfiguration`, `ApiExceptionHandler`, `RateWindowResetTask`, `MonthlyQuotaResetTask`.

Decisiones razonables tomadas:

- Costo de un request = tokens del prompt + tokens de la respuesta (`ceil(chars/4)` ambos). Antes de llamar al servicio real se exige que el balance cubra al menos los tokens del prompt; el cargo se aplica tras la respuesta y nunca supera el límite del plan.
- Ventana de rate limit fija alineada al minuto UTC; `Retry-After` = segundos hasta el próximo minuto.
- `POST /api/quota/upgrade` sube al siguiente plan (FREE -> PRO -> ENTERPRISE); desde ENTERPRISE responde 409. Los tokens ya usados se conservan.
- Valores ilimitados se serializan como `-1`.
- Prompt máximo 8000 caracteres; `X-User-Id` máximo 64 caracteres.

## 5. Contrato de la API

Todas las rutas `/api/**` requieren `X-User-Id` salvo `/api/arlynx/pulse`.

### POST /api/ai/generate

Request: `{ "prompt": "text" }`

200: `{ "text": "...", "promptTokens": 3, "completionTokens": 180 }`

### GET /api/quota/status

200:

```json
{
  "plan": "FREE",
  "tokensUsed": 1200,
  "tokensRemaining": 48800,
  "tokenLimit": 50000,
  "resetDate": "2026-11-01",
  "requestsUsed": 2,
  "requestLimit": 10,
  "windowResetSeconds": 37
}
```

### GET /api/quota/history

200: arreglo de 7 elementos ascendentes por fecha: `[{ "date": "2026-10-24", "tokens": 0 }, ...]`

### POST /api/quota/upgrade

Sin body. 200: mismo cuerpo que `/status` con el plan nuevo. 409 si no hay plan superior.

### GET /api/arlynx/pulse

200: `{ "status": "UP", "service": "arlynx-api" }` (health check de Railway).

### Errores

Cuerpo: `{ "code": "...", "message": "...", "retryAfterSeconds": 12 }` (`retryAfterSeconds` solo en 429).

| Estado | code | Causa |
|---|---|---|
| 400 | INVALID_REQUEST | prompt vacío o muy largo, header ausente, JSON inválido |
| 402 | QUOTA_EXHAUSTED | cuota mensual agotada |
| 409 | PLAN_CHANGE_NOT_ALLOWED | no existe plan superior |
| 429 | RATE_LIMIT_EXCEEDED | límite por minuto superado; header `Retry-After: <segundos>` |
| 503 | GENERATION_INTERRUPTED | hilo interrumpido durante la simulación |

## 6. Comandos

```bash
mvn spring-boot:run
mvn test
mvn -DskipTests package
java -jar target/arlynx-api.jar
```

Prueba manual de la cadena (FREE):

```bash
for i in $(seq 1 11); do curl -s -o /dev/null -w "%{http_code}\n" -X POST http://localhost:8080/api/ai/generate -H "Content-Type: application/json" -H "X-User-Id: demo" -d '{"prompt":"hello"}'; done
```

Resultado esperado: diez `200` y un `429` con `Retry-After`. Para agotar la cuota enviar prompts de 8000 caracteres (aprox. 2200 tokens por request, unas 23 requests agotan FREE) y esperar un `402`.

## 7. Pruebas (JUnit 5)

`RateLimitProxyServiceTest`, `QuotaProxyServiceTest`, `ProxyChainOrderTest` (orden RateLimit -> Quota -> real), `MockAIGenerationServiceTest`, `PlanUpgradeServiceTest`, `InMemoryUsageHistoryTest`, `QuotaStatusProviderTest`, `BillingCycleTest`. Usan dobles propios y `Clock` fijo; sin Mockito.

## 8. Despliegue en Railway

1. Subir este repo a GitHub.
2. Railway: New Project -> Deploy from GitHub repo. Detecta `Dockerfile` y `railway.json`.
3. Variables: `ARLYNX_CORS_ALLOWED_ORIGINS` = URL exacta del frontend en Vercel (sin barra final). `PORT` lo inyecta Railway.
4. Settings -> Networking -> Generate Domain.
5. Verificar `https://<dominio>/api/arlynx/pulse`.

El almacenamiento es en memoria: reiniciar el servicio borra cuentas y consumo.
