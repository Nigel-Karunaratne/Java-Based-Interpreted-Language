package com.nigel_karunaratne.error_handler;

public final class ErrorHandler {
    public static void OutputWarning(String message) {
        System.out.printf("WARNING | ????, ???? | %s\n", message);
    }

    public static void OutputWarning(String message, int line, int col) {
        System.out.printf("WARNING | %4d, %4d | %s\n", line, col, message);
    }

    public static void OutputException(String message) {
        System.out.printf("ERROR   | ????, ???? | %s\n", message);
    }

    public static void OutputException(String message, int line, int col) {
        System.out.printf("ERROR   | %4d, %4d | %s\n", line, col, message);
    }


    public static void OutputFatalException() {

    }

    public static void DebugOutput(String message) {
        System.out.printf("DEBUG   | ????, ???? | %s\n", message);
    }
}
