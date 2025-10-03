package com.codewithz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

@WebServlet("/phone-calculator")
public class PhoneCalculatorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // --- Calculator Logic ---

    // Simple evaluator for + - * /
    private double evaluate(String expr) {
        // Remove spaces before evaluation
        expr = expr.replaceAll("\\s+", "");

        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            // Handle unary minus (negative numbers)
            if (c == '-' && (i == 0 || !Character.isDigit(expr.charAt(i-1)) && expr.charAt(i-1) != '.')) {
                // Find where the negative number ends
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                i++;

                // Read the number part
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    sb.append(expr.charAt(i));
                    i++;
                }
                i--;
                values.push(Double.parseDouble(sb.toString()));
            }
            // number or decimal
            else if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    sb.append(expr.charAt(i));
                    i++;
                }
                i--;
                values.push(Double.parseDouble(sb.toString()));
            }
            // operator
            else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(c)) {
                    // Check if there are enough values to apply the operator
                    if (values.size() < 2) throw new IllegalArgumentException("Invalid expression format.");
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) {
            if (values.size() < 2) throw new IllegalArgumentException("Invalid expression format.");
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Divide by zero");
                return a / b;
        }
        return 0;
    }

    // --- Servlet Logic ---

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String expr = request.getParameter("expression");
        String btn = request.getParameter("btn");
        if (expr == null) expr = "";

        // 1. Logic to update the expression
        if (btn.equals("C")) {
            expr = "";
        } else if (btn.equals("=")) {
            try {
                double result = evaluate(expr);
                // Clean up trailing .0 for integers
                if (result == Math.floor(result) && !Double.isInfinite(result)) {
                    expr = String.valueOf((long) result);
                } else {
                    expr = String.valueOf(result);
                }
            } catch (Exception e) {
                // Simplified error handling
                expr = "Error";
            }
        } else {
            // Simple validation to prevent multiple operators/decimals
            char lastChar = expr.isEmpty() ? ' ' : expr.charAt(expr.length() - 1);
            boolean isOp = "+-*/.".contains(btn);
            boolean lastIsOp = "+-*/.".indexOf(lastChar) >= 0;

            if (isOp && lastIsOp) {
                // Replace the last operator/decimal with the new one, unless it's a negative sign
                if (btn.equals("-")) {
                    // Allows 5*-3 or 5/-3 but not 5-- or 5*+
                    if (lastChar != '*' && lastChar != '/') {
                        expr = expr.substring(0, expr.length() - 1) + btn;
                    } else {
                        expr += btn;
                    }
                } else if (!btn.equals(".")) {
                    // Replace the last operator with the new one
                    expr = expr.substring(0, expr.length() - 1) + btn;
                } else {
                    // Do nothing if pressing decimal after operator/decimal
                }
            } else {
                expr += btn; // append number/operator
            }
        }

        // 2. Re-render the complete, styled calculator HTML
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // The entire HTML, including the CSS, is outputted here
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <title>Phone Style Calculator</title>");
        out.println("    <style>");
        out.println("        body {");
        out.println("            font-family: Arial, sans-serif;");
        out.println("            display: flex;");
        out.println("            justify-content: center;");
        out.println("            align-items: center;");
        out.println("            height: 100vh;");
        out.println("            background-color: #f4f4f9;");
        out.println("            margin: 0;");
        out.println("        }");
        out.println("        form {");
        out.println("            background-color: #fff;");
        out.println("            padding: 20px;");
        out.println("            border-radius: 10px;");
        out.println("            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);");
        out.println("            text-align: center;");
        out.println("        }");
        out.println("        #display {");
        out.println("            width: 100%;");
        out.println("            padding: 15px;");
        out.println("            font-size: 22px;");
        out.println("            text-align: right;");
        out.println("            margin-bottom: 10px;");
        out.println("            border: 1px solid #ccc;");
        out.println("            border-radius: 8px;");
        out.println("            box-sizing: border-box;");
        out.println("        }");
        out.println("        .buttons {");
        out.println("            display: grid;");
        out.println("            grid-template-columns: repeat(4, 70px);");
        out.println("            gap: 10px;");
        out.println("        }");
        out.println("        button {");
        out.println("            padding: 15px;");
        out.println("            font-size: 18px;");
        out.println("            border: none;");
        out.println("            border-radius: 8px;");
        out.println("            cursor: pointer;");
        out.println("        }");
        out.println("        .op { background-color: #007BFF; color: white; }");
        out.println("        .eq { background-color: #28a745; color: white; grid-column: span 2; }");
        out.println("        .clr { background-color: #dc3545; color: white; grid-column: span 2; }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<form action='phone-calculator' method='post'>");
        out.println("    <input type='text' id='display' name='expression' readonly value='" + expr + "'>");

        out.println("    <div class='buttons'>");
        out.println("        <button type='submit' name='btn' value='7'>7</button>");
        out.println("        <button type='submit' name='btn' value='8'>8</button>");
        out.println("        <button type='submit' name='btn' value='9'>9</button>");
        out.println("        <button type='submit' class='op' name='btn' value='+'>+</button>");

        out.println("        <button type='submit' name='btn' value='4'>4</button>");
        out.println("        <button type='submit' name='btn' value='5'>5</button>");
        out.println("        <button type='submit' name='btn' value='6'>6</button>");
        out.println("        <button type='submit' class='op' name='btn' value='-'>-</button>");

        out.println("        <button type='submit' name='btn' value='1'>1</button>");
        out.println("        <button type='submit' name='btn' value='2'>2</button>");
        out.println("        <button type='submit' name='btn' value='3'>3</button>");
        out.println("        <button type='submit' class='op' name='btn' value='*'>*</button>");

        out.println("        <button type='submit' name='btn' value='0'>0</button>");
        out.println("        <button type='submit' name='btn' value='.'>.</button>");
        out.println("        <button type='submit' class='op' name='btn' value='/'>/</button>");
        out.println("        <button type='submit' class='eq' name='btn' value='='>=</button>");

        out.println("        <button type='submit' class='clr' name='btn' value='C'>C</button>");
        out.println("    </div>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }
}