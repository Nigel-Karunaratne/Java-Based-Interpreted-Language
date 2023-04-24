package com.nigel_karunaratne.ast.functions.native_functions;

import java.util.List;

import com.nigel_karunaratne.ast.functions.CallableInterface;
import com.nigel_karunaratne.interpreter.Interpreter;

public class ToNumberFunction implements CallableInterface {

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        String obj = args.get(0).toString();
        try {
            int i = Integer.parseInt(obj);
            return i;
        } catch (NumberFormatException e) {
        }

        try {
            double d = Double.parseDouble(obj);
            return d;
        } catch (NumberFormatException e) {
        }

        return null;
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