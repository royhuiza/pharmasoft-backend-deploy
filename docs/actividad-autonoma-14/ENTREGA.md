# Actividad Autónoma 14 PharmaMobil

Responsable: Alexs Roy Huiza Flores. Revisión técnica: 12/11/2026.

Rama: entrega/actividad-autonoma-14. Los pendientes no se consideran aprobados.


## Producto A Guía de instalación y uso

A1 Requisitos y versiones

PharmaMobil utiliza Kotlin Multiplatform y comparte código entre Android e iOS. PharmaSoft expone una API Spring Boot con JWT y PostgreSQL. Para instalar en un teléfono real, el equipo que ejecuta el servicio y el teléfono deben estar en una red que permita comunicación entre ambos.

| Componente | Versión identificada | Comprobación |
| --- | --- | --- |

| Java | Azul Zulu 21.0.11 | Ejecutado en pruebas Gradle y Maven. |

| Gradle | 9.1.0 | Wrapper del proyecto; pruebas JVM ejecutadas. |

| Kotlin / Android Gradle Plugin | 2.4.10 / 9.0.1 | Catálogo gradle/libs.versions.toml. |

| Android SDK | compile y target 36; mínimo 24 | Configuración y metadatos del APK. |

| Android Studio | 2026.2.2; build 262.10968.63.2622.16840690 | Instalación local identificada; no acredita compilación desde el IDE. |

| Maven / Spring Boot | 3.9.16 / 4.0.7 | Wrapper y pom.xml; compilación Java realizada. |

| PostgreSQL | 16 en Docker Compose | Versión declarada. No se ejecutó en esta revisión. |

| Xcode | Versión probada no registrada | No disponible en Windows; requiere macOS. |

Archivos y preparación

```powershell
git clone https://github.com/royhuiza/PharmaMobilfrontend.git
git clone https://github.com/royhuiza/pharmasoft-backend-deploy.git
```

No se dispone de un registro de versiones usadas en el Samsung A71. La versión exacta de Android y la fecha de esa prueba deben confirmarse antes del cierre de la matriz.


## A2 Levantar y comprobar el servicio

Los siguientes comandos se ejecutan en PowerShell, dentro de pharmasoft-backend-deploy. Docker Desktop debe mostrar su motor en ejecución. La base local queda en localhost:5433; el servicio, en el puerto 8080. No es necesario crear tablas manualmente.

```powershell
$env:DB_PASSWORD = Read-Host 'Clave local de PostgreSQL'
docker compose up -d postgres
docker compose exec postgres pg_isready -U postgres -d pharma_guia14
```

Compose crea pharma_guia14 al inicializar un volumen vacío. Si ya existe el volumen de una instalación anterior, no se modifica automáticamente su base ni su clave. En ese caso, conservar los datos y crear la base con el propietario existente:

```powershell
docker compose exec postgres createdb -U postgres pharma_guia14
```

Configurar Java 21 en JAVA_HOME y las variables en la misma terminal de arranque. Definir JWT_SECRET con al menos 32 bytes y ADMIN_PASSWORD con al menos 12 caracteres; comunicarlos al evaluador por un canal privado.

```powershell
$env:DB_URL='jdbc:postgresql://localhost:5433/pharma_guia14'
$env:DB_USERNAME='postgres'
$env:JWT_SECRET=Read-Host 'Secreto JWT local'
$env:ADMIN_USERNAME='admin'
$env:ADMIN_PASSWORD=Read-Host 'Clave privada de prueba'
.\mvnw.cmd spring-boot:run
```

Comprobación desde otra terminal

```powershell
curl.exe -i http://localhost:8080/api/health
```

Se espera HTTP 200 y el texto Backend funcionando correctamente. La documentación de desarrollo está en http://localhost:8080/swagger-ui.html. Este resultado es el criterio de aceptación; en esta revisión no se obtuvo, porque el motor Docker y PostgreSQL no estaban activos.

Para detener el servicio: Ctrl+C. Para detener PostgreSQL conservando sus datos: docker compose stop postgres. No eliminar el volumen durante la evaluación.


## A3 Migraciones y datos de prueba

| Elemento | Contenido real |
| --- | --- |

| V1__esquema_inicial.sql | Crea categorías, clientes, productos, ventas y detalles con claves e índices. |

| V2__crear_usuarios.sql | Crea usuarios, rol y almacenamiento del hash de contraseña. |

| InitialAdminConfig | Crea el usuario indicado por ADMIN_USERNAME con rol ADMIN si no existe y si se proporcionan las variables. |

| Datos semilla de negocio | No hay inserciones de categorías, clientes o productos en las migraciones revisadas. Deben registrarse antes del recorrido. |

Usuario de prueba propuesto: admin, rol ADMIN. Su contraseña es la elegida al arrancar, no una contraseña incluida en el repositorio o en este PDF. Cambiar ADMIN_PASSWORD no reemplaza la contraseña de un usuario ya creado. La credencial de prueba se entrega por separado para cumplir la prohibición de contraseñas en el documento.

Preparar una categoría

Abrir Swagger de desarrollo y ejecutar POST /api/v1/auth/login con username y password elegidos localmente. Copiar el campo token de la respuesta y usar Authorize con el esquema Bearer. No capturar el token ni la contraseña en las evidencias.

Ejecutar POST /api/v1/categorias con el siguiente cuerpo y conservar el id devuelto. Esta categoría permite registrar el producto del recorrido; no asumir que su id es 1.

```powershell
{"nombre":"Prueba autonoma 14",
 "descripcion":"Categoria para el recorrido de evaluacion",
 "estado":true}
```

Datos del recorrido

| Registro | Valores de prueba |
| --- | --- |

| Producto | Nombre: Producto prueba 14; precio: 8.50; stock: 10; categoría: la creada arriba. |

| Edición | Cambiar precio a 9.00 y stock a 12. |

| Cliente | DNI ficticio de ocho dígitos no usado, nombres Cliente, apellidos Prueba, correo cliente14@example.com. No usar datos personales de terceros. |

Si ya existen esos registros, usar un sufijo distinto para evitar conflictos de unicidad. Los resultados de este procedimiento todavía deben confirmarse contra una base activa.


## A4 Instalar y configurar Android

Instalación del APK existente

Archivo verificado: androidApp/release/androidApp-release.apk. Su firma APK Signature Scheme v2 es válida. Identificador: pe.edu.upeu.pharmamobil. versionName 1.0.0 y versionCode 1. Requiere Android 7.0 o superior (API 24).

Copiar el APK al teléfono por USB o descargarlo desde una ubicación de confianza. Abrirlo con el administrador de archivos. Si Android lo solicita, entrar en Ajustes > Instalar aplicaciones desconocidas y autorizar temporalmente a esa aplicación de archivos. Confirmar Instalar y, al terminar, retirar esa autorización.

Si aparece un conflicto con una versión previa, verificar primero que se trata del mismo applicationId y certificado. No desinstalar automáticamente: esa acción puede borrar los datos locales. Respaldarlos y decidir antes de reemplazar una instalación firmada con otra clave.

Apuntar al servicio en la red local

La URL está fijada en shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/di/BackendConfig.kt. El código revisado contiene http://10.40.29.27:8080/api/v1/. No existe una pantalla para cambiarla en tiempo de ejecución; cambiar la IP exige recompilar y firmar el APK.

```powershell
ipconfig
# Elegir la IPv4 de la red Wi-Fi del equipo servidor
# En BackendConfig.kt usar: http://IP_DEL_PC:8080/api/v1/
```

Conectar teléfono y PC a la misma Wi-Fi. Desde el navegador del teléfono abrir http://IP_DEL_PC:8080/api/health. Si falla, revisar la red invitada, el aislamiento entre clientes y la regla de entrada TCP 8080 en el perfil privado de Windows. No usar localhost en un teléfono real.

Generación de los artefactos definitivos

```powershell
.\gradlew.bat :androidApp:assembleRelease :androidApp:bundleRelease
```

Restaurar localmente keystore.properties y androidApp/google-services.json antes de construir. No versionarlos. Las salidas habituales son androidApp/build/outputs/apk/release/ y androidApp/build/outputs/bundle/release/. Verificar con apksigner el APK y con jarsigner el AAB. El AAB no se instala directamente tocándolo en el teléfono.

La nomenclatura EquipoN_ProyectoFinal_Android.apk y EquipoN_ProyectoFinal_Android.aab queda pendiente del número de equipo. No se presenta un AAB como existente: no se recibió su ruta.


## A5 iOS y limitación de instalación

Procedimiento en macOS

Clonar el frontend en una Mac con Xcode y el JDK requerido por Gradle. Abrir iosApp/iosApp.xcodeproj. El módulo compartido tiene los destinos iosArm64 e iosSimulatorArm64 y genera el framework Shared.

En iosApp/Configuration/Config.xcconfig definir TEAM_ID con el equipo de desarrollo propio. La configuración actual tiene TEAM_ID vacío, PRODUCT_NAME PharmaMobil, MARKETING_VERSION 1.0 y CURRENT_PROJECT_VERSION 1. Confirmar un identificador de paquete único antes de registrar el dispositivo.

En Signing & Capabilities seleccionar el equipo autorizado y configurar la firma. Conectar el iPhone, aceptar la confianza en el equipo y habilitar Developer Mode si el sistema lo requiere. Seleccionar el dispositivo y ejecutar Run. La instalación depende de que la cuenta y el perfil permitan usar ese dispositivo.

Para un simulador Apple Silicon, seleccionar un simulador compatible con iosSimulatorArm64 y ejecutar la aplicación. Una ejecución en simulador sirve como evidencia alternativa, pero no demuestra una prueba en un iPhone real.

Conectividad

Preferir un servicio HTTPS accesible desde iOS. El backend configurado usa HTTP y el Info.plist revisado no declara una excepción de App Transport Security. El acceso HTTP puede impedir el recorrido en iOS; debe resolverse y verificarse antes de declarar esa plataforma operativa.

Limitación declarada

| Campo | Registro al 12 de noviembre |
| --- | --- |

| Equipo utilizado | Windows; no hay Xcode disponible en este entorno. |

| Prueba iOS del estudiante | No realizada; el estudiante declaró únicamente un Android. |

| Cuenta Apple utilizada | No informada; no se atribuye un intento de firma a una cuenta. |

| Impedimento comprobable | No se puede compilar ni firmar un IPA con Xcode en este equipo Windows. |

| Alternativa disponible | Código de iosApp y project.pbxproj versionados. No se dispone de captura de simulador u Organizer. |

| Pendiente | Ejecutar en Mac y adjuntar evidencia, o ampliar la limitación con el intento real y la cuenta empleada. |


## A6 Recorrido guiado de ocho pasos

Precondiciones: servicio accesible desde el teléfono, cuenta de prueba creada, categoría registrada y APK instalado. Este recorrido corresponde a las pantallas implementadas de autenticación, productos y clientes; Pedidos figura como funcionalidad futura en el código.

| Paso | Acción | Resultado esperado |
| --- | --- | --- |

| 1 | Abrir PharmaMobil e iniciar sesión con la cuenta de prueba. | Acceso a Inicio sin mostrar ni registrar el token. |

| 2 | Abrir el menú lateral y seleccionar Productos. | Listado o estado vacío; categorías disponibles para el formulario. |

| 3 | Registrar Producto prueba 14 con precio 8.50 y stock 10. | Confirmación y producto visible; sin errores de validación. |

| 4 | Editar ese producto: precio 9.00 y stock 12. | El listado refleja los valores actualizados. |

| 5 | Actualizar el listado y comprobar el producto creado. | Datos coherentes con el registro anterior; documentar si queda pendiente de sincronización. |

| 6 | Abrir Clientes y registrar Cliente Prueba con DNI único. | Confirmación y cliente visible en el listado. |

| 7 | Editar el teléfono o dirección del cliente y verificar la lista. | El registro muestra el dato corregido sin duplicarse. |

| 8 | Cerrar sesión desde el menú y volver a entrar. | Retorno al login y nuevo acceso correcto. Verificar qué datos persisten. |

Por cada paso, anotar resultado, dispositivo y defecto si existe. El estudiante confirmó que productos y clientes funcionan en su Samsung A71, pero no aportó un registro separado de estos ocho pasos; no se da por acreditada toda la secuencia.

Evidencias del recorrido

Capturar la lista tras registrar y editar el producto, la lista de clientes tras la modificación y el cierre de sesión. Numerar las capturas y citarlas por su número. Evitar que aparezcan contraseñas, tokens o datos personales. No se incorporan capturas de pantallas que no fueron ejecutadas.


## A7 Problemas y prueba de la guía

Problemas identificados y solución

| Situación | Diagnóstico y corrección |
| --- | --- |

| Base no encontrada o conexión rechazada | Compose original publicaba 5432 y creaba pharmadb; application-dev esperaba 5433/pharma_guia14. Se alineó Compose. Falta comprobar arranque con Docker activo. |

| Backend no disponible | docker version no encontró el motor Linux; la prueba de contexto Spring falló al conectar a PostgreSQL. Iniciar Docker Desktop, ejecutar Compose y repetir health. |

| El teléfono no llega al servicio | La URL del código está fijada a una IPv4 local. Verificar health desde el navegador del teléfono, cambiar BackendConfig y regenerar el APK si la IP ya no coincide. |

| Credenciales y servicios externos en Git | Se retiraron del seguimiento keystore.properties y google-services.json y se añadieron exclusiones. Siguen en el historial inicial; el saneamiento y la revisión de credenciales continúan abiertos. |

Registro obligatorio de validación por tercero

| Dato | Estado |
| --- | --- |

| Compañero de otro equipo | Pendiente de realizar la prueba. |

| Fecha y equipo de prueba | No registrados. |

| Servicio instalado por el tercero | No verificado. |

| Aplicación instalada por el tercero | No verificado. |

| Pasos que necesitó consultar | No registrados: todavía no se ejecutó esta validación. |

| Correcciones después de su prueba | Pendientes del resultado real. |

Mi prueba personal en un Android no sustituye esta validación. Para cerrar el producto A debo entregar la guía y los archivos a un compañero de otro equipo, registrar sus dudas, corregir los pasos y solicitar que repita los que fallaron.


## Producto B Checklist de entrega

Responsable de seguimiento: Alexs Roy Huiza Flores. Fecha del control técnico: 12 de noviembre de 2026. Conforme indica que existe evidencia revisada; parcial indica una parte comprobada; pendiente o no conforme exige una acción antes de la entrega. Los puntos pendientes no tienen fecha de cierre.

| Bloque y punto | Estado | Evidencia y cierre requerido |
| --- | --- | --- |

| Repositorios · rama de actividad | Conforme | Rama entrega/actividad-autonoma-14 creada en ambos repositorios. |

| Repositorios · integración final | Pendiente | Los cambios permanecen en la rama de actividad. Revisar y fusionar antes de seleccionar el commit final. |

| Repositorios · v1.0-final | Pendiente | No existe la etiqueta en los repositorios clonados. Etiquetar solo el commit que corresponda a los artefactos finales. |

| Repositorios · tres autores | No conforme | El historial recibido tiene un commit de royhuiza en cada repositorio. No acredita aportes propios de tres integrantes. |

| Secretos · árbol actual | Parcial | Configuraciones privadas del frontend retiradas del seguimiento; .gitignore reforzado en ambos repositorios. |

| Secretos · historial | No conforme | keystore.properties y google-services.json permanecen en el commit inicial del frontend. No se reescribió el historial remoto. |

| Secretos · Compose local | Conforme | Eliminada la clave fija de PostgreSQL; DB_PASSWORD es obligatoria. |

| Builds · APK firmado | Conforme | apksigner verify confirma firma v2 válida del APK existente. |

| Builds · AAB y nombres | Pendiente | AAB no localizado ni recibido. Número de equipo no informado. |

| Builds · trazabilidad | Parcial | Identificador y versión del APK coinciden con el código; no hay registro reproducible de su commit de construcción. |

Responsable asignado a todos los puntos de esta página: Alexs Roy Huiza Flores. Fecha de revisión técnica: 12/11/2026. Los estados se basan en inspección del código y verificaciones automáticas; no equivalen a una revisión presencial del equipo.


## Producto B Pruebas documentación y defensa

| Bloque y punto | Estado | Evidencia y cierre requerido |
| --- | --- | --- |

| Pruebas · primer Android | Parcial | Samsung A71; el estudiante declara funcionamiento de productos y clientes. Faltan versión, fecha y evidencia de los ocho pasos. |

| Pruebas · segundo Android | Pendiente | No se realizó. Usar otro modelo o versión y registrar recorrido completo. |

| Pruebas · iOS | Parcial | Se declara la limitación Windows y se entrega fuente; no hay intento de firma ni ejecución documentado en Mac. |

| Pruebas · defectos | Parcial | Tabla del producto C con identificadores y estados. Los cierres funcionales necesitan repetición en dispositivo. |

| Pruebas · JVM frontend | Conforme | 58 pruebas, cero fallos y cero errores; :shared:jvmTest terminó correctamente. |

| Documentación · PDF A B C D | Conforme | Portada proporcionada, índice, cuatro productos y repositorios al final. |

| Documentación · tercero | Pendiente | La guía todavía no fue validada por un compañero ajeno al equipo. |

| Documentación · plantilla final | Pendiente | No se recibió la plantilla del informe de sesión 13; este PDF cubre la actividad autónoma 14. |

| Sustentación · módulos | Pendiente | Responsabilidades de los demás integrantes no informadas. Confirmar nombre y módulo que defiende cada uno. |

| Sustentación · falla de red | Pendiente | Plan propuesto: PC y teléfono en red local, PostgreSQL con datos de prueba, APK firmado y copia de fuentes. Ensayar antes de la defensa. |

Control previo a la subida

No cerrar la entrega hasta confirmar el AAB, las pruebas faltantes, la validación externa y la relación entre build y commit. El PDF debe mantener su nombre Huiza_Flores_Alexs_Roy_Autonoma14.pdf. La fecha límite indicada por la consigna es el lunes 16 de noviembre de 2026 a las 23:59, hora de Lima.

Responsable asignado: Alexs Roy Huiza Flores. Fecha de revisión técnica: 12/11/2026. Fecha de cierre de los pendientes: sin registrar. La subida al aula virtual aún no se ha realizado.


## Producto C Matriz de dispositivos

La tabla registra el único dispositivo declarado y los dos ensayos que faltan. No se sustituyen dispositivos reales por pruebas JVM. La columna de pasos utiliza el recorrido 1 a 8 del producto A6.

| Dispositivo y plataforma | Build | Pasos y resultado | Responsable y fecha |
| --- | --- | --- | --- |

| Samsung Galaxy A71 · Android, versión sin informar | APK del repositorio, según declaración del estudiante; 1.0.0 (1). No se identificó cuál de los dos APK del repositorio instaló. | Productos y clientes: correcto según declaración. Pasos 2 a 7 relacionados con esos módulos, sin desglose ejecutado. Pasos 1 y 8 no confirmados individualmente. Observación OBS-01. | Alexs Roy Huiza Flores. Fecha de ejecución no informada; declaración recibida 12/11/2026. |

| Segundo Android · modelo y versión por definir | No instalado | Pasos 1 a 8 no ejecutados. Motivo: solo se declaró una prueba en Android. | Pendiente de asignación y ejecución. |

| iOS · modelo y versión no disponibles | No hay IPA verificado | Pasos 1 a 8 no ejecutados. Motivo: equipo Windows, sin Xcode disponible. Fuente iOS disponible; evidencia de ejecución alternativa pendiente. LIM-01. | Alexs Roy Huiza Flores, seguimiento. Sin fecha de prueba. |

Alcance de la comprobación técnica

En el frontend se ejecutaron 58 pruebas JVM con cero fallos y cero errores. Estas verifican lógica con dobles de prueba y SQLite en JVM. No demuestran instalación, cámara, interfaz, red o firma en los tres teléfonos exigidos.

El APK release tiene firma verificada y su versión fue leída con aapt. La orden adb devices -l no detectó teléfonos conectados durante esta revisión. Por ello no se ejecutó una prueba adicional en el Samsung ni se extrajo su versión de Android.

El archivo Evidencia_05_PharmaMobil_Celular.png del repositorio no pudo abrirse como PNG válido: su cabecera está codificada como texto UTF-16. No se usa para acreditar esta matriz. Se necesita una captura original válida del dispositivo.


## Producto C Registro de defectos

Los hallazgos de configuración se separan de los defectos funcionales en teléfonos. No se inventan errores de productos o clientes: el estudiante declaró que funcionan.

| ID y ámbito | Pasos y resultado esperado | Severidad y estado |
| --- | --- | --- |

| CFG-01 · servicio local | Con Compose original, comparar puerto/base con application-dev. Esperado: PostgreSQL accesible en 5433 con pharma_guia14. | Alta. Corregido en código; pendiente repetir health con Docker activo. |

| SEG-01 · repositorio frontend | Revisar archivos versionados e historial. Esperado: sin configuración privada de firma ni servicios externos. | Alta. Retirados del árbol actual; abierto por persistencia en historial. Revisar y rotar las credenciales expuestas según corresponda. |

| ENV-01 · backend en este equipo | Ejecutar Docker y pruebas de contexto. Esperado: conexión a PostgreSQL y arranque de Spring. | Bloqueo de entorno. Motor Docker ausente; conexión rechazada. No se acredita servicio operativo. |

| LIM-01 · iOS | Intentar preparación iOS desde Windows. Esperado: ejecución con Xcode en macOS y firma válida. | Alta para cobertura de entrega. Limitación declarada; falta ejecución y evidencia alternativa de Mac. |

| OBS-01 · Samsung A71 | Desglosar pasos 1 a 8, versión Android y fecha. Esperado: registro reproducible por dispositivo. | Media. Productos y clientes declarados correctos; falta detalle y captura. |

| EVI-01 · evidencia PNG | Abrir Evidencia_05_PharmaMobil_Celular.png. Esperado: imagen de captura legible. | Media. Archivo no válido como PNG; sustituir por la captura original. |

Criterio de cierre

Cada corrección se cierra después de repetir sus pasos en el entorno o dispositivo correspondiente y adjuntar fecha, responsable y resultado. Una modificación del código o una prueba JVM aprobada por sí sola no cierra un defecto de instalación en dispositivo.


## Producto D Ficha de versión y firma

| Dato | Valor comprobado o estado |
| --- | --- |

| Aplicación | PharmaMobil · pe.edu.upeu.pharmamobil |

| Versión Android | versionName 1.0.0 · versionCode 1 · minSdk 24 · targetSdk 36 |

| APK verificado | androidApp/release/androidApp-release.apk |

| Firma | Válida · APK Signature Scheme v2 · RSA 2048 bits · un firmante |

| Titular | CN=roy huiza, OU=aplicaciones moviles, O=upeu, L=lima, ST=chosica, C=pe |

| SHA-256 del certificado | 835b1fc275bf2b95d9a397747b03bb01cc1ce84646b6e9da6249c6ff31ecacb6 |

| Validez del certificado | Desde 10/11/2026 19:18:15 UTC hasta 28/03/2054 19:18:15 UTC. |

| SHA-256 del archivo APK | 4c84764880ae8d115e949e6058d25c8d8dd06ec13c383617bb718f848bfb0c44 |

| AAB firmado | Declarado por el estudiante, pero sin ruta ni archivo recibido para verificar. |

| IPA y firma iOS | No disponibles; limitación descrita en A5. |

| Minificación | Desactivada en release; isMinifyEnabled=false e isShrinkResources=false. El comentario del código indica diagnóstico del error m0. No se dispone de una reproducción de ese error. |

| Nomenclatura final | Pendiente número de equipo para EquipoN_ProyectoFinal_Android.apk y .aab. |

| Etiqueta v1.0-final | Pendiente en ambos repositorios; no se atribuye el APK a una etiqueta inexistente. |

| Commit de construcción del APK | No acreditado. El archivo ya existe en el commit inicial 166b14f del frontend, pero eso no prueba que fuera compilado desde ese mismo commit. |

La huella del certificado identifica al firmante. La huella del archivo identifica los bytes del APK. No son equivalentes. Esta ficha no contiene contraseñas, tokens ni claves privadas.
