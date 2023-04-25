package com.nigel_karunaratne;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.nigel_karunaratne.ast.statements.StmtNode;
import com.nigel_karunaratne.identifier_resolver.IdentifierResolver;
import com.nigel_karunaratne.interpreter.Interpreter;
import com.nigel_karunaratne.lexer.Lexer;
import com.nigel_karunaratne.parser.Parser;
import com.nigel_karunaratne.tokens.Token;

//* This is the entry point for the interpreter command line.
public class JBIL_Main {

    //flags (& variables)for REPL
    public static boolean hadError;
    public static Scanner userInputScanner;

    //global interpreter instance
    public static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws Exception {
        userInputScanner = new Scanner(System.in);

        if(args.length >= 1) {
            //Interpret a single file
            hadError = false;

            // File file = new File(args[0]);
            if(!Files.exists(Paths.get(args[0]))) {
                System.out.println("Error: File '" + args[0] + "' does not exist.");
                System.exit(2);
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

        userInputScanner.close();
    }

    static void runRepl() {
        boolean shouldExit = false;
        // Lexer lexer = new Lexer();
        // Parser parser;

        System.out.println("Java-Based Interpreted Language\nType help to see built-in commands\n");
        // Scanner scanner = new Scanner(System.in);


        while(!shouldExit) {
            System.out.print(">>> ");
            String input;

            try {
                input = userInputScanner.nextLine();
            } catch(NoSuchElementException e) {
                break; //Catches Ctrl+C (on windows), and should catch other console terminators
            }

            if(input == null || input.equals("exit")) {
                break;
            }
            
            if(input.equals("help")) {
                System.out.println("REPL commands:");
                System.out.println("exit\tExits the current REPL session");
                System.out.println("help\tDisplays help message");
                System.out.println("\nBuilt-in functions:");
                System.out.println("print(obj)    Prints obj to the screen");
                System.out.println("printN(obj)   Prints obj to the screen (omits the final newline)");
                System.out.println("input()       Waits for the user to input a line, returns the line");
                System.out.println("inputN(msg)   Prints the message, then acts the same as input()");
                System.out.println("toNum(obj)    Tries to convert obj (usually a string) into a number (double or int). Returns null if it fails");
                System.out.println("toStr(obj)    Returns the string representation of the object.");

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
