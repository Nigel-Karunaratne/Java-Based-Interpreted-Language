package com.nigel_karunaratne.ast.functions.native_functions;

import java.util.List;

import com.nigel_karunaratne.JBIL_Main;
import com.nigel_karunaratne.ast.functions.CallableInterface;
import com.nigel_karunaratne.interpreter.Interpreter;

public class ToNumberFunction implements CallableInterface {

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        String v = args.get(0).toString();
        try {
            int x = Integer.parseInt(v);
            return x;
        } catch (NumberFormatException e) {
        }

        try {
            double y = Double.parseDouble(v);
            return y;
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