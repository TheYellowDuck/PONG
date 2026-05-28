# Pong

A fully playable Pong game built in Java with Swing, featuring local two-player multiplayer and a single-player mode against a predictive AI opponent.

## Features

- **Single-player mode** — AI that simulates the ball's full trajectory (including wall bounces) to predict landing position, with a speed cap and randomised error margin so it's challenging but beatable
- **Two-player local multiplayer** — both players share the keyboard
- **Physics-based ball mechanics** — bounce angle depends on where the ball strikes the paddle; ball speed increases 5% per volley up to a maximum, rewarding longer rallies
- **Clean menu UI** with keyboard navigation

## Gameplay

The ball angle on each return is determined by the hit position relative to paddle center — edge hits produce steep angles, center hits produce flat returns. Speed builds each volley, making long rallies progressively harder to control.

The AI uses frame-by-frame physics simulation to project where the ball will land, but is limited to a movement speed slightly below the player's and applies a small position error, meaning it can be beaten consistently by pushing the ball to extreme corners at high speeds.

## Controls

| Action | Player 1 | Player 2 / Menu |
|--------|----------|-----------------|
| Move paddle | `W` / `S` | `↑` / `↓` |
| Serve | `Space` | `Space` |
| Select mode | — | `1` / `2` |
| Back to menu | `Esc` | `Esc` |
| Quit | `Esc` (from menu) | — |

## Running

Requires Java 11 or later.

```
java -jar PONG.jar
```

## Technical Details

- **Language / UI:** Java 17+, Java Swing / AWT
- **Game loop:** `javax.swing.Timer` firing at 60 FPS on the Event Dispatch Thread — no blocking `Thread.sleep` on the EDT
- **AI:** per-frame ball-path simulation with configurable imperfection; target position is refreshed every ~15 frames to avoid pixel-perfect tracking
- **Build:** single modular JAR (`module-info.java`, `requires java.desktop`)

## Build from Source

```bash
javac -d out src/module-info.java src/PONG/PONG.java src/PONG/keyListener.java
jar --create --file=PONG.jar --main-class=PONG.PONG -C out .
```
