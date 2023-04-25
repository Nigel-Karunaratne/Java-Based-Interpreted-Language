package com.nigel_karunaratne.ast.functions.native_functions;

import java.util.List;

import com.nigel_karunaratne.ast.functions.CallableInterface;
import com.nigel_karunaratne.interpreter.Interpreter;

public class ToStringFunction implements CallableInterface {

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        return args.get(0).toString();
    }

    @Override
    public int expectedArgCount() {
        return 1;
    }

    @Override
    public String toString() {
        return "<<native func>>";
    }

}