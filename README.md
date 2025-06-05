# 👑 Angry Queens

A puzzle game inspired by the classic N-Queens challenge!  
Your goal? Place 👸 queens on an n×n chessboard without them threatening each other. Easy... right?

---

## 🎥 Demo

📽️ [Live demo on Appetize.io](https://appetize.io/app/b_moocpuinu7xsebe5cc27rapeoq)  
No login required. Click "Tap to Play" to launch the app in-browser.


📽️ [Watch the demo](https://youtube.com/shorts/i2Oe9QPeb6k)

--

## 🧩 Features

- 🧠 Interactive n×n chessboard (4 ≤ n)
- 👆 Tap to place/remove queens
- 🔥 Real-time conflict highlighting (optional)
- 🏆 Win and Loss detection 
- 😂 Snarky commentary as you play

---

## 🧪 Tech & Architecture

- 🛠 Kotlin + Jetpack Compose
- 🔄 Unidirectional state flow via MVI (model view intent) pattern
- 💡 Clean architecture with a testable logic core
- 🧪 Tested with AssertK 🤝 Turbine

---

## 🤖🧠Use of artificial intelligence and other resources

This is grade A, organic code 🍓... Mostly. Any code where I used AI is commented as such.

The game logic in `QueensProblemChessGame` class I coded myself with no assistance. Same for the
ViewModel, the implementation of the MVI pattern, and most of the UI.

Use of ChatGPT:
- Generation of all the UI preview code.
- Help with the animating chat bubble.
- Tried to use it for unit tests but it did a terrible job. Exception being generating threatened positions in `QueensProblemChessGameTest`.
- The fun messages displayed are mostly ChatGPT. I tried. It was funnier.
- 50/50 on this README.md

Use of public domain resources:
- The chess queen image I had nothing to do with. I used a public domain SVG, converted that to Compose, and modified it slightly. Documented in `ChessQueenImageVector.kt`

## 📦 How to Build / Run

- Built with Android Studio Meerkat Feature Drop | 2024.3.2 Patch 1
- Tested on Android emulator API 36

There are no special steps required to run. Simply open the project in Android Studio and run.

---
