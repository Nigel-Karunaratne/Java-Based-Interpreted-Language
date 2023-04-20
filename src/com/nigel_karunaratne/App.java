package com.nigel_karunaratne;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.nigel_karunaratne.ast.expressions.ExprNode;
import com.nigel_karunaratne.ast.statements.StmtNode;
import com.nigel_karunaratne.error_handler.ErrorHandler;
import com.nigel_karunaratne.interpreter.Interpreter;
import com.nigel_karunaratne.lexer.Lexer;
import com.nigel_karunaratne.parser.Parser;
import com.nigel_karunaratne.tokens.Token;

//* This is the entry point for the interpreter command line. It is temporary
public class App {

    //flags for REPL
    public static boolean hadError;

    public static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws Exception {
        ErrorHandler.debugOutput("Main App started.");

        if(args.length >= 1) {
            //Interpret a single file
            Lexer lexer = new Lexer();
            lexer.LexInput(args[0]);
            Parser parser = new Parser(lexer.getGeneratedTokens());
            List<StmtNode> stmts = parser.parse();
            interpreter.interpretStmtList(stmts);
            ErrorHandler.debugOutput("Main App ended.");
            System.exit(0);
            return;
        }

        //Run a REPL
        else {
            runRepl();
        }

        //TEMPORARY DEBUG THINGS
        // String inputString = "var variable = 1 + 3 / 4;\nvar stringVar123 = \"Hello World! 123454321;\";   \nvar a = \"oonga\";";
        // String inputString = "var x = 3 + 2 / 4 * 5 % 2;\nvar y = \"Hello World\";\nif(x >= 3) y = \"hello worl\";";
        // String inputString = "(2 + 4 / 3) >= 1 == null;";
        String inputString = "var i = 0; var j = 50; while(i < 5) {i = i + 1; j = j - i;}";

        System.out.println("Lexing: \n" + inputString + "\n");

        Lexer lexer = new Lexer();
        lexer.LexInput(inputString);

        Parser parser = new Parser(lexer.getGeneratedTokens());

        List<StmtNode> stmts = parser.parse();

        interpreter.interpretStmtList(stmts);

        ErrorHandler.debugOutput("Main App ended.");
    }

    static void runRepl() {
        boolean shouldExit = false;
        // Lexer lexer = new Lexer();
        // Parser parser;

        Scanner scanner = new Scanner(System.in);


        while(!shouldExit) {
            System.out.print(">>> ");
            String input = scanner.nextLine();

            if(input == null) 
                break; //TODO- make work

            runCycle(input);

            hadError = false;
            
        }
    }

    static void runCycle(String input) {
        Lexer lexer = new Lexer();

        lexer.LexInput(input);
        if(hadError)
            return;
        ArrayList<Token> tokens = lexer.getGeneratedTokens();

        Parser parser = new Parser(tokens);
        ArrayList<StmtNode> stmts = parser.parse();
        if(hadError)
            return;

        interpreter.interpretStmtList(stmts);
    }
}
