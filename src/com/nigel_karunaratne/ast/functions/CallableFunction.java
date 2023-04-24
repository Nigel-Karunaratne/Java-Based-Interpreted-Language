package com.nigel_karunaratne.ast.functions;

import java.util.List;

import com.nigel_karunaratne.ast.statements.FunctionDefStmtNode;
import com.nigel_karunaratne.environment.Environment;
import com.nigel_karunaratne.interpreter.Interpreter;

public class CallableFunction implements CallableInterface {
    private final FunctionDefStmtNode declaration;
    private final Environment closure;

    public CallableFunction(FunctionDefStmtNode declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.parameters.size(); i++) {
            environment.defineValue(declaration.parameters.get(i).value.toString(), args.get(i));
        }

        try {
            interpreter.executeBlockStmt(declaration.functionBody, environment);
        } catch (ReturnFromFunction ret) {
            return ret.returnValue;
        }
        return null;
    }

    @Override
    public int expectedArgCount() {
        return declaration.parameters.size();
    }

    @Override
    public String toString() {
        return "<<func " + declaration.name.value.toString() + ">>";
    }

}
