# MVP-Architecture (Tercera aplicación)

Descripción
-----------
Esta tercera aplicación implementa la arquitectura MVP (Modelo - Vista - Presentador) enfocada en Android. El objetivo es separar código por capas, aplicar el patrón Repository, extraer la lógica de la lista y añadir una capa Presentador que centralice la toma de decisiones en el flujo de trabajo.

Objetivos de aprendizaje
------------------------
- Separar el código por capas (View / Presenter / Model).
- Implementar el patrón Repository para acceso y manipulación de datos.
- Extraer la lógica de la UI (lista, clicks, guardado) fuera de la Vista.
- Centralizar la lógica de flujo en el Presenter.

Qué se recicla del proyecto Event-Bus-Pattern
--------------------------------------------
Se reutilizan clases, layouts y recursos: OnClickListener, ResultAdapter, DataBase.kt, EventBus, SportEvent, layouts, iconos, dimens y strings. También se tomarán métodos de MainActivity: setupAdapter(), setupRecyclerView(), setupSwipeRefresh(), setupClicks().

Estructura propuesta
--------------------
- \app
  - \src\main\java\...\mainModel
    - MainActivity (vista)
    - OnClickListener
    - ResultAdapter
    - model\MainRepository.kt
  - \res\layout
  - \res\values (strings, dimens, icons)

Capa Model / Repository
-----------------------
Crear MainRepository con 4 responsabilidades (métodos):
1. getEvents(): consulta general sin parámetros que devuelve la lista de resultados.
2. suspend fun saveResult(event: SportEvent.ResultSuccess): guarda un resultado.
3. registerAd(): manejar click corto (registro/mostrar publicidad).
4. closeAd(): manejar click largo (cerrar publicidad).

Internamente el repositorio tendrá una función privada publishEventRepository(event: SportEvent) que centraliza la publicación al EventBus: EventBus.instance().publishEvent(event). Todos los métodos de datos llamarán a publishEventRepository con el evento correspondiente.

Capa Presentador
----------------
- El Presentador será el suscriptor de EventBus y el intermediario entre Vista y Repositorio.
- Recibe comandos de la Vista y solicita al Repository las operaciones (getEvents, saveResult, registerAd, closeAd).
- Centraliza decisiones de flujo (cuándo refrescar, mostrar loaders, manejar errores y navegación).

Capa Vista (Activity / Fragment)
-------------------------------
- Sin lógica de negocio. Solo reacciona a interacciones de usuario y comandos del Presenter.
- Delegar clicks y acciones UI al Presenter.

Integración y flujo
-------------------
1. La Vista inicializa el Presenter.
2. El Presenter pide getEvents() al Repository.
3. Repository obtiene datos y publica eventos con publishEventRepository.
4. Presenter (suscriptor) recibe respuesta y actualiza la Vista.
5. Acciones UI (guardar, registrar anuncio, cerrar anuncio) se envían al Presenter, que llama al Repository.

Configuración adicional
-----------------------
- Habilitar viewBinding en module: build.gradle (android { viewBinding { enabled true } }).
- Añadir dependencia de SwipeRefreshLayout en build.gradle: implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'

Cómo ejecutar
-------------
1. Abrir proyecto en Android Studio.
2. Sincronizar Gradle.
3. Ejecutar en emulador o dispositivo.

Notas finales
-------------
Esta implementación traslada la lógica desde la Vista al Repository y Presenter para mantener un flujo limpio y testeable. El Presenter toma las decisiones clave del flujo —éste es el punto central de MVP en Android— mientras la Vista queda despojada de lógica.

Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>
