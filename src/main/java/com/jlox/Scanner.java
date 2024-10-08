package com.jlox;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;


public class Scanner {

    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and",    TokenType.AND);
        keywords.put("class",  TokenType.CLASS);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("for",    TokenType.FOR);
        keywords.put("fun",    TokenType.FUN);
        keywords.put("if",     TokenType.IF);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("or",     TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);
      }

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

    private char peekNext() {
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

    private void string() {
        String value = "";
        boolean escCharError = false;
        while(peek() != '"' && peek() != '\n' && !isAtEnd()) {
            if(peek() == '\\'){
                // consume \ but not included in value"
                advance();
                // consume next char & check if escape acceptable
                char c = advance();
                switch(c) {
                    case '"' :
                        value += '"';
                        break;
                    case '\\' :
                        value += '\\';
                        break;
                    case 'n' :
                        value += '\n';
                        break;
                    case 'r' :
                        value += '\r';
                        break;
                    case 't' :
                        value += '\t';
                        break;
                    default:
                        ErrorHandler.error(line, start+1, "Illegal escape character in string literal.");
                        escCharError = true;
                        //return;
                }
            }
            else
                value += advance();
        }
        
        if(peek() == '\n') {
            ErrorHandler.error(line, start+1, "Illegal new line character in string literal.");
            return;
        }
        if(isAtEnd()) {
            ErrorHandler.error(line, start+1, "String literal not terminated properly.");
            return;
        }

        // consume the closing "
        advance();

        if(!escCharError)
            addToken(TokenType.STRING, value);

    }
        
    private boolean number(boolean dot) {
        while(isDigit(peek()))
            advance();
        
        if(peek() == '.' && isDigit(peekNext())) {
            advance();
            if(dot) {
                ErrorHandler.error(line, start+1, "Illegal character in number literal");
                return false;
            }
            else {
                return number(true);
            }
        }
        return true;
    }

    private void identifier() {
        char ch = peek();
        while(isAlphaNumeric(ch)) {
            advance();
            ch = peek();
        }
            

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if(type == null)
            type = TokenType.IDENTIFIER;
        addToken(type);
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') 
            || (c >= 'A' && c <= 'Z') 
            || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isDigit(c) || isAlpha(c);
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
                if(isDigit(peek())) {
                    //  a number can start with a period. In that case the token becomes a NUMBER
                    if(number(true))
                        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
                }                    
                else
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
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
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
                    while(!isAtEnd() && peek() != '*' && peekNext() != '/')
                        advance();
                }
                else 
                    addToken(TokenType.SLASH);
                break;
            case '"' :
                string();
                break;
            case ' ' :
            case '\t':
            case '\r':
                break;
            case '\n':
                ++line;
                break;

            default : 
                if(isDigit(c)) {
                    if(number(false))
                        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
                } else if (isAlpha(c)) {
                    identifier();
                }
                else {
                    ErrorHandler.error(line, start+1, "Unexpected character!");
                }
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
