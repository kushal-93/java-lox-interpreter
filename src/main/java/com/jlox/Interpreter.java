package com.jlox;

public class Interpreter implements Visitor<Object> {

    @Override
    public Object visitConditionalExpr(Expr.Conditional expr) {
        Object left = evaluate(expr.left);
        if(isTruthy(left)) 
            return evaluate(expr.middle);
        else 
            return evaluate(expr.right);
    }

    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            ErrorHandler.runtimeError(error);
        }
    }


    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {

        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch(expr.operator.type) {
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case BANG_EQUAL:
                return !isEqual(left, right);
            case LESS:
                return (double)left < (double)right;
            case LESS_EQUAL:
                return (double)left <= (double)right;
            case GREATER:
                checkNumberOperand(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperand(expr.operator, left, right);
                return (double)left >= (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double)
                    return (double)left + (double)right;
                if (left instanceof String && right instanceof String)
                    return (String)left + (String)right;
                throw new RuntimeError(expr.operator, "Operand must be two numbers or two strings.");
            case MINUS:
                checkNumberOperand(expr.operator, left, right);
                return (double)left - (double)right;
            case STAR:
                checkNumberOperand(expr.operator, left, right);
                return (double)left * (double)right;
            case SLASH:
                checkNumberOperand(expr.operator, left, right);
                return (double)left / (double)right;
            case BITWISE_AND:
                if (isIntegerLike(left) && isIntegerLike(right)) {
                    return intify((Double)left) & intify((Double)right);
                }
                throw new RuntimeError(expr.operator, "Operand must be two whole numbers.");
            case BITWISE_OR:
                if (isIntegerLike(left) && isIntegerLike(right)) {
                    return intify((Double)left) | intify((Double)right);
                }
                throw new RuntimeError(expr.operator, "Operand must be two whole numbers.");
            default:
            
        }

        // should be unreachable
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
                checkNumberOperand(expr.operator, right);
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

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null) return false;
        return left.equals(right);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if(operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperand(Token operator, Object left, Object right) {
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }
    
    private String stringify(Object object) {
        if(object == null) return "nil";

        if(object instanceof Double) {
            String text = object.toString();
            if(text.endsWith(".0"))
                text = text.substring(0, text.length()-2);
            return text;
        }

        return object.toString();
    }

    private boolean isIntegerLike(Object object) {
        if(object instanceof Double) {
            String text = object.toString();
            if(text.endsWith(".0"))
                return true;
        }
        return false;
    }

    private Integer intify(Double object) {
        String string = object.toString();
        string = string.substring(0, string.length()-2);
        return Integer.parseInt(string);
    }
}
