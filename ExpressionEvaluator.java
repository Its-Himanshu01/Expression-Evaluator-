import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class ExpressionEvaluator extends JFrame implements ActionListener {
    JTextField infixField, prefixField, postfixField, outputField;
    JButton infixToPrefixBtn, infixToPostfixBtn, infixEvalBtn;
    JButton prefixToInfixBtn, prefixToPostfixBtn;
    JButton postfixToInfixBtn, postfixToPrefixBtn;
    JButton clearBtn;
    JLabel infixLabel, prefixLabel, postfixLabel, outputLabel;

    public ExpressionEvaluator() {
        setTitle("Expression Evaluator");
        setLayout(null);

        // Infix Section
        infixLabel = new JLabel("Enter Expression:");
        infixLabel.setBounds(30, 30, 140, 30);
        add(infixLabel);

        infixField = new JTextField();
        infixField.setBounds(180, 30, 220, 30);
        add(infixField);

        infixToPrefixBtn = new JButton("Infix → Prefix");
        infixToPrefixBtn.setBounds(420, 30, 120, 30);
        add(infixToPrefixBtn);

        infixToPostfixBtn = new JButton("Infix → Postfix");
        infixToPostfixBtn.setBounds(550, 30, 120, 30);
        add(infixToPostfixBtn);

        infixEvalBtn = new JButton("Evaluate");
        infixEvalBtn.setBounds(680, 30, 100, 30);
        add(infixEvalBtn);

        // Prefix Section
        prefixLabel = new JLabel("Enter Prefix Expression:");
        prefixLabel.setBounds(30, 80, 160, 30);
        add(prefixLabel);

        prefixField = new JTextField();
        prefixField.setBounds(180, 80, 220, 30);
        add(prefixField);

        prefixToInfixBtn = new JButton("Prefix → Infix");
        prefixToInfixBtn.setBounds(420, 80, 120, 30);
        add(prefixToInfixBtn);

        prefixToPostfixBtn = new JButton("Prefix → Postfix");
        prefixToPostfixBtn.setBounds(550, 80, 120, 30);
        add(prefixToPostfixBtn);

        // Postfix Section
        postfixLabel = new JLabel("Enter Postfix Expression:");
        postfixLabel.setBounds(30, 130, 160, 30);
        add(postfixLabel);

        postfixField = new JTextField();
        postfixField.setBounds(180, 130, 220, 30);
        add(postfixField);

        postfixToInfixBtn = new JButton("Postfix → Infix");
        postfixToInfixBtn.setBounds(420, 130, 120, 30);
        add(postfixToInfixBtn);

        postfixToPrefixBtn = new JButton("Postfix → Prefix");
        postfixToPrefixBtn.setBounds(550, 130, 120, 30);
        add(postfixToPrefixBtn);

        // Output Section
        outputLabel = new JLabel("Output Expression:");
        outputLabel.setBounds(30, 190, 140, 30);
        add(outputLabel);

        outputField = new JTextField();
        outputField.setBounds(180, 190, 340, 30);
        outputField.setEditable(false);
        add(outputField);

        clearBtn = new JButton("Clear");
        clearBtn.setBounds(550, 190, 120, 30);
        add(clearBtn);

        // Action Listeners
        infixToPrefixBtn.addActionListener(this);
        infixToPostfixBtn.addActionListener(this);
        infixEvalBtn.addActionListener(this);
        prefixToInfixBtn.addActionListener(this);
        prefixToPostfixBtn.addActionListener(this);
        postfixToInfixBtn.addActionListener(this);
        postfixToPrefixBtn.addActionListener(this);
        clearBtn.addActionListener(this);

        setSize(820, 290);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String result = "";

        if (e.getSource() == infixToPrefixBtn) {
            String expr = infixField.getText().replaceAll("\\s+", "");
            result = infixToPrefix(expr);
        } else if (e.getSource() == infixToPostfixBtn) {
            String expr = infixField.getText().replaceAll("\\s+", "");
            result = infixToPostfix(expr);
        } else if (e.getSource() == infixEvalBtn) {
            String expr = infixField.getText().replaceAll("\\s+", "");
            try {
                result = String.valueOf(evaluateInfix(expr));
            } catch (Exception ex) {
                result = "Invalid Expression";
            }
        } else if (e.getSource() == prefixToInfixBtn) {
            String expr = prefixField.getText().replaceAll("\\s+", "");
            result = prefixToInfix(expr);
        } else if (e.getSource() == prefixToPostfixBtn) {
            String expr = prefixField.getText().replaceAll("\\s+", "");
            result = prefixToPostfix(expr);
        } else if (e.getSource() == postfixToInfixBtn) {
            String expr = postfixField.getText().replaceAll("\\s+", "");
            result = postfixToInfix(expr);
        } else if (e.getSource() == postfixToPrefixBtn) {
            String expr = postfixField.getText().replaceAll("\\s+", "");
            result = postfixToPrefix(expr);
        } else if (e.getSource() == clearBtn) {
            infixField.setText("");
            prefixField.setText("");
            postfixField.setText("");
            outputField.setText("");
        }

        outputField.setText(result);
    }

    // Fixed Methods
    String infixToPrefix(String exp) {
        // Step 1: Reverse the infix expression
        StringBuilder sb = new StringBuilder(exp);
        sb.reverse();
        char[] chars = sb.toString().toCharArray();

        // Step 2: Swap '(' and ')'
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '(')
                chars[i] = ')';
            else if (chars[i] == ')')
                chars[i] = '(';
        }

        // Step 3: Get postfix of modified expression
        String postfix = infixToPostfixForPrefix(new String(chars));

        // Step 4: Reverse postfix to get prefix
        return new StringBuilder(postfix).reverse().toString();
    }

    // Helper for correct prefix conversion
    String infixToPostfixForPrefix(String exp) {
        Stack<Character> stack = new Stack<>();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);

            if (Character.isLetterOrDigit(c))
                result.append(c);
            else if (c == '(')
                stack.push(c);
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(')
                    result.append(stack.pop());
                if (!stack.isEmpty())
                    stack.pop();
            } else if (isOperator(c)) {
                // FIXED: use < instead of <= for correct right associativity
                while (!stack.isEmpty() && precedence(c) < precedence(stack.peek()))
                    result.append(stack.pop());
                stack.push(c);
            }
        }

        while (!stack.isEmpty())
            result.append(stack.pop());

        return result.toString();
    }

    // Infix → Postfix (for UI button)
    String infixToPostfix(String exp) {
        Stack<Character> stack = new Stack<>();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (Character.isLetterOrDigit(c))
                result.append(c);
            else if (c == '(')
                stack.push(c);
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(')
                    result.append(stack.pop());
                if (!stack.isEmpty())
                    stack.pop();
            } else if (isOperator(c)) {
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek()))
                    result.append(stack.pop());
                stack.push(c);
            }
        }
        while (!stack.isEmpty())
            result.append(stack.pop());
        return result.toString();
    }

    boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    int precedence(char ch) {
        switch (ch) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
        }
        return -1;
    }

    int evaluateInfix(String exp) {
        Stack<Integer> values = new Stack<>();
        Stack<Character> ops = new Stack<>();
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (c == ' ')
                continue;
            if (Character.isDigit(c)) {
                int num = 0;
                while (i < exp.length() && Character.isDigit(exp.charAt(i))) {
                    num = num * 10 + (exp.charAt(i) - '0');
                    i++;
                }
                i--;
                values.push(num);
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (!ops.isEmpty() && ops.peek() != '(')
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.pop();
            } else if (isOperator(c)) {
                while (!ops.isEmpty() && precedence(c) <= precedence(ops.peek()))
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.push(c);
            }
        }
        while (!ops.isEmpty())
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        return values.pop();
    }

    int applyOp(char op, int b, int a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
            case '^':
                return (int) Math.pow(a, b);
        }
        return 0;
    }

    String postfixToInfix(String exp) {
        Stack<String> stack = new Stack<>();
        for (char c : exp.toCharArray()) {
            if (c == ' ')
                continue;
            if (Character.isLetterOrDigit(c)) {
                stack.push("" + c);
            } else if (isOperator(c)) {
                String op2 = stack.pop();
                String op1 = stack.pop();
                stack.push("(" + op1 + c + op2 + ")");
            }
        }
        return stack.isEmpty() ? "" : stack.pop();
    }

    String prefixToInfix(String exp) {
        Stack<String> stack = new Stack<>();
        for (int i = exp.length() - 1; i >= 0; i--) {
            char c = exp.charAt(i);
            if (c == ' ')
                continue;
            if (Character.isLetterOrDigit(c)) {
                stack.push("" + c);
            } else if (isOperator(c)) {
                String op1 = stack.pop();
                String op2 = stack.pop();
                stack.push("(" + op1 + c + op2 + ")");
            }
        }
        return stack.isEmpty() ? "" : stack.pop();
    }

    String postfixToPrefix(String exp) {
        Stack<String> stack = new Stack<>();
        for (char c : exp.toCharArray()) {
            if (c == ' ')
                continue;
            if (Character.isLetterOrDigit(c))
                stack.push("" + c);
            else if (isOperator(c)) {
                String op2 = stack.pop();
                String op1 = stack.pop();
                stack.push(c + op1 + op2);
            }
        }
        return stack.isEmpty() ? "" : stack.pop();
    }

    String prefixToPostfix(String exp) {
        Stack<String> stack = new Stack<>();
        for (int i = exp.length() - 1; i >= 0; i--) {
            char c = exp.charAt(i);
            if (c == ' ')
                continue;
            if (Character.isLetterOrDigit(c))
                stack.push("" + c);
            else if (isOperator(c)) {
                String op1 = stack.pop();
                String op2 = stack.pop();
                stack.push(op1 + op2 + c);
            }
        }
        return stack.isEmpty() ? "" : stack.pop();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpressionEvaluator::new);
    }
}
