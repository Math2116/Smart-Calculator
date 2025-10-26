// Save as CalculatorApp.java
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CalculatorApp extends Application {
    private TextField display;
    private BigDecimal firstOperand = null;
    private String operator = null;
    private boolean startNewNumber = true;
    private final MathContext mc = new MathContext(16, RoundingMode.HALF_UP);

    @Override
    public void start(Stage primaryStage) {
        display = new TextField("0");
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setPrefHeight(60);
        display.setStyle("-fx-font-size: 20;");

        GridPane buttons = createButtonGrid();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setTop(display);
        BorderPane.setMargin(display, new Insets(0, 0, 12, 0));
        root.setCenter(buttons);

        Scene scene = new Scene(root, 320, 420);

        // Keyboard support (basic)
        scene.setOnKeyPressed(e -> {
            if (e.getCode().isDigitKey()) appendNumber(e.getText());
            else if (e.getCode() == KeyCode.PERIOD || e.getText().equals(".")) appendDecimalPoint();
            else if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.EQUALS) calculateResult();
            else if (e.getCode() == KeyCode.BACK_SPACE) backspace();
            else if (e.getText().equals("+") || e.getText().equals("-") || e.getText().equals("*") || e.getText().equals("/"))
                applyOperator(e.getText());
        });

        primaryStage.setTitle("Calculator (JavaFX)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createButtonGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        String[][] layout = {
                {"C", "⌫", "%", "/"},
                {"7", "8", "9", "*"},
                {"4", "5", "6", "-"},
                {"1", "2", "3", "+"},
                {"±", "0", ".", "="}
        };

        for (int r = 0; r < layout.length; r++) {
            for (int c = 0; c < layout[r].length; c++) {
                String text = layout[r][c];
                Button btn = new Button(text);
                btn.setPrefSize(64, 56);
                btn.setStyle("-fx-font-size: 16;");
                btn.setOnAction(e -> handleButton(text));
                GridPane.setConstraints(btn, c, r);
                grid.getChildren().add(btn);
            }
        }
        return grid;
    }

    private void handleButton(String text) {
        switch (text) {
            case "C":
                clearAll();
                break;
            case "⌫":
                backspace();
                break;
            case "%":
                applyPercent();
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                applyOperator(text);
                break;
            case "=":
                calculateResult();
                break;
            case "±":
                toggleSign();
                break;
            case ".":
                appendDecimalPoint();
                break;
            default: // digits
                appendNumber(text);
        }
    }

    private void appendNumber(String digit) {
        if (startNewNumber) {
            display.setText(digit.equals("0") ? "0" : digit);
            startNewNumber = digit.equals("0"); // if pressing 0 first keep 0
            if (!digit.equals("0")) startNewNumber = false;
        } else {
            String cur = display.getText();
            if (cur.equals("0")) display.setText(digit);
            else display.setText(cur + digit);
        }
    }

    private void appendDecimalPoint() {
        if (startNewNumber) {
            display.setText("0.");
            startNewNumber = false;
            return;
        }
        if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void backspace() {
        if (startNewNumber) {
            display.setText("0");
            startNewNumber = true;
            return;
        }
        String cur = display.getText();
        if (cur.length() <= 1) {
            display.setText("0");
            startNewNumber = true;
        } else {
            display.setText(cur.substring(0, cur.length() - 1));
        }
    }

    private void clearAll() {
        display.setText("0");
        firstOperand = null;
        operator = null;
        startNewNumber = true;
    }

    private void toggleSign() {
        String cur = display.getText();
        if (cur.equals("0")) return;
        if (cur.startsWith("-")) display.setText(cur.substring(1));
        else display.setText("-" + cur);
    }

    private void applyPercent() {
        try {
            BigDecimal value = new BigDecimal(display.getText(), mc);
            BigDecimal result = value.divide(BigDecimal.valueOf(100), mc);
            display.setText(stripTrailingZeros(result));
            startNewNumber = true;
        } catch (Exception ex) {
            display.setText("Error");
            startNewNumber = true;
        }
    }

    private void applyOperator(String op) {
        try {
            BigDecimal displayed = new BigDecimal(display.getText(), mc);
            if (firstOperand == null) {
                firstOperand = displayed;
            } else if (operator != null && !startNewNumber) {
                firstOperand = compute(firstOperand, displayed, operator);
                display.setText(stripTrailingZeros(firstOperand));
            }
            operator = op;
            startNewNumber = true;
        } catch (Exception ex) {
            display.setText("Error");
            startNewNumber = true;
            firstOperand = null;
            operator = null;
        }
    }

    private void calculateResult() {
        if (operator == null || firstOperand == null) return;
        try {
            BigDecimal secondOperand = new BigDecimal(display.getText(), mc);
            BigDecimal result = compute(firstOperand, secondOperand, operator);
            display.setText(stripTrailingZeros(result));
            firstOperand = null;
            operator = null;
            startNewNumber = true;
        } catch (ArithmeticException ae) {
            display.setText("Error");
            firstOperand = null;
            operator = null;
            startNewNumber = true;
        } catch (Exception ex) {
            display.setText("Error");
            firstOperand = null;
            operator = null;
            startNewNumber = true;
        }
    }

    private BigDecimal compute(BigDecimal a, BigDecimal b, String op) {
        switch (op) {
            case "+":
                return a.add(b, mc);
            case "-":
                return a.subtract(b, mc);
            case "*":
                return a.multiply(b, mc);
            case "/":
                if (b.compareTo(BigDecimal.ZERO) == 0)
                    throw new ArithmeticException("Division by zero");
                // set scale for division safely
                return a.divide(b, mc.getPrecision(), RoundingMode.HALF_UP);
            default:
                throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }

    private String stripTrailingZeros(BigDecimal bd) {
        bd = bd.stripTrailingZeros();
        // BigDecimal.toPlainString avoids scientific notation
        String s = bd.toPlainString();
        // For values like "-0", return "0"
        if (s.equals("-0")) return "0";
        return s;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
