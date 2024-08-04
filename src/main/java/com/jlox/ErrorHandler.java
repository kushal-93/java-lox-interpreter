package com.jlox;

public class ErrorHandler {
    public static void error(int line, int column, String message) {
        report(line, column, "", message);
    }

    private static void report(int line, int column, String where, String message) {
        Lox.hadError = true;
        System.err.println("[line " + line + ", column "+column+"] Error" + where + ": " + message);
    }

    static void error(Token token, String message) {
        if(token.type == TokenType.EOF) 
            report(token.line, token.column, " at end", message);
        else
            report(token.line, token.column, " at '"+token.lexeme+"'", message);
    }
}
