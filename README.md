# Pong

A fully playable Pong game built in **Java** with Swing, featuring local two-player multiplayer and a single-player mode against a predictive AI that simulates the ball's full trajectory — including wall bounces — to anticipate where it will land.

https://github.com/user-attachments/assets/54ef0b10-a559-49a8-9805-efe1b83df394

## Features

- **Single-player mode** — AI that simulates the ball's full trajectory (including wall bounces) to predict its landing position, with a speed cap and randomised error margin so it's challenging but beatable
- **Two-player local multiplayer** — both players share the keyboard
- **Physics-based ball mechanics** — bounce angle depends on where the ball strikes the paddle; ball speed increases 5% per volley up to a maximum, rewarding longer rallies
- **Clean menu UI** with keyboard navigation

## How It Works

The game runs on a `javax.swing.Timer` firing at 60 FPS on the Event Dispatch Thread, so updates and repaints stay smooth without ever blocking the EDT with `Thread.sleep`.

Ball physics are vector-based: each return angle is computed from the hit position relative to the paddle centre (`rel × 60°`), so edge hits produce steep angles and centre hits produce flat returns, with the velocity decomposed via `cos`/`sin`. Speed builds 5% per volley up to a hard cap (`MAX_SPEED`), making long rallies progressively harder to control.

The AI opponent projects where the ball will land by running a frame-by-frame physics simulation (`predictBallY`) that mirrors the wall-bounce logic. To stay beatable, it recomputes its target only every ~15 frames, applies a ±25px position error, moves slightly slower than the player, and drifts back to centre when the ball is travelling away — so it can be consistently beaten by pushing the ball into the corners at high speed.

## Skills Demonstrated

- Object-oriented design — game state, rendering, and input separated across classes
- Event-driven programming — Java Swing event dispatch and key handling
- Game loop architecture — `javax.swing.Timer` at 60 FPS, no blocking on the EDT
- Game physics — vector velocity, trigonometric bounce angles, per-volley speed ramping
- Collision detection — ball-versus-paddle and ball-versus-wall response
- Game AI — predictive opponent via frame-by-frame trajectory simulation
- Vector math & trigonometry — `cos`/`sin` angle decomposition from paddle hit position
- 2D rendering — custom `Graphics` drawing of paddles, ball, scores, and menu
- Input handling — multi-key state tracking and keyboard menu navigation
- Java Platform Module System — modular build with `module-info.java`
- JAR packaging — runnable modular Java application

## Tech Stack

- Java 17+
- Java Swing / AWT (`JPanel`, `JFrame`, `Graphics`, `javax.swing.Timer`)
- Java Platform Module System (`module-info.java`, `requires java.desktop`)
- Packaged as a runnable modular JAR (`PONG.jar`)

## Getting Started

Requires Java 11 or later.

```bash
java -jar PONG.jar
```

### Build from source

```bash
javac -d out src/module-info.java src/PONG/PONG.java src/PONG/keyListener.java
jar --create --file=PONG.jar --main-class=PONG.PONG -C out .
```

### Controls

| Action | Player 1 | Player 2 / Menu |
|--------|----------|-----------------|
| Move paddle | `W` / `S` | `↑` / `↓` |
| Serve | `Space` | `Space` |
| Select mode | — | `1` / `2` |
| Back to menu | `Esc` | `Esc` |
| Quit | `Esc` (from menu) | — |
