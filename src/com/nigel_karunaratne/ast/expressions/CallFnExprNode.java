package com.nigel_karunaratne.ast.expressions;

import java.util.List;
import com.nigel_karunaratne.tokens.Token;


public class CallFnExprNode extends ExprNode {

    public final ExprNode callee;
    public final Token parenthesis;
    public final List<ExprNode> args;

    public CallFnExprNode(ExprNode callee, Token parenthesis, List<ExprNode> args) {
        this.callee = callee;
        this.parenthesis = parenthesis;
        this.args = args;
    }

    @Override
    public <T> T accept(ExprNodeVisitor<T> visitor) {
        return visitor.visitCallFunExpr(this);
    }
    
}
