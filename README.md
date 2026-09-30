# Ajedrez — TPO Ingeniería de Software

Ajedrez en Java 21 con arquitectura **núcleo + adaptadores**. Las reglas del juego viven en un solo
módulo que no sabe nada de pantallas ni teclados. La consola y la ventana Swing se "enchufan" a ese
núcleo por una única interfaz.

## Cómo correrlo

```bash
mvn package                                      # compila, corre los tests y arma el jar
java -jar ajedrez-app/target/ajedrez.jar         # consola: escribí "e2 e4"; "salir" para terminar
java -jar ajedrez-app/target/ajedrez.jar --gui   # ventana: clic en la pieza y clic en el destino
mvn test                                         # solo los tests
```

## La arquitectura en un dibujo

```
                         ajedrez-app
              Principal: crea todo y lo conecta
                │                 │          │
                ▼                 ▼          │
         ajedrez-consola  ajedrez-gui-swing  │
           (adaptador)        (adaptador)    │
                │                 │          │
                └──── usan ───────┤          │
                                  ▼          ▼
              ajedrez-nucleo  (Java puro, sin dependencias)
                JuegoDeAjedrez  ← puerto de entrada
                Partida · reglas · piezas · tablero
```

**Regla de oro:** las flechas siempre apuntan hacia el núcleo. El `pom.xml` del núcleo no tiene
dependencias (solo JUnit para los tests), así que el compilador no le deja importar Swing ni la consola.
Los adaptadores solo traducen: leen lo que hace el usuario, llaman a `JuegoDeAjedrez` y muestran la
respuesta. **Ninguna regla del ajedrez vive en un adaptador.**

## Qué hace cada paquete

| Módulo › paquete | Qué hace | Clases principales |
|---|---|---|
| núcleo › `modelo` | Las "cosas" del juego | `Tablero` (inmutable), `Pieza`, `TipoDePieza`, `Posicion`, `Direccion`, `Color` |
| núcleo › `movimiento` | Cómo se mueve cada pieza | `ReglaDeMovimiento` y sus 4 implementaciones, `Jugada`, `JugadaSimple` |
| núcleo › `configuracion` | Arma el ajedrez clásico | `TiposDePiezaEstandar` (las 6 piezas), `TableroEstandar` (posición inicial) |
| núcleo › `reglas` | Qué jugadas se permiten | `ValidadorDeJugadas`, `ReglaPiezaPropia`, `ReglaNoDejarReyEnJaque`, `DetectorDeJaque` |
| núcleo › `juego` | La puerta de entrada al núcleo | `JuegoDeAjedrez` (puerto), `Partida`, `ResultadoJugada` |
| consola › `consola` | Jugar escribiendo `e2 e4` | `AdaptadorConsola`, `TraductorDeNotacion`, `DibujanteAscii` |
| gui › `gui` | Jugar con clics | `VentanaSwing`, `GlifosDePieza` |
| app › `app` | Arranque (raíz de composición) | `Principal` |

## Las piezas no son clases: son listas de reglas

No existe `class Torre` ni `class Dama`. Cada pieza es un `TipoDePieza` con un nombre y una lista de
`ReglaDeMovimiento` (patrón Strategy). Así se arman:

| Pieza | Reglas |
|---|---|
| Torre | deslizarse en línea recta, sin límite |
| Alfil | deslizarse en diagonal, sin límite |
| Dama | **las dos anteriores juntas** |
| Rey | deslizarse un solo paso en cualquier dirección |
| Caballo | saltar en L |
| Peón | avanzar (dos casillas si todavía no se movió) + capturar en diagonal |

Una pieza nueva no toca código existente: es un `new TipoDePieza("canciller", false, List.of(regla1, regla2))`.

## Qué pasa cuando alguien juega `e2 e4`

1. **Consola:** `TraductorDeNotacion` convierte `"e2"` y `"e4"` en `Posicion(4, 1)` y `Posicion(4, 3)`.
   `AdaptadorConsola` llama a `juego.jugar(desde, hasta)` y no conoce nada más del núcleo que esa interfaz.
2. **Partida** busca la pieza en e2 y le pide sus `jugadasCandidatas`: cada regla de su tipo propone
   destinos. Si ninguno es e4, la jugada se rechaza.
3. **ValidadorDeJugadas** pasa la jugada por su lista de reglas: ¿la pieza es del jugador de turno?
   ¿deja a su propio rey en jaque? Para lo segundo aplica la jugada, obtiene un tablero *nuevo*
   (el original no cambia) y le pregunta al `DetectorDeJaque`.
4. Si pasa, `jugada.aplicarEn(tablero)` da el tablero nuevo, cambia el turno y el detector se fija si
   el rival quedó en jaque.
5. Devuelve un `ResultadoJugada` (si fue válida, el motivo si no lo fue, quién quedó en jaque).
   La consola lo imprime y la ventana lo muestra abajo.

## Decisiones de diseño, en una línea cada una

- **Núcleo sin dependencias:** se prueba con tests comunes, sin levantar consola ni ventana.
- **Tablero inmutable:** `con()` y `sin()` devuelven un tablero nuevo, así se puede *probar* una jugada
  (¿deja al rey en jaque?) sin romper nada.
- **Inyección por constructor:** ninguna clase hace `new` de sus colaboradores; solo `Principal`.
- **Abierto/cerrado:** pieza nueva = otro `TipoDePieza`. Regla nueva = otra clase en la lista del
  validador. Interfaz nueva (web, IA) = otro adaptador que use `JuegoDeAjedrez`.
- **Nada de `8` suelto:** el tamaño lo sabe el tablero. Hay un test que juega en 10x10.

Todavía no está hecho el alcance opcional: jaque mate, enroque, captura al paso, promoción y tablas.

## Para profundizar (orden de lectura sugerido)

1. [JuegoDeAjedrez](ajedrez-nucleo/src/main/java/juego/JuegoDeAjedrez.java): lo único que el núcleo ofrece hacia afuera.
2. [Partida](ajedrez-nucleo/src/main/java/juego/Partida.java): el recorrido de arriba, en 4 pasos comentados.
3. [TiposDePiezaEstandar](ajedrez-nucleo/src/main/java/configuracion/TiposDePiezaEstandar.java): cómo se arman las piezas.
4. [ReglaDeslizante](ajedrez-nucleo/src/main/java/movimiento/ReglaDeslizante.java) y [ReglaAvanceDePeon](ajedrez-nucleo/src/main/java/movimiento/ReglaAvanceDePeon.java): cómo se generan las jugadas.
5. [ReglaNoDejarReyEnJaque](ajedrez-nucleo/src/main/java/reglas/ReglaNoDejarReyEnJaque.java) y [DetectorDeJaque](ajedrez-nucleo/src/main/java/reglas/DetectorDeJaque.java): el jaque.
6. [Principal](ajedrez-app/src/main/java/app/Principal.java): cómo se conecta todo.
7. Tests: [PartidaTest](ajedrez-nucleo/src/test/java/juego/PartidaTest.java) y [ExtensibilidadTest](ajedrez-nucleo/src/test/java/extensibilidad/ExtensibilidadTest.java), que muestra una pieza nueva y un tablero de 10x10 sin tocar el núcleo.

Los diagramas de clases están en [uml/](uml/).
