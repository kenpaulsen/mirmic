# Mir Medjugorje Information Center Services
Services for CenterMirMedjugorje.com (Mir Medjugorje Information Center).

## Docker Build
See [README-Docker.md](README-Docker.md) for details, but at a high level (for now):

### Pre-build (TBD: make this part of the build)
1. Build the trip application (FIXME: remove dependency)
1. Build `freya` theme from the medjugorje app (`mvn install -P distribute`) (FIXME: remove dependency)
1. jar / cp all the `WEB-INF/classes` and `WEB-INF/lib` files from the trip app.
1. cp the freya jar and `resources/freya-layout`

### Build
1. docker compose down
1. docker compose build

## Certificates

As of 9/4/2023 I am using "LetsEncrypt". The [cert/tomcat-readme.md](cert/tomcat-readme.md) file for instructions.
