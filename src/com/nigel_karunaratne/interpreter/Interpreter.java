package com.nigel_karunaratne.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nigel_karunaratne.ast.expressions.*;
import com.nigel_karunaratne.ast.functions.*;
import com.nigel_karunaratne.ast.functions.native_functions.*;
import com.nigel_karunaratne.ast.statements.*;
import com.nigel_karunaratne.environment.Environment;
import com.nigel_karunaratne.error_handler.ErrorHandler;
import com.nigel_karunaratne.error_handler.RuntimeError;
import com.nigel_karunaratne.tokens.Token;

public class Interpreter implements ExprNodeVisitor<Object>, StmtNodeVisitor<Void> {

    public Environment globalsEnvironment = new Environment();
    private Environment baseEnvironment = globalsEnvironment; //TODO - remove this : new Environment();

    private final Map<ExprNode, Integer> locals = new HashMap<>();

    //Used to define global functions
    public Interpreter() {
        globalsEnvironment.defineValue("printN", new PrintNoNewlineFunction());
        globalsEnvironment.defineValue("print", new PrintFunction());
        globalsEnvironment.defineValue("input", new GetInputFunction());
        globalsEnvironment.defineValue("inputM", new GetInputWithMessageFunction());
        globalsEnvironment.defineValue("toNum", new ToNumberFunction());
        globalsEnvironment.defineValue("toStr", new ToStringFunction());
    }

    public void interpretExprTree(ExprNode exp) {
        try {
            Object value = evaluateExpr(exp);
            ErrorHandler.debugOutput("VALUE EVALUATED -> " + value.toString() + " : " + (value.getClass().getName()));
        } catch (RuntimeError e) {
            //TODO - look into here
        }
    }

    public void interpretStmtList(List<StmtNode> stmts) {
        try {
            for (StmtNode stmt : stmts) {
                stmt.accept(this); //TODO - move to method?
            }
        }
        catch (RuntimeError e) {

        }
    }

    //ANCHOR - Statement Visitor Methods
    @Override
    public Void visitExprStmt(ExprStmtNode stmt) {
        evaluateExpr(stmt.expr);
        // Object o = evaluateExpr(stmt.expr);
        // ErrorHandler.debugOutput(o.toString());
        return null;
    }

    @Override
    public Void visitVarDeclStmt(VarDeclarationStmtNode stmt) {
        Object value = null;
        if(stmt.initialExpr != null) {
            value = evaluateExpr(stmt.initialExpr);
        }

        baseEnvironment.defineValue(stmt.name.value.toString(), value);

        return null;
    }

    @Override
    public Void visitBlockStmt(BlockStmtNode stmt) {
        // Environment previousEnvironment = baseEnvironment;
        // Environment blockEnvironment = new Environment(baseEnvironment);
        // try {
        //     baseEnvironment = blockEnvironment;

        //     for (StmtNode content : stmt.contents) {
        //         content.accept(this);
        //     }
        // }
        // finally {
        //     baseEnvironment = previousEnvironment;
        // }

        // return null;
        executeBlockStmt(stmt.contents, new Environment(baseEnvironment));
        return null;
    }

    public void executeBlockStmt(List<StmtNode> stmts, Environment blockEnvironment) {
        Environment previousEnvironment = baseEnvironment;
        try {
            baseEnvironment = blockEnvironment;

            for (StmtNode content : stmts) {
                content.accept(this);
            }
        }
        finally {
            baseEnvironment = previousEnvironment;
        }

        return;
    }

    @Override
    public Void visitIfStmt(IfStmtNode stmt) {
        if(determineTruthy(evaluateExpr(stmt.conditionExpr))) {
            stmt.thenStmt.accept(this);
        } else if (stmt.elseStmt != null) {
            stmt.elseStmt.accept(this);
        }

        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmtNode stmt) {
        while(determineTruthy(evaluateExpr(stmt.conditionExpr))) {
            stmt.bodyStmt.accept(this);
        }

        return null;
    }

    @Override
    public Void visitFunctionDefStmt(FunctionDefStmtNode stmt) {
        CallableFunction function = new CallableFunction(stmt, baseEnvironment);
        baseEnvironment.defineValue(stmt.name.value.toString(), function);
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmtNode stmt) {
        Object value = null; //default return value
        if(stmt.value != null)
            value = evaluateExpr(stmt.value);
        
        throw new ReturnFromFunction(value);
    }

    //ANCHOR - Expression Visitor Methods
    @Override
    public Object visitBinaryExpr(BinaryExprNode expr) {
        Object left = evaluateExpr(expr.left);
        Object right = evaluateExpr(expr.right);

        switch(expr.operator.type) {
            //equality
            case EQUALS:
                return isEqual(left, right); 
            case NOT_EQUAL:
                return !isEqual(left, right); 

            //comparator
            //since these always return bool, just convert all nums to doubles (no bools can be accepted)
            case GREATER_THAN:
                enforceNumberOrStringOperands(expr.operator, left, right);
                return getComparasonValue(left) > getComparasonValue(right);
            case GREATER_THAN_EQUAL:
                enforceNumberOrStringOperands(expr.operator, left, right);
                return getComparasonValue(left) >= getComparasonValue(right);
            case LESS_THAN:
                enforceNumberOrStringOperands(expr.operator, left, right);
                return getComparasonValue(left) < getComparasonValue(right);
            case LESS_THAN_EQUAL:
                enforceNumberOrStringOperands(expr.operator, left, right);
                return getComparasonValue(left) <= getComparasonValue(right);


            //arithmetic
            case ADD:
                //TODO - Check Addition
                enforceNumberStringOrFunctionOperands(expr.operator, left, right);
                if(isString(left) || isString(right) || (isFunction(left) ^ isFunction(right)))
                    return returnStringVersion(left) + returnStringVersion(right);

                //you can only add functions via strings
                enforceNumberOrStringOperands(expr.operator, left, right);
                if((isDouble(left) || isDouble(right)))
                    return convertNumToDouble(left) + convertNumToDouble(right);
                else
                    return (int)left + (int)right;
            case MINUS:
                //TODO - Check
                enforceNumberOperands(expr.operator, left, right);
                if(isDouble(left) || isDouble(right))
                    return convertNumToDouble(left) - convertNumToDouble(right);
                else
                    return (int)left - (int)right;
            case MUL:
                enforceNumberOperands(expr.operator, left, right);
                //TODO - Check Multiplication
                if(isDouble(left) || isDouble(right))
                    return convertNumToDouble(left) * convertNumToDouble(right);
                else
                    return (int)left * (int)right;
            case DIV:
                //TODO - Check Division
                enforceNumberOperands(expr.operator, left, right);
                //check for div by zero
                if((right instanceof Double && (double)right == 0) || (right instanceof Integer && (int)right == 0)) {
                    throw ErrorHandler.throwRuntimeError(expr.operator, "Righthand side of division cannot be zero.");
                }

                if(isDouble(left) || isDouble(right))
                    return convertNumToDouble(left) / convertNumToDouble(right);
                else
                    return (int)left / (int)right;
            case MOD:
                //TODO - Check Modulo
                enforceNumberOperands(expr.operator, left, right);
                if(isDouble(left) || isDouble(right))
                    return convertNumToDouble(left) % convertNumToDouble(right);
                else
                    return (int)left % (int)right;
            case POWER:
                //TODO - Check power
                enforceNumberOperands(expr.operator, left, right);
                if(isDouble(left) || isDouble(right))
                    return Math.pow(convertNumToDouble(left), convertNumToDouble(right));
                else
                    return (int)Math.pow((int)left, (int)right);
            default:
                break;
        }

        //This should never happen, as the checks for the correct operators happened in the parser.
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExprNode expr) {
        Object right = evaluateExpr(expr.expression);

        switch(expr.operator.type) {
            case NOT:
                return !determineTruthy(right);
            case MINUS:
                enforceNumberOperands(expr.operator, right);
                if(isDouble(right))
                    return -((double)right);
                else
                    return-((int)right);
            default:
                break;
        }

        //This should never happen, as the checks for NOT and MINUS happened in the parser.
        return null;
    }

    @Override
    public Object visitLiteralExpr(LiteralExprNode expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupExpr(GroupExprNode expr) {
        return evaluateExpr(expr.expression);
    }

    @Override
    public Object visitVarAccessExpr(VarAccessExprNode expr) {
        // return baseEnvironment.getValue(expr.identifier);
        return lookUpVariable(expr.identifier, expr);
    }

    @Override
    public Object visitVarAssignExpr(VarAssignmentExprNode expr) {
        Object newValue = evaluateExpr(expr.value);
        // baseEnvironment.setValue(expr.name, newValue);
        Integer distance = locals.get(expr);
        if(distance != null) {
            baseEnvironment.setValueAt(distance, expr.name, newValue);
        }
        else {
            globalsEnvironment.setValue(expr.name, newValue);
        }

        return newValue;
    }

    @Override
    public Object visitLogicalOp(LogicalOpExprNode expr) {
        Object left = evaluateExpr(expr.left);

        //TODO - check to see if values return as booleans or as themselves w/ equivalent truth values
        switch(expr.operator.type) {
            case OR:
                if(determineTruthy(left))
                    // return left;
                    return true;
                else
                    // return evaluateExpr(expr.right);
                    return determineTruthy(evaluateExpr(expr.right));

            case AND:
                if(!determineTruthy(left))
                    // return left;
                    return true;
                else
                    // return evaluateExpr(expr.right);
                    return determineTruthy(evaluateExpr(expr.right));

            default:
                break;
        }

        //This should never happen
        return null;
    }

    @Override
    public Object visitCallFunExpr(CallFnExprNode expr) {
        Object callee = evaluateExpr(expr.callee);

        List<Object> args = new ArrayList<>();
        for (ExprNode arg : expr.args) {
            args.add(evaluateExpr(arg));
        }

        if(!(callee instanceof CallableInterface))
            ErrorHandler.throwRuntimeError(expr.parenthesis, "Cannot call anything other than a function.");
            
        CallableInterface function = (CallableInterface)callee;
        if(args.size() != function.expectedArgCount()) {
            ErrorHandler.throwRuntimeError(expr.parenthesis, "Expected " + function.expectedArgCount() + " args, got " + args.size() +" instead.");
            return null; //so that we don't even try calling the function
        }
        
        return function.call(this, args);
    }

    //ANCHOR - Helper methods

    public void resolve(ExprNode expr, int depth) {
        locals.put(expr,depth);
    }

    private Object lookUpVariable(Token name, ExprNode expr) {
        Integer distance = locals.get(expr);
        if(distance != null) {
            return baseEnvironment.getValueAt(distance, name.value.toString());
        }
        else {
            return globalsEnvironment.getValue(name);
        }
    }

    private String returnStringVersion(Object o) {
        if(o instanceof Integer)
            return ((Integer)o).toString();
        if(o instanceof Double)
            return ((Double)o).toString();
        if(o instanceof String)
            return ((String)o);
        if(o instanceof CallableFunction)
            return ((CallableFunction)o).toString();
        return o.toString();
    }

    private Double convertNumToDouble(Object o) {
        if(o instanceof Integer)
            return Double.valueOf(((Integer)o).intValue());
        else
            return ((Double)o);
    }

    // private Double returnDoubleVersion(Object o) {
    //     if(o instanceof Integer)
    //         return Double.// ((Integer)o)
    // }

    //All non-zero numbers + non-null objects are true. Booleans should evaluate as themselves.
    private boolean determineTruthy(Object obj) {
        if(obj instanceof Boolean) return (boolean)obj;
        if(obj == null || (obj instanceof Integer || obj instanceof Double) && (double)obj == 0.0) return false;

        return true;
    }

    //Calls the accept method on the given Expr.
    private Object evaluateExpr(ExprNode expr) {
        return expr.accept(this);
    }

    private boolean isEqual(Object l, Object r) {
        if(l == null)
            return r == null;
        
        return l.equals(r);
    }

    private boolean isDouble(Object obj) {
        return (obj instanceof Double); //!!!!!!!!!!!OR instance of Float???????????????
    }

    private boolean isString(Object obj) {
        return (obj instanceof String);
    }

    private boolean isNumber(Object obj) {
        return (obj instanceof Double) || (obj instanceof Integer);
    }

    private boolean isFunction(Object obj) {
        return (obj instanceof CallableFunction);
    }

    private double getComparasonValue(Object obj) {
        //for >, <, >=, <= (no booleans can be accepted)
        //strings should be compared on length (returned as double)
        //ints and floats -> get converted to java doubles

        //TODO - if (obj == null) throw throwRuntimeError() ???

        if(obj instanceof String)
            return ((String)obj).length();
        else if(obj instanceof Double)
            return (Double)obj;
        else
            return (double)((Integer)obj);
    }

    // private RuntimeError throwRuntimeError(Token currentToken, String errorMessage) {
    //     ErrorHandler.OutputException(errorMessage, currentToken.line, currentToken.column);
    //     return new RuntimeError();
    // }

    private void enforceNumberOperands(Token operatorToken, Object... operands) {
        for (Object object : operands) {
            if(!isNumber(object))
                throw ErrorHandler.throwRuntimeError(operatorToken, "The operand MUST be a number");
        }
    }

    private void enforceNumberOrStringOperands(Token operatorToken, Object... operands) {
        for (Object object : operands) {
            if(!isNumber(object) && !isString(object))
                throw ErrorHandler.throwRuntimeError(operatorToken, "The operand must be either a number or a string");
        }
    }

    private void enforceNumberStringOrFunctionOperands(Token operatorToken, Object... operands) {
        for (Object object : operands) {
            if(!isNumber(object) && !isString(object) && !isFunction(object))
                throw ErrorHandler.throwRuntimeError(operatorToken, "The operand must be either a number or a string");
        }
    }
}
