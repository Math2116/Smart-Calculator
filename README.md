

## 🧮 Smart Calculator (JavaFX)

A simple and modern calculator built using **JavaFX**.
It supports basic arithmetic operations, percentage, sign toggle, and keyboard input — all with high precision using **BigDecimal**.

---

### ✨ Features

* Basic operations: `+`, `-`, `*`, `/`
* Percentage (`%`), sign toggle (`±`), clear (`C`), backspace (`⌫`)
* Keyboard input support
* Clean and responsive JavaFX UI
* High precision calculations

---

### ⚙️ How to Run

1. Ensure **Java 11+** and **JavaFX SDK** are installed.
2. Compile and run:

   ```bash
   javac --module-path /path/to/javafx/lib --add-modules javafx.controls CalculatorApp.java
   java --module-path /path/to/javafx/lib --add-modules javafx.controls CalculatorApp
   ```

---
 Highlights

* Built with `BigDecimal` for precise math
* Clean UI using `GridPane` and `BorderPane`
* Handles division by zero and invalid input gracefully

