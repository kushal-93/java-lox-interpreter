package com.jlox;

public class Interpreter implements Visitor<Object> { 

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        // TODO
        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr){
        Object right = evaluate(expr.right);

        switch(expr.operator.type) {
            case MINUS :
                return -(double)right;
            case BANG:
                return !isTruthy(right);
            default:
        }
        // should be unreachable
        return null;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private Boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }
    
}
