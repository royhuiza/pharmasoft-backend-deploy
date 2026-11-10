# PharmaSoft backend corregido

## Desarrollo Windows (PowerShell)
1. Descomprime en una carpeta nueva, no encima del original. Mantén PostgreSQL en Docker (`5433`) y la base `pharma_guia14`.
2. En PowerShell de la misma terminal donde arrancarás Spring Boot:
```powershell
$env:DB_PASSWORD = 'TU_CLAVE_POSTGRES'
$env:JWT_SECRET = -join ((1..48) | ForEach-Object { 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'[(Get-Random -Maximum 62)] })
$env:ADMIN_USERNAME = 'admin'
$env:ADMIN_PASSWORD = Read-Host 'Contraseña nueva para admin (minimo 12 caracteres)'
.\mvnw.cmd spring-boot:run
```
3. Prueba `http://localhost:8080/api/health` y `http://localhost:8080/swagger-ui.html`.
4. Login con POST a `/api/v1/auth/login` y JSON `{"username":"admin","password":"TU_CLAVE"}`. Para productos, usa encabezado `Authorization: Bearer TOKEN`.
5. La migración V2 crea la tabla usuarios en `pharma_guia14` y el administrador se crea una sola vez si no existe. Si ya existe, cambiar ADMIN_PASSWORD no cambia su contraseña.

## Despliegue
- `docker-compose.deploy.yml` usa PostgreSQL 16 + API en red privada y publica la API solo en loopback del servidor. Necesitas proxy HTTPS (Caddy/Nginx), dominio y certificado TLS para acceso externo.
- Crea `.env` **solo en el servidor**, no lo subas a Git. Variables `DB_PASSWORD`, `JWT_SECRET` (>=32 bytes), `ADMIN_USERNAME`, `ADMIN_PASSWORD` (>=12 caracteres).
- Ejecuta `docker compose -f docker-compose.deploy.yml up -d --build`.
- En Android cambia `urlBase` a `https://TU_DOMINIO/api/v1/` y recompila APK release; el servidor debe ser accesible por HTTPS.
- No uses `pharma_jwt` sin reconciliar antes sus migraciones V1/V2/V3. La base local de esta entrega es `pharma_guia14`.
- No se adjuntó el proyecto Android: para entregar un frontend compilado e integrado, adjunta el ZIP actualizado de PharmaMobil1.

## Seguridad
- Los endpoints `/api/v1/**` requieren JWT excepto login. Swagger está habilitado en desarrollo y deshabilitado en producción.
- La contraseña se guarda como hash BCrypt. No se incluyen credenciales ni claves predefinidas.
- Configura backups, firewall y HTTPS antes de exponer el servicio.
