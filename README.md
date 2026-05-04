# QA Web Practice

Proyecto Java con Spring Boot para practicar QA sobre un flujo web real:

- Registro de usuarios
- Login con Spring Security
- Dashboard protegido
- Base de datos H2
- Selectores estables para automatizacion (`id` y `data-testid`)

## Requisitos

- Java 21 o superior

## Como ejecutar

Desde la carpeta del proyecto:

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicacion queda en:

- `http://localhost:8080/`

## Usuarios demo

- `qa.admin@testlab.local` / `Admin123!`
- `qa.user@testlab.local` / `User123!`

## Base de datos H2

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/qa-practice-db`
- User: `sa`
- Password: `password`

## Ideas de pruebas QA

- Registro exitoso
- Registro con correo duplicado
- Login exitoso
- Login fallido
- Acceso no autorizado al dashboard
- Cierre de sesion y expiracion de acceso
- Verificacion de mensajes y validaciones del formulario

## Selenium para QA

Los tests HTTP que ya tienes con `MockMvc` validan el servidor.
Los tests con Selenium validan la aplicacion como lo haria un QA en navegador real:

- Abrir pagina
- Escribir en formularios
- Hacer clic
- Ver mensajes visuales
- Validar redirecciones y contenido del dashboard

El ejemplo quedo en:

- `src/test/java/com/qalab/qawebpractice/SeleniumQaFlowTests.java`

Ejecutarlo:

```powershell
.\mvnw.cmd "-Dtest=SeleniumQaFlowTests" test
```

Ver el navegador en pantalla:

```powershell
.\mvnw.cmd "-Dtest=SeleniumQaFlowTests" "-Dselenium.headless=false" "-Dselenium.slowMs=800" test
```

Que cubre:

- Registro + login + dashboard
- Login fallido con mensaje de error

Notas:

- Usa Chrome en modo headless
- Selenium Manager descargara el driver si hace falta
- Si quieres ver el navegador, quita `--headless=new` del test
- `selenium.headless=false` muestra Chrome en pantalla
- `selenium.slowMs=800` agrega pausas para que puedas mirar cada paso
- En PowerShell, pasa los `-D...` entre comillas para que Maven los reciba completos
