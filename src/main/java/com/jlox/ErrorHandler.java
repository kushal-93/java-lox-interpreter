package com.jlox;

public class ErrorHandler {
    public static void error(int line, int column, String message) {
        Lox.hadError = true;
        report(line, column, "", message);
    }

    private static void report(int line, int column, String where, String message) {
        System.err.println("[line " + line + ", column "+column+"] Error" + where + ": " + message);
    }
}
