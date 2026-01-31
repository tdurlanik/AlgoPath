# AlgoPath: Pathfinding Visualizer 🚀

**AlgoPath** is an Android application built to visualize how graph algorithms work in real-time.

As a **Mathematical Engineering student**, I wanted to bridge the gap between abstract Graph Theory concepts (like $f(n) = g(n) + h(n)$) and practical software development.
This project helped me understand how pathfinding algorithms behave differently in terms of cost and efficiency.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?style=flat&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-green?style=flat&logo=android)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue)

## 🎯 What I Learned & Implemented

Instead of just following a tutorial, I focused on building a scalable architecture. Key technical achievements include:

* **MVVM Architecture:** Separated the UI (Compose) from the Logic (ViewModel) to keep the code clean.
* **State Management:** Used `StateFlow` and Coroutines to handle grid updates without freezing the UI.
* **Algorithmic Logic:** Implemented BFS, Dijkstra, and A* from scratch to observe their differences.
* **Coordinate Mapping:** Handled touch gestures to map screen pixels to grid coordinates for drawing walls.

## 📱 Features

* **Interactive Grid:** Draw walls and obstacles with touch gestures.
* **Algorithm Selector:** Switch between BFS, Dijkstra, and A*.
* **Real-Time Stats:** See the `Duration` and `Visited Nodes` count to compare efficiency.

## 🛠 Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose
* **Architecture:** MVVM / Clean Architecture Principles

---
Currently maintained by [Tayfur Durlanık]
Open to feedback and contributions!
