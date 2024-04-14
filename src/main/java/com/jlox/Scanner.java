package com.jlox;

import java.util.List;
import java.util.ArrayList;

public class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0, current = 0, line = 1;

    Scanner(String source) {
        this.source = source;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private char peek() {
        if(isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private char peekSecond() {
        if(isAtEnd() || current+1 >= source.length())
            return '\0';
        return source.charAt(current+1);
    }

    private boolean match(char expected) {
        if(peek() != expected)
            return false;
        advance();
        return true;
    }
    
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, line, start+1, literal));
    }

    private void scanToken() {
        char c = advance();

        switch(c) {
            case '(' : 
                addToken(TokenType.LEFT_PAREN); 
                break;
            case ')' : 
                addToken(TokenType.RIGHT_PAREN); 
                break;
            case '{' : 
                addToken(TokenType.LEFT_BRACE); 
                break;
            case '}' : 
                addToken(TokenType.RIGHT_BRACE); 
                break;
            case '.' : 
                addToken(TokenType.DOT); 
                break;
            case ',' : 
                addToken(TokenType.COMMA); 
                break;
            case '-' : 
                addToken(TokenType.MINUS); 
                break;
            case '+' : 
                addToken(TokenType.PLUS); 
                break;
            case ';' : 
                addToken(TokenType.SEMICOLON); 
                break;
            case '*' : 
                addToken(match('/') ? TokenType.BLOCK_COMMENT_END : TokenType.STAR);
                break; 
            case '!' :
                
                break;
            case '=' :
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '>' :
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '<' :
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '/' :
                if (match('/')) {
                    while(peek() != '\n' && !isAtEnd())
                        advance();
                }
                else if(match('*')) {
                    addToken(TokenType.BLOCK_COMMENT_START);
                    while(!isAtEnd() && peek() != '*' && peekSecond() != '/')
                        advance();
                }
                else 
                    addToken(TokenType.SLASH);
                break;
            case ' ':
            case '\t':
            case '\r':
                break;
            case '\n':
                ++line;
                break;

            default : 
                ErrorHandler.error(line, start+1, "Unexpected character!");
                break;
        }
    }

    public List<Token> scanTokens() {

        while(!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", line+1, 1, null));
        return tokens;
    }
}
