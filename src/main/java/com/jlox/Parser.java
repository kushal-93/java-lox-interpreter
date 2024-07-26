package com.jlox;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
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

    private Expr expression() {
        return equality();
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

    // comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;

    private Expr comparison() {
        Expr expr = term();

        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
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
                  | "(" expression ")" ;
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
        
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            //consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
    }
    


    


}
