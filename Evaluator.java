// File: Evaluator.java

import java.io.InputStream;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Scanner;

public class Evaluator {

   public static void main(String[] args) {
      Evaluator evaluator = new Evaluator(new Lexer(System.in));
      evaluator.run();
   }

   private Lexer lexer;
   private LinkedList<Double> stack;
   private HashMap<String, Double> symbols;
   private String target;

   public Evaluator(Lexer lexer) {
      this.lexer = lexer;
      stack = new LinkedList<>();
      symbols = new HashMap<>();
      target = "it";
   }

   public Double evaluate() {
      stack.clear();
      target = "it"; // Default target variable

      int token = lexer.nextToken();
      String real_var = "";
      boolean lastWasOperator = false; // Flag to track if the last token was an operator

      while (token != Lexer.EOL) {
         switch (token) {
            case Lexer.NUMBER:
               if (lastWasOperator) {
                  return error("Operand encountered after an operator");
               }
               // Push the number onto the stack
               stack.push(Double.parseDouble(lexer.getText()));
               lastWasOperator = false; // Reset flag
               break;

            case Lexer.VARIABLE:
               if (lastWasOperator) {
                  return error("Operand encountered after an operator");
               }
               String var = lexer.getText();
               real_var = var;
               if (var.equals("exit")) {
                  // Exit the program if "exit" is encountered
                  System.out.println("Bye");
                  System.exit(0);
               }
               Double check = symbols.get(var);
               if (check == null) {
                  // If the variable is not defined, default to 0.0
                  check = 0.0;
               }
               // Push the value of the variable onto the stack (default to 0.0 if undefined)
               symbols.put(var, check);
               stack.push(check);
               lastWasOperator = false; // Reset flag
               break;

            case Lexer.MINUS_OP:
               // Unary minus: Negate the top value on the stack
               if (stack.isEmpty())
                  return error("No operand for unary minus");
               stack.push(-stack.pop());
               lastWasOperator = true; // Set flag
               break;

            case Lexer.ADD_OP:
            case Lexer.SUBTRACT_OP:
            case Lexer.MULTIPLY_OP:
            case Lexer.DIVIDE_OP:
               // Binary operations: Pop two values, perform the operation, and push the result
               if (stack.size() < 2)
                  return error("Too few operands for binary operator");
               double b = stack.pop();
               double a = stack.pop();
               switch (token) {
                  case Lexer.ADD_OP:
                     stack.push(a + b);
                     break;
                  case Lexer.SUBTRACT_OP:
                     stack.push(a - b);
                     break;
                  case Lexer.MULTIPLY_OP:
                     stack.push(a * b);
                     break;
                  case Lexer.DIVIDE_OP:
                     if (b == 0)
                        return error("Division by zero");
                     stack.push(a / b);
                     break;
               }
               lastWasOperator = true; // Set flag
               break;

            case Lexer.ASSIGN_OP:
               // Assignment: Pop a value and assign it to a variable
               if (stack.isEmpty())
                  return error("No value to assign");

               double value = stack.pop();
               token = lexer.nextToken();

               if (token != Lexer.NUMBER && token != Lexer.VARIABLE)
                  return error("Expected variable after '='");

               String variable = lexer.getText();
               symbols.put(real_var, new Double (variable)); // Store the value in the hashmap
               target = variable; // Update the targetnew Double (variable))ble
               stack.push(new Double (variable));
               break;

            case Lexer.BAD_TOKEN:
               // Handle invalid tokens
               return error("Bad token: " + lexer.getText());

            default:
               // Handle unexpected tokens
               return error("Unexpected token");
         }
         token = lexer.nextToken(); // Get the next token
      }

      // At the end of the line, ensure the stack has exactly one value
      if (stack.size() != 1)
         return error("Malformed expression");

      Double result = stack.pop(); // Get the result
      symbols.put(target, result); // Store the result in the target variable
      return result; // Return the result
   }

   private Double error(String msg) {
      System.out.println(msg);
      String line = lexer.getCurrentLine();
      int index = lexer.getCurrentChar();
      System.out.print(line);
      for (int i = 1; i < index; i++)
         System.out.print(' ');
      System.out.println("^");
      lexer.flush();
      return null;
   }

   public void run() {
      while (true) {
         Double value = evaluate();
         if (value == null)
            System.out.println("no value");
         else
            System.out.println(value);
      }
   }

   public static class Lexer {

      public static final int ADD_OP = 3;
      public static final int SUBTRACT_OP = 4;
      public static final int MULTIPLY_OP = 5;
      public static final int DIVIDE_OP = 6;
      public static final int MINUS_OP = 7;
      public static final int ASSIGN_OP = 8;
      public static final int EOL = 9;
      public static final int NUMBER = 11;
      public static final int VARIABLE = 12;
      public static final int BAD_TOKEN = 100;

      private Scanner input;
      private String line;
      private int index;
      private String text;

      public Lexer(InputStream in) {
         input = new Scanner(in);
         line = "";
         index = 0;
         text = "";
      }

      private char nextChar() {
         if (index == line.length()) {
            System.out.print(">> ");
            if (input.hasNextLine()) {
               line = input.nextLine() + "\n";
               index = 0;
            } else {
               System.out.println("\nBye");
               System.exit(0);
            }
         }
         return line.charAt(index++);
      }

      public void unread() {
         index -= 1;
      }

      public int nextToken() {
         text = "";
         char ch;
         do {
            ch = nextChar();
         } while (ch == ' ' || ch == '\t');

         if (ch == '\n')
            return EOL;
         text += ch;

         if (Character.isDigit(ch)) {
            while (true) {
               ch = nextChar();
               if (Character.isDigit(ch) || ch == '.') {
                  text += ch;
               } else {
                  unread();
                  break;
               }
            }
            return NUMBER;
         } else if (Character.isLetter(ch) || ch == '_') {
            while (true) {
               ch = nextChar();
               if (Character.isLetterOrDigit(ch) || ch == '_') {
                  text += ch;
               } else {
                  unread();
                  break;
               }
            }
            return VARIABLE;
         } else {
            switch (ch) {
               case '+':
                  return ADD_OP;
               case '-':
                  return SUBTRACT_OP;
               case '*':
                  return MULTIPLY_OP;
               case '/':
                  return DIVIDE_OP;
               case '~':
                  return MINUS_OP;
               case '=':
                  return ASSIGN_OP;
               default:
                  return BAD_TOKEN;
            }
         }
      }

      public String getCurrentLine() {
         return line;
      }

      public int getCurrentChar() {
         return index;
      }

      public String getText() {
         return text;
      }

      public void flush() {
         index = line.length();
      }
   }
} // End of Evaluator.java
