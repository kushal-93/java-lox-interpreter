package com.jlox;

import java.util.List;

public class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private boolean match(TokenType... types) {
        for(TokenType type : types) {
            if(check(type)) {
                advance(); // why match is consuming the token??
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if(isAtEnd()) 
            return false;
        return peek().type == type;
    }

    private Token advance() {
        if(!isAtEnd()) 
            current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current-1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        ErrorHandler.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while(!isAtEnd()) {
            if(previous().type == TokenType.SEMICOLON) return;

            switch(peek().type) {
                case CLASS:
                case FUN:
                case VAR: 
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }

    private Expr expression() {
        return conditional();
    }

    // conditional → equality ( "?" expression ":" expression )? ;

    private Expr conditional() {
        Expr expr = equality();
        if (match(TokenType.QUESTION)) {
            Expr middle = expression();
            consume(TokenType.COLON, "Expect ':' in conditional expression.");
            Expr right = expression();
            return new Expr.Conditional(expr, middle, right);
        }
        return expr;
    }

    // equality → comparison ( ( "!=" | "==" ) comparison )* ;

    private Expr equality() {
        Expr expr = comparison();

        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // comparison → bitwise ( ( ">" | ">=" | "<" | "<=" ) bitwise )* ;

    private Expr comparison() {
        Expr expr = bitwise();

        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = bitwise();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // bitwise → term ( ( "&" | "|" ) term )* ;

    private Expr bitwise() {
        Expr expr = term();

        while(match(TokenType.BITWISE_AND, TokenType.BITWISE_OR)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // term → factor ( ( "+" | "-" ) factor )*

    private Expr term() {
        Expr expr = factor();

        while(match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // factor → unary ( ( "/" | "*" ) unary )*;

    private Expr factor() {
        Expr expr = unary();
    
        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    /* 
     unary → ( "!" | "-" ) unary 
             | primary;
    */

    private Expr unary() {
        if(match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    /* 
        primary → NUMBER | STRING | "true" | "false" | "nil" 
                  | "(" expression ")" 
                  | ( "==" | "!=" ) equality
                  | ( ">" | "<" | "<=" | ">=" ) comparision
                  | ( "+" ) term
                  | ( "*" | "/" ) factor
    */

    private Expr primary() {
        if(match(TokenType.NUMBER, TokenType.STRING)) {
            Expr expr = new Expr.Literal(previous().literal);
            return expr;
        }

        if(match(TokenType.FALSE)) 
            return new Expr.Literal(false);
        if(match(TokenType.TRUE)) 
            return new Expr.Literal(true);
        if(match(TokenType.NIL)) 
            return new Expr.Literal(null);
        
        if(match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            error(previous(), "Missing left operand");
            equality();
            return null;
        }

        if(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            error(previous(), "Missing left operand");
            comparison();
            return null;
        }

        if(match(TokenType.PLUS)) {
            error(previous(), "Missing left operand");
            term();
            return null;
        }

        if(match(TokenType.STAR, TokenType.SLASH)) {
            error(previous(), "Missing left operand");
            factor();
            return null;
        }
        
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }


}
