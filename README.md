# A Java-Based-Interpreted-Language
## Made by Nigel Karunaratne

> README.md is incomplete

> For Comp Sci 2 (Spring 2023) Honors Project.

This project involved creating an interpreter for a fictional, made-up language. 

The purpose of this project was to understand how computers actually run computer code.

This project used the book [Crafting Interpreters](http://craftinginterpreters.com) (by Robert Nystrom) as a major reference for understanding how an interpreter works and why each piece is needed. The parser's grammer used in the book was also used here.

---
This interpreter uses 4 distinct "modules":
1. The __Lexer__ takes in source code (as a String) and generates a list of _Tokens_.
2. The __Parser__ takes in a list of _Tokens_ and generates a list of _Statements_. The parser is where the parsing grammar gets applied (syntactic analysis) and trees of _Statements_ and _Expressions_ are created.
3. The __Identifier Resolver__ does semantic analysis on the parser's list of _Statements_ (it determines what delcaration each variable in the code refers to).
4. The __Interpreter__ walks along each tree of _Statements_ and _Expressions_ and does the actual interpretation of the code. 

---
The language application supports two modes:
1. Running the program _without_ arguments, this opens up a REPL where code can be typed.
    - The REPL is currently limited to typing one line at a time (a statement cannot span more than one line).
2. Running the program and _passing in a file_, this interprets the file and displays output on screen.

In the REPL, two commands are supported:
- __help__: this displays a help page detailing these commands + some native functions
- __exit__: this quits the REPL

The language itself supports:
- Dynamically typed variables
- integer, double, and boolean data types (and null)
- Functions
- Simple control flow structures (if-else statements and while loops)
- Native functions for reading user input and printing to the screen