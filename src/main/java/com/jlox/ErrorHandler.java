package com.jlox;

public class ErrorHandler {
    public static void error(int line, int column, String message) {
        report(line, column, "", message);
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println("[line "+error.token.line +", column "+error.token.column+"] Error: "+error.getMessage());
        Lox.hadRuntimeError = true;
    }

    private static void report(int line, int column, String where, String message) {
        Lox.hadError = true;
        System.err.println("[line " + line + ", column "+column+"] Error " + where + ": " + message);
    }

    static void error(Token token, String message) {
        if(token.type == TokenType.EOF) 
            report(token.line, token.column, " at end", message);
        else
            report(token.line, token.column, " at '"+token.lexeme+"'", message);
    }
}
