package com.nigel_karunaratne;

import com.nigel_karunaratne.lexer.*;

//* This is the entry point for the interpreter command line. It is temporary
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println('z' - 'z');
        System.out.println("Interpreter started.");

        // String inputString = "var variable = 1 + 3 / 4;\nvar stringVar123 = \"Hello World! 123454321;\";   \nvar a = \"oonga\";";
        String inputString = "var x = 3 + 2 / 4 * 5 % 2;\nvar y = \"Hello World\";\nif(x >= 3) y = \"hello worl\";";

        System.out.println("Lexing: \n" + inputString + "\n");

        LexerInstance lexer = new LexerInstance();
        lexer.LexInput(inputString);
    }
}
