package com.jlox;

import java.util.List;
import java.util.ArrayList;

public class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while(!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
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
                default:
            }
            advance();
        }
    }

    private Stmt declaration() {
        try {
            if(match(TokenType.VAR)) 
                return varDeclaration();
            return statement();
        
        } catch(ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if(match(TokenType.EQUAL))
            initializer = expression();

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt statement() {
        if(match(TokenType.PRINT)) 
            return printStatement();
        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Expression(expr);
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = equality();

        if(match(TokenType.EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if(expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
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
        if(match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if(match(TokenType.FALSE)) 
            return new Expr.Literal(false);
        if(match(TokenType.TRUE)) 
            return new Expr.Literal(true);
        if(match(TokenType.NIL)) 
            return new Expr.Literal(null);
        
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }


}
