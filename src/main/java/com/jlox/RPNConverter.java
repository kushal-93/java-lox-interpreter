package com.jlox;

public class RPNConverter implements Visitor<String>{


    private String print(Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        for (Expr expr : exprs) {
            builder.append(expr.accept(this));
        }
        builder.append(" ");
        return builder.toString();
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return  print(expr.left, expr.right) + expr.operator.lexeme;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return "group" + print(expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if(expr.value == null) 
            return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return expr.operator.lexeme + print(expr.right);
    }


    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
            new Expr.Unary(
                new Token(TokenType.MINUS, "-", 1, 1, null),
                new Expr.Literal(123)
            ),
            new Token(TokenType.STAR, "*", 1, 2, null),
            new Expr.Grouping(
                new Expr.Literal(45.67)
            )
        );

        System.out.println(expression.accept(new RPNConverter()));
    }
}
