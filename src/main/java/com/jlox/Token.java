package com.jlox;

public class Token {
    
    final TokenType type;
    final String lexeme;
    final int line;
    final int column;
    final Object literal;

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
