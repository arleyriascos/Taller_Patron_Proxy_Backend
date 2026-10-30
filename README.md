# arley-arlynx-api

Backend de **Arlynx Gate**: plataforma de consumo de IA simulada con proxies encadenados de rate limiting y cuotas (patrón Proxy). Spring Boot 3, Java 21, almacenamiento en memoria.

## Ejecutar localmente

Requisitos: JDK 21 y Maven 3.9+.

```bash
mvn spring-boot:run
```

El servidor queda en `http://localhost:8080`. Verificación rápida:

```bash
curl http://localhost:8080/api/arlynx/pulse
curl -H "X-User-Id: demo" http://localhost:8080/api/quota/status
```

Pruebas:

```bash
mvn test
```

Variables de entorno: `PORT` (por defecto 8080) y `ARLYNX_CORS_ALLOWED_ORIGINS` (por defecto `http://localhost:5173`, separar varios con coma).

Detalle completo en `SPEC.md`.
