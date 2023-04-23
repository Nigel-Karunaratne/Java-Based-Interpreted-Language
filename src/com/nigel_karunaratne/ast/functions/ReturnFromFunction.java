package com.nigel_karunaratne.ast.functions;

//Acts as a sentinel. When returning from a function, throw this and catch it _____.
public class ReturnFromFunction extends RuntimeException {
    public final Object returnValue;

    public ReturnFromFunction(Object returnValue) {
        super(null, null, false, false);
        this.returnValue = returnValue;
    }
}
