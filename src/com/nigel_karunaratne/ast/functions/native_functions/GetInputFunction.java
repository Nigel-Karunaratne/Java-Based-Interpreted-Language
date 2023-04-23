package com.nigel_karunaratne.ast.functions.native_functions;

import java.util.List;

import com.nigel_karunaratne.JBIL_Main;
import com.nigel_karunaratne.ast.functions.CallableInterface;
import com.nigel_karunaratne.interpreter.Interpreter;

public class GetInputFunction implements CallableInterface {

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        System.out.print(">>? ");
        return JBIL_Main.userInputScanner.nextLine();
    }

    @Override
    public int expectedArgCount() {
        return 0;
    }

    @Override
    public String toString() {
        return "<<native func>>";
    }

}