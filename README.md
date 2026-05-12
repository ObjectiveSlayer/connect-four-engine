# Connect Four Engine 🔴🟡

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![AI](https://img.shields.io/badge/AI-Minimax_w/_Alpha--Beta-0ea5e9?style=for-the-badge)
![Vanilla JS](https://img.shields.io/badge/Vanilla_JS-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

I built this project to dive deep into game theory, algorithm optimization, and full-stack architecture. What started as a terminal-based Connect Four game is now a fully headless, mathematically rigorous AI engine running behind a local web server, complete with a modern web client that is constantly getting improved.

The engine looks 10 moves into the future, evaluating millions of possible timelines in under 50 milliseconds using Alpha-Beta pruning.

## Features
- **Optimized Engine:** Custom Minimax algorithm heavily optimized for speed.
- **Stateless Architecture:** The engine doesn't remember the game state; it just evaluates whatever snapshot it's handed.
- **Local Web Server:** Built entirely on Java's native `HttpServer` (no Spring Boot or heavy dependencies required).
- **Cinematic UI:** A dark-mode, physics-driven frontend built with pure HTML/CSS/JS.
- **Terminal & API Modes:** Play directly in the console, or spin up the server to play in the browser.

## 🛠️ How to Run
1. Clone the repo and navigate to the project root.
2. Compile the project (via Maven or standard `javac`).
3. Run `Main.java`. 
4. The terminal will greet you with a menu. Select **Option 3: Launch Local Web Server**.
5. Open your browser and navigate to `http://localhost:8080`.

## 🔌 Using the Engine as an API (Custom GUIs)
Because the backend and frontend are completely decoupled, you can easily rip out my UI and build your own in React, Python, Unity, or whatever else you want. 

When the server is running, you just send an HTTP GET request to the engine with a snapshot of your board.

**Endpoint:**
`GET http://localhost:8080/api/move?snapshot=[YOUR_SNAPSHOT]`

**The Snapshot Format:**
The board is a 6x7 grid represented by 6 rows, separated by `/`. 
- `0` = Empty
- `1` = Player 1 (Usually Red)
- `2` = Player 2 (Usually Yellow)

Example Request:
`http://localhost:8080/api/move?snapshot=0000000/0000000/0000000/0000000/0000000/0012000`

The engine will auto-detect whose turn it is, calculate the optimal mathematical move, and return a simple JSON response:
```json
{
  "move": 1
}
```

## 🗺️ Future plans
The math works and the UI is dialed in. Next up is adding data persistence and player profiling to turn this into a proper training tool.

- [ ] **Database Integration:** Hook up an embedded SQLite database to log every game, move, and outcome locally.
- [ ] **Player Elo Rating:** Implement a chess-style rating system to track skill progression against the engine.
- [ ] **Win/Loss Tracking:** A persistent dashboard to track your overall record.
- [ ] **Dynamic Difficulty:** Instead of always playing at Depth 10 (perfect play), the engine will automatically classify the human's skill level and artificially limit its search depth or inject blunders to match the human's rating.
