package com.nigel_karunaratne.ast.functions;

import java.util.List;

import com.nigel_karunaratne.interpreter.Interpreter;

public interface CallableInterface {
    public Object call(Interpreter interpreter, List<Object> args);
    public int expectedArgCount();
}
