package com.jlox;

public interface Visitor<R> {
    R visitConditionalExpr(Expr.Conditional expr);
    R visitBinaryExpr(Expr.Binary expr);
    R visitGroupingExpr(Expr.Grouping expr);
    R visitLiteralExpr(Expr.Literal expr);
    R visitUnaryExpr(Expr.Unary expr);
}
