# TPO Ajedrez — reglas del proyecto

Ajedrez en Java 21 con arquitectura núcleo + adaptadores. Módulos Maven: `ajedrez-nucleo`,
`ajedrez-consola`, `ajedrez-gui-swing` y `ajedrez-app`. El README explica la arquitectura,
`docs/decisiones.md` el porqué de cada decisión y `docs/estado.html` hasta dónde llegamos.

```bash
mvn test                                         # todos los tests
mvn package && java -jar ajedrez-app/target/ajedrez.jar [--gui]
```

## Arquitectura
- `ajedrez-nucleo` es el núcleo: Java puro, sin dependencias (solo JUnit en scope test).
- Prohibido en el núcleo: `System.out`, `Scanner`, `javax.swing`, `java.awt`, archivos, red.
- `ajedrez-consola` y `ajedrez-gui-swing` son adaptadores: solo usan el puerto `JuegoDeAjedrez` y los
  datos que devuelve (`modelo`, `ResultadoJugada`). Nunca importan `reglas`, `movimiento` ni `configuracion`.
- Ninguna regla del ajedrez vive en un adaptador.

## Diseño
- Ninguna clase hace `new` de sus colaboradores: los recibe por constructor en campos `final`.
  La única excepción es `Principal` (raíz de composición).
- Las piezas NO heredan entre sí: cada `TipoDePieza` se compone de `ReglaDeMovimiento`.
- Prohibido `switch` o `instanceof` sobre tipos de pieza.
- Las reglas de movimiento nunca generan jugadas sobre piezas propias.
- `Tablero` es inmutable: `con`/`sin` devuelven un `Tablero` nuevo.
- Nada de literales `8`: el tablero conoce su `ancho` y su `alto`. El único 8 está en `TableroEstandar`.
- El doble paso del peón depende de `yaSeMovio`, nunca de un número de fila.
- No agregar una interfaz si va a tener una sola implementación y ningún consumidor polimórfico.

## Convenciones
- Todo en español: módulos, paquetes, clases, métodos, variables, comentarios y documentación.
- Tests: JUnit 5, nombre `sujeto_condicion_resultadoEsperado`, bloques `// Arrange`, `// Act`,
  `// Assert` comentados (`// Act & Assert` si el test es de una línea).
- Si cambia una firma pública, se actualiza `docs/uml/*.puml` en el MISMO commit.
- Si se toma una decisión de diseño, se agrega una entrada a `docs/decisiones.md`
  (qué / por qué / cuándo se rompería).
- Nunca pushear con tests en rojo: el CI (`.github/workflows/ci.yml`) corre `mvn test` en cada push.
- Cuando se termina un avance (una fase, un opcional, un entregable), se actualiza `docs/estado.html`:
  la fecha de "Última actualización", las tablas que cambiaron y una entrada nueva arriba de todo
  en el historial.

## Documentación
Toda vive en `docs/`: `estado.html` (avance), `decisiones.md`, `uml/`, `explicaciones/` (HTML para
leer el diseño) y `referencia/` (consigna y roadmap en PDF).
