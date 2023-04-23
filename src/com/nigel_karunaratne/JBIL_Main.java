package com.nigel_karunaratne;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import com.nigel_karunaratne.ast.statements.StmtNode;
import com.nigel_karunaratne.error_handler.ErrorHandler;
import com.nigel_karunaratne.identifier_resolver.IdentifierResolver;
import com.nigel_karunaratne.interpreter.Interpreter;
import com.nigel_karunaratne.lexer.Lexer;
import com.nigel_karunaratne.parser.Parser;
import com.nigel_karunaratne.tokens.Token;

//* This is the entry point for the interpreter command line.
public class JBIL_Main {

    //flags for REPL
    public static boolean hadError;

    public static final Interpreter interpreter = new Interpreter();

    public static Scanner userInputScanner;

    public static void main(String[] args) throws Exception {
        ErrorHandler.debugOutput("Main App started.");
        userInputScanner = new Scanner(System.in);

        if(args.length >= 1) {
            //Interpret a single file
            // Lexer lexer = new Lexer();
            // lexer.LexInput(args[0]);

            // Parser parser = new Parser(lexer.getGeneratedTokens());
            // List<StmtNode> stmts = parser.parse();

            // VariableResolver resolver = new VariableResolver(interpreter);
            // resolver.resolveAllStmts(stmts);

            // interpreter.interpretStmtList(stmts);

            hadError = false;

            // File file = new File(args[0]);
            if(!Files.exists(Paths.get(args[0]))) {
                System.out.println("Error: File '" + args[0] + "' does not exist.");
                System.exit(1);
            }

            byte[] fileBytes = Files.readAllBytes(Paths.get(args[0]));
            runCycle(new String(fileBytes, Charset.defaultCharset()));

            if(hadError) {
                System.exit(1);
            }

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
        // String inputString = "var y = \"Hello\"; func retThree(){var x = input(); if(x == \"hello\") {return true;} else {return false;} }\nprint(retThree());";

        // System.out.println("Lexing: \n" + inputString + "\n");

        // Lexer lexer = new Lexer();
        // lexer.LexInput(inputString);

        // Parser parser = new Parser(lexer.getGeneratedTokens());

        // List<StmtNode> stmts = parser.parse();

        // interpreter.interpretStmtList(stmts);

        userInputScanner.close();
        ErrorHandler.debugOutput("Main App ended.");
    }

    static void runRepl() {
        boolean shouldExit = false;
        // Lexer lexer = new Lexer();
        // Parser parser;

        System.out.println("Java-Based Interpreted Language\nType help to see built-in commands\n");
        // Scanner scanner = new Scanner(System.in);


        while(!shouldExit) {
            System.out.print(">>> ");
            String input = userInputScanner.nextLine();//scanner.nextLine();

            if(input == null || input.equals("exit")) {
                break; //TODO- make work
            }
            
            if(input.equals("help")) {
                System.out.println("REPL commands:");
                System.out.println("exit\tExits the current REPL session");
                System.out.println("help\tDisplays help message");
                System.out.println("\nBuilt-in functions:");
                System.out.println("print()   Prints to the screen");
                System.out.println("printN()  Prints to the screen (omits the final newline)");
                System.out.println("input()   Waits for the user to input a line, returns the line");

                continue;
            }
            
            runCycle(input);

            hadError = false;
            
        }

        // scanner.close();
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

        IdentifierResolver resolver = new IdentifierResolver(interpreter);
        resolver.resolveAllStmts(stmts);
        if(hadError)
            return;

        interpreter.interpretStmtList(stmts);
    }
}
