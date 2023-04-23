package com.nigel_karunaratne.identifier_resolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.nigel_karunaratne.ast.expressions.*;
import com.nigel_karunaratne.ast.statements.*;
import com.nigel_karunaratne.error_handler.ErrorHandler;
import com.nigel_karunaratne.interpreter.Interpreter;
import com.nigel_karunaratne.tokens.Token;

public class IdentifierResolver implements ExprNodeVisitor<Object>, StmtNodeVisitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    private boolean isInFunction = false;

    public IdentifierResolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    //ANCHOR - Expression Visitor Methods

    //*Directly affects variables & functions
    @Override
    public Object visitVarAssignExpr(VarAssignmentExprNode expr) { //TODO - convert return type to Void?
        resolveExpr(expr.value);
        resolveLocalScope(expr, expr.name);
        return null;
    }

    //*Directly affects variables & functions
    @Override
    public Object visitVarAccessExpr(VarAccessExprNode expr) { //TODO - convert return type to Void?
        if(!scopes.isEmpty() && scopes.peek().get(expr.identifier.value.toString()) == Boolean.FALSE) {
            ErrorHandler.reportResolverError(expr.identifier, "Var delcared but not defined yet (Can't read the value of a local variable in it's own initializer).");
        }

        resolveLocalScope(expr, expr.identifier);
        return null;
    }

    @Override
    public Object visitBinaryExpr(BinaryExprNode expr) {
        resolveExpr(expr.left);
        resolveExpr(expr.right);
        return null;
    }

    @Override
    public Object visitCallFunExpr(CallFnExprNode expr) {
        resolveExpr(expr.callee);
        resolveAllExpr(expr.args);
        return null;
    }

    @Override
    public Object visitGroupExpr(GroupExprNode expr) {
        resolveExpr(expr.expression);
        return null;
    }

    @Override
    public Object visitLogicalOp(LogicalOpExprNode expr) {
        resolveExpr(expr.left);
        resolveExpr(expr.right);
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExprNode expr) {
        resolveExpr(expr.expression);
        return null;
    }

    @Override
    public Object visitLiteralExpr(LiteralExprNode expr) {
        return null;
    }

    //ANCHOR - Statement Visitor Methods

    //*Directly affects variables & functions
    @Override
    public Void visitBlockStmt(BlockStmtNode stmt) {
        beginScope();
        resolveAllStmts(stmt.contents);
        endScope();
        return null;
    }

    //*Directly affects variables & functions
    @Override
    public Void visitVarDeclStmt(VarDeclarationStmtNode stmt) {
        declareVariable(stmt.name);
        if(stmt.initialExpr != null)
            resolveExpr(stmt.initialExpr);
        defineVariable(stmt.name);
        return null;
    }

    //*Directly affects variables & functions
    @Override
    public Void visitFunctionDefStmt(FunctionDefStmtNode stmt) {
        declareVariable(stmt.name);
        defineVariable(stmt.name);

        resolveFunction(stmt, true);
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmtNode stmt) {
        resolveExpr(stmt.expr);
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmtNode stmt) {
        resolveExpr(stmt.conditionExpr);
        resolveStmt(stmt.thenStmt);
        if(stmt.elseStmt != null)
            resolveStmt(stmt.elseStmt);
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmtNode stmt) {
        if(!isInFunction)
            ErrorHandler.reportResolverError(stmt.returnKeyword, "Can only return from inside a function.");
        if(stmt.value != null)
            resolveExpr(stmt.value);
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmtNode stmt) {
        resolveExpr(stmt.conditionExpr);
        resolveStmt(stmt.bodyStmt);
        return null;
    }


    //ANCHOR - Helper Methods

    private void resolveLocalScope(ExprNode expr, Token name) {
        for(int i = scopes.size() - 1; i >= 0; i--) {
            if(scopes.get(i).containsKey(name.value.toString())) {
                interpreter.resolve(expr, scopes.size() - 1 - i); //Variable must be global
                return;
            }
        }
    }

    private void resolveFunction(FunctionDefStmtNode functionStmt, boolean isInFunction) {

        boolean isEnclosingInFunction = this.isInFunction;
        this.isInFunction = isInFunction;

        beginScope();
        for (Token parameter : functionStmt.parameters) {
            declareVariable(parameter);
            defineVariable(parameter);
        }
        resolveAllStmts(functionStmt.functionBody);
        endScope();

        this.isInFunction = isEnclosingInFunction;
    }

    private void declareVariable(Token name) {
        if (scopes.isEmpty())
            return;
        
        Map<String, Boolean> scope = scopes.peek();
        if(scope.containsKey(name.value.toString()))
            ErrorHandler.reportResolverError(name, "There's already a variable called '" + name.value.toString() + "' in the same scope.");

        scope.put(name.value.toString(), false);
    }

    private void defineVariable(Token name) {
        if(scopes.isEmpty())
            return;
        scopes.peek().put(name.value.toString(), true);
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    public void resolveAllStmts(List<StmtNode> stmts) {
        for (StmtNode stmtNode : stmts) {
            resolveStmt(stmtNode);
        }
    }

    private void resolveAllExpr(List<ExprNode> exprs) {
        for (ExprNode exprNode : exprs) {
            resolveExpr(exprNode);
        }
    }

    private void resolveStmt(StmtNode stmt) {
        stmt.accept(this);
    }

    private void resolveExpr(ExprNode expr) {
        expr.accept(this);
    }
}
