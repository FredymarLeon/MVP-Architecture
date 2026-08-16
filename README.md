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
2. `suspend fun saveResult(event: SportEvent.ResultSuccess)`: guarda un resultado.
3. registerAd(): manejar click corto (registro/mostrar publicidad).
4. closeAd(): manejar click largo (cerrar publicidad).

Internamente, el repositorio tendrá una función privada publishEventRepository(event: SportEvent) que centraliza la publicación al EventBus: EventBus.instance().publishEvent(event). Todos los métodos de datos llamarán a publishEventRepository con el evento correspondiente.

Capa Presentador
----------------
- El Presentador será el suscriptor de EventBus y el intermediario entre Vista y Repositorio.
- Recibe comandos de la Vista y solicita al Repository las operaciones (getEvents, saveResult, registerAd, closeAd).
- Centraliza decisiones de flujo (cuándo refrescar, mostrar loaders, manejar errores y navegación).
- Esta capa es la clave dentro de esta arquitectura, ella va a funcionar como el cerebro de todo.
- Aquí es necesario hacer un llamado a la vista y al repositorio, que sería el modelo; así como las inyecciones de dependencias.
- En el módulo fue creada una nueva carpeta llamada `presenter` y dentro de ella una clase de Kotlin llamada `MainPresenter`.
- Para inyectar la vista, esta es recibida en el constructor de la clase como una variable privada, en este caso, de tipo `MainActivity`.
- Luego es definida una variable global privada para el repositorio y una coroutine.
- Creada una función pública, que será visible desde la vista, llamada `onCreate()`, en la que es inicializada la coroutine.
- Se crea una función para poder hacer la suscripción y estar atentos a EventBus, llamada `onEvent()`.
- La tercera función `onDestroy()`, en la que es cancelada la `coroutine` para que se liberen los recursos.
- Después de esto, se crean todas las funciones que van a ser consumidas/llamadas desde la vista y que se van a conectar con el repositorio
en la mayoría de los casos, porque también podría darse casos en donde simplemente aquí se pueda ejecutar una lógica. En este caso para:
refrescar, conseguir nuevos eventos, registrar que se hizo click en la publicidad y también cerrarla; guardar cuando se hace click a un elemento de la lista.
- Así como el método de la suscripción que es inicializado con `onCreate()`.

Este presenter actúa como punto central de coordinación entre la vista y el modelo: la vista le envía eventos o acciones, y el presentador
decide si invoca al repositorio, publica o consume eventos, o ejecuta lógica auxiliar antes de comunicar el resultado de vuelta a la UI.

Capa Vista (Activity / Fragment)
-------------------------------
- Sin lógica de negocio. Solo reacciona a interacciones de usuario y comandos del Presenter.
- Delegar clicks y acciones UI al Presenter.
- El package recibe el nombre de view y dentro de él colocamos a MainActivity, OnClickListener e ResultAdapter.
- Comenzamos con una primera versión, definiendo todos los métodos que van a ser despachados desde el presentador.
- En getEvents() tenemos la llamada a la base de datos, pero eso ya fue delegado al repositorio; sin embargo, tiene que haber un flujo de trabajo donde la vista se conecte al presentador, este a su vez al modelo y haya un flujo de trabajo inverso ya con la respuesta.
- Entonces haremos las respuestas a las que tiene que reaccionar la vista. Una vez que consultamos los eventos, pasan las siguientes acciones:
  - Si se obtiene un resultado exitoso, el evento es añadido al adaptador. Se crea una función add que recibe un evento de tipo `SportEvent.ResultSuccess`.
  - Cuando el swipe es configurado, lo primero que se hace es limpiar el adaptador, luego consultar los eventos (`getEvents()`) y finalmente volver a hacer visible el botón para la publicidad. Se crea otra función llamada `clearAdapter` que ejecuta la orden de limpiar el adapter (`adapter.clear()`).
  - Para controlar la visibilidad del botón de publicidad se crea un nuevo método llamado `showAdUI()`, que recibe `isVisible: Boolean` y hace una validación para mostrar u ocultar el botón según sea `true` o `false`.
  - Para mostrar si se está o no refrescando, con la animación de swipe (`setupClicks()`), se crea una función llamada `showProgress`, que recibe `isVisible: Boolean`.
  - Cuando tenemos un error se muestra un mensaje de tipo `Snackbar` que recibe un mensaje de tipo `String`: `Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()`.
  - Cuando recibimos y guardamos una publicidad/evento, se muestra un mensaje en un Toast. Esto se coloca en un método llamado `showToast` que recibe un mensaje de tipo `String` y será disparado por el presentador: `Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()`.
- Aún falta implementar adecuaciones que dependen de que el presentador esté configurado.


Integración y flujo
-------------------
1. La Vista inicializa el Presenter.
2. El Presenter pide getEvents() al Repository.
3. Repository obtiene datos y publica eventos con publishEventRepository.
4. Presenter (suscriptor) recibe respuesta y actualiza la Vista.
5. Acciones UI (guardar, registrar anuncio, cerrar anuncio) se envían al Presenter, que llama al Repository.

Configuración adicional
-----------------------
- Habilitar viewBinding en `module: build.gradle` (android { viewBinding { enabled true } }).
- Añadir dependencia de SwipeRefreshLayout en build.gradle: implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'

Cómo ejecutar
-------------
1. Abrir proyecto en Android Studio.
2. sincronizar Gradle.
3. Ejecutar en emulador o dispositivo.

Notas finales
-------------
Esta implementación traslada la lógica desde la Vista al Repository y Presenter para mantener un flujo limpio y testeable.
El Presenter toma las decisiones clave del flujo —este es el punto central de MVP en Android— mientras la Vista queda despojada de lógica.

Importante:
------------------
- La mayoría de las arquitecturas llevan el orden de trabajar en su mismo nombre, por ejemplo: MVP: Model - View - Presentación.

Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>