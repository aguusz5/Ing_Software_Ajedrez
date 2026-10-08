# Decisiones de diseño

Registro de las decisiones del grupo, en el formato que pide la consigna: **qué** decidimos, **por qué**
y **cuándo se rompería** (en qué situación dejaría de ser la mejor opción). Es la base del documento de
justificación.

Cuando se tome una decisión nueva, se agrega una entrada al final con el mismo formato. Si una decisión
se revierte, no se borra: se marca como reemplazada y se dice por cuál.

---

## Arquitectura

### 1. El núcleo es un módulo Maven sin dependencias

- **Qué:** las reglas del ajedrez viven en `ajedrez-nucleo`, cuyo `pom.xml` solo tiene JUnit en scope test.
  La consola, la GUI y el arranque son módulos aparte que dependen del núcleo, nunca al revés.
- **Por qué:** la consigna exige que el núcleo no dependa de frameworks ni de infraestructura y que se
  pruebe sin levantar nada. Con módulos separados no alcanza con la buena voluntad: el compilador
  rechaza un `import javax.swing` dentro del núcleo.
- **Cuándo se rompería:** si el núcleo necesitara algo del exterior que Java estándar no da (por ejemplo,
  una librería de motor de ajedrez para la IA). Aun así, se resolvería con un puerto de salida definido en
  el núcleo e implementado en otro módulo, no agregando la dependencia al núcleo.

### 2. Un único puerto de entrada: `JuegoDeAjedrez`

- **Qué:** los adaptadores solo conocen la interfaz `JuegoDeAjedrez` (`jugar`, `tablero`, `turnoActual`,
  `estaEnJaque`) y los datos que devuelve. Nunca importan `reglas`, `movimiento` ni `configuracion`.
- **Por qué:** así un adaptador no puede saltearse la validación ni mover una pieza por su cuenta, y una
  interfaz nueva (web, IA, tests) se agrega sin tocar el núcleo.
- **Cuándo se rompería:** si un adaptador necesitara información que el puerto no expone (por ejemplo,
  las jugadas legales de una pieza para resaltarlas). La solución es ampliar el puerto, no que el
  adaptador importe clases internas.

### 3. Sin puerto de salida

- **Qué:** el núcleo no define interfaces para que el exterior le provea cosas ni para notificar eventos.
- **Por qué:** hoy el núcleo no necesita nada de afuera: `jugar()` devuelve un `ResultadoJugada` y el
  adaptador decide cómo mostrarlo. Una interfaz sin uso real sería decoración.
- **Cuándo se rompería:** con guardado de partidas (puerto de persistencia) o con un reloj de juego
  (puerto de tiempo).

### 4. `Principal` es la raíz de composición

- **Qué:** `Principal` es el único lugar que hace `new` de las dependencias y las conecta. El resto de las
  clases recibe sus colaboradores por constructor.
- **Por qué:** cada clase se puede probar armándola con lo que el test necesite, y cambiar una pieza del
  sistema (otro tablero, otra regla) se hace en un solo lugar.
- **Cuándo se rompería:** si la cantidad de configuraciones creciera tanto que `Principal` se volviera
  inmanejable. Ahí convendría separar la composición en fábricas por módulo o usar un contenedor de
  inyección.

---

## Modelo y movimiento

### 5. Las piezas no son clases: son listas de reglas de movimiento

- **Qué:** no existe `class Torre`. Cada pieza es un `TipoDePieza` con un nombre, una marca `esRey` y una
  lista de `ReglaDeMovimiento` (Strategy). La dama es la lista de la torre más la del alfil.
- **Por qué:** composición sobre herencia. Una pieza nueva es un `new TipoDePieza(...)` con reglas que
  ya existen, sin tocar código (lo prueba `ExtensibilidadTest` con el canciller). Con herencia, la dama
  tendría que heredar de torre y de alfil a la vez, o duplicar código.
- **Cuándo se rompería:** si apareciera una pieza cuyo movimiento no se pudiera expresar como suma de
  reglas independientes (por ejemplo, una que se mueve distinto según lo que hizo en la jugada anterior).
  Igual se resolvería con una regla nueva, no con una subclase.

### 6. Las reglas de movimiento nunca generan jugadas sobre piezas propias

- **Qué:** `ReglaDeslizante`, `ReglaDeSalto` y las del peón descartan el destino si hay una pieza del
  mismo color.
- **Por qué:** las jugadas candidatas salen limpias. El validador solo se ocupa del turno y del
  auto-jaque, y el detector de jaque puede usar esas mismas jugadas sin filtrar nada. Además se descartó
  una regla de validación aparte para esto, porque parcheaba un problema que nacía en las reglas de
  movimiento.
- **Cuándo se rompería:** si una variante permitiera capturar piezas propias. Ahí el filtro tendría que
  salir de las reglas de movimiento y volver como una regla de validación configurable.

### 7. El tablero es inmutable

- **Qué:** `Tablero.con()` y `Tablero.sin()` devuelven un tablero nuevo. `Pieza`, `Posicion` y
  `JugadaSimple` también son inmutables (records).
- **Por qué:** para saber si una jugada deja al rey en jaque, se aplica sobre una copia y se pregunta al
  detector, sin riesgo de dejar el tablero real modificado. También permite pasarle el tablero a un
  adaptador sin que lo pueda cambiar, y hace casi gratis un deshacer futuro (guardar los tableros
  anteriores).
- **Cuándo se rompería:** si el rendimiento importara mucho, por ejemplo en una IA que evalúe millones de
  posiciones. Copiar el mapa en cada jugada sería caro y convendría un tablero mutable con
  hacer/deshacer.

### 8. Sin interfaz de solo lectura para el tablero

- **Qué:** las reglas y los adaptadores reciben `Tablero` directamente; no existe una interfaz tipo
  "vista del tablero".
- **Por qué:** como el tablero es inmutable, nadie lo puede modificar aunque lo reciba. Una interfaz de
  lectura no protegía nada y además rompía la simulación de jaque: las reglas recibían la vista, pero
  `Jugada.aplicarEn` necesita un `Tablero`.
- **Cuándo se rompería:** si el tablero dejara de ser inmutable (ver la decisión 7).

### 9. La jugada es un objeto: interfaz `Jugada`

- **Qué:** una jugada sabe de dónde sale, a dónde llega y cómo transforma un tablero (`aplicarEn`). Hoy
  la única implementación es `JugadaSimple`.
- **Por qué:** es una interfaz con una sola implementación, pero sostiene extensiones probables: enroque
  (mueve dos piezas), captura al paso (captura fuera del destino) y promoción (cambia el tipo de pieza)
  serían clases nuevas, sin modificar `Partida` ni el validador.
- **Cuándo se rompería:** si el grupo decidiera no implementar nunca jugadas especiales. Ahí la interfaz
  sobraría y alcanzaría con el record.

### 10. El doble paso del peón depende de `yaSeMovio`, no de la fila

- **Qué:** `Pieza` lleva la marca `yaSeMovio`. `ReglaAvanceDePeon` permite dos casillas solo si es
  `false`.
- **Por qué:** con "si está en la fila 2", el peón deja de funcionar en un tablero de otro tamaño o con
  otra posición inicial.
- **Cuándo se rompería:** si una variante pidiera explícitamente que el doble paso dependa de la zona del
  tablero (hay variantes así). Sería otra regla de avance.

### 11. Nada de `8` suelto

- **Qué:** el tamaño lo conoce el `Tablero` (`ancho`, `alto`). El único 8 del código está en
  `TableroEstandar`, que arma la posición inicial clásica.
- **Por qué:** la defensa individual puede pedir cambiar el tamaño del tablero. Hay un test que juega en
  10x10 sin modificar el núcleo, y la consola y la GUI se adaptan solas.
- **Cuándo se rompería:** no se rompería. Cualquier tamaño fijo debería seguir viviendo en una sola
  configuración.

### 12. El rey se identifica por la marca `esRey`, no por el nombre

- **Qué:** `TipoDePieza` tiene un booleano `esRey`. `Tablero.buscarRey` y el detector de jaque usan esa
  marca.
- **Por qué:** el detector no necesita saber qué piezas existen ni comparar strings. Una variante con otra
  pieza "real" solo la marca.
- **Cuándo se rompería:** con variantes de varios reyes por bando: `buscarRey` devuelve el primero que
  encuentra y habría que revisar qué significa "estar en jaque".

---

## Reglas y juego

### 13. La validación es una lista de reglas inyectada

- **Qué:** `ValidadorDeJugadas` recibe una lista de `ReglaDeValidacion` (`ReglaPiezaPropia` y
  `ReglaNoDejarReyEnJaque`) y la primera que rechaza decide.
- **Por qué:** es el punto real de extensión del sistema: una regla nueva es una clase nueva que se suma
  a la lista en `Principal`, sin modificar el validador (abierto/cerrado).
- **Cuándo se rompería:** si las reglas dependieran entre sí o necesitaran un orden complejo (por
  ejemplo, una que solo aplique si otra pasó). Ahí la lista plana no alcanzaría.

### 14. `DetectorDeJaque` es una clase concreta, no una interfaz

- **Qué:** se inyecta por constructor (en `Partida` y en `ReglaNoDejarReyEnJaque`), pero no tiene interfaz.
- **Por qué:** hay una sola forma de detectar el jaque y nadie la consume de forma polimórfica. En los
  tests se usan tableros armados a mano; reemplazar el detector por un doble confunde más de lo que ayuda.
- **Cuándo se rompería:** si una variante necesitara otra definición de jaque, o si hiciera falta un
  detector más rápido para una IA. Ahí se extrae la interfaz.

### 15. `TiposDePiezaEstandar` es estático y `TableroEstandar` no tiene interfaz

- **Qué:** las seis piezas se obtienen con métodos estáticos (`peon()`, `torre()`...). `TableroEstandar`
  es una clase concreta con `crearTablero()`. `Partida` recibe un `Tablero` ya armado, no una fábrica.
- **Por qué:** son clases sin estado ni alternativas. Inyectarlas o ponerles una interfaz sería
  indirección sin motivo: el juego necesita un tablero, no saber cómo se arma.
- **Cuándo se rompería:** si el usuario pudiera elegir entre varias configuraciones al arrancar (clásico,
  10x10, una variante). Ahí tendría sentido una interfaz de configuración que `Principal` elija.

### 16. Sin Observer

- **Qué:** el núcleo no notifica eventos; `jugar()` devuelve un `ResultadoJugada` con la validez, el
  motivo del rechazo y el jaque.
- **Por qué:** la consola y la GUI llaman a `jugar()` y reciben la respuesta en el acto. Nadie necesita
  enterarse de algo que no pidió.
- **Cuándo se rompería:** con un oponente de IA o una partida en red, donde aparecen jugadas que la
  interfaz no inició y alguien tiene que avisarle que redibuje.

---

## Adaptadores

### 17. Sin interfaz para dibujar el tablero

- **Qué:** `DibujanteAscii` es una clase concreta.
- **Por qué:** hay un solo dibujante de texto. La GUI no dibuja texto, así que no comparte esa
  abstracción.
- **Cuándo se rompería:** si la consola necesitara elegir entre dos formatos (por ejemplo, ASCII y
  Unicode con colores).

### 18. Las piezas nuevas se ven sin tocar los adaptadores

- **Qué:** `DibujanteAscii` usa la inicial del nombre de la pieza. `GlifosDePieza` busca el símbolo
  Unicode por nombre y, si no lo conoce, también usa la inicial.
- **Por qué:** una pieza nueva definida en el núcleo aparece en la consola y en la ventana sin modificar
  ninguna de las dos.
- **Cuándo se rompería:** si dos piezas compartieran la inicial: el canciller del test de extensibilidad
  se ve como una C, igual que el caballo en la consola. Ahí haría falta que el tipo de pieza tenga un
  símbolo propio.

---

## Convenciones

### 19. Nombres en español

- **Qué:** módulos, paquetes, clases, métodos y variables en español (`ajedrez-nucleo`, `Tablero`,
  `jugadasCandidatas`). El roadmap proponía inglés; el grupo decidió no seguirlo.
- **Por qué:** el dominio, la consigna, la cursada y la defensa son en español. El código se lee igual
  que como se explica, sin traducir mentalmente `Board` → tablero en la exposición.
- **Cuándo se rompería:** si el proyecto pasara a mantenerlo gente que no habla español, o si se
  publicara como librería para otros.

### 20. Tests sin infraestructura

- **Qué:** los tests del núcleo arman los tableros a mano. Los de la consola le pasan un `Scanner` sobre
  un `String` y un `PrintStream` sobre memoria.
- **Por qué:** la consigna pide que el núcleo se pruebe sin levantar nada, y así cada test muestra
  exactamente la posición que verifica.
- **Cuándo se rompería:** no se rompería para el núcleo. Para la GUI, si se quisieran tests automáticos
  de la ventana, haría falta otra estrategia, porque hoy no tiene tests.
