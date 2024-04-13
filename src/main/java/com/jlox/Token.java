package com.jlox;

public class Token {
    
    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final int column;
    private final Object literal;

    Token(TokenType type, String lexeme, int line, int column, Object literal) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
        this.literal = literal;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal + " [" + line + ", " + column +"]"; 
    }

}
