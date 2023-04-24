package com.nigel_karunaratne.environment;

import java.util.HashMap;
import java.util.Map;

import com.nigel_karunaratne.error_handler.ErrorHandler;
import com.nigel_karunaratne.tokens.Token;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    public final Environment parent;

    public Environment() {
        parent = null;
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public void defineValue(String name, Object value) {
        values.put(name, value);
    }

    public Object getValueAt(int distance, String name) {
        return getAncestorEnvironment(distance).values.get(name);
    }

    public Object getValue(Token name) {
        if(values.containsKey(name.value)) {
            return values.get(name.value);
        }

        if(hasParent()) {
            return parent.getValue(name); //TODO - find another way that doesnt use recursion?
        }

        throw ErrorHandler.throwRuntimeError(name, "Identifier '" + name.value +"' hasn't been defined yet");
    }

    public void setValueAt(int distance, Token name, Object value) {
        getAncestorEnvironment(distance).values.put(name.value.toString(), value);
    }

    public void setValue(Token identifierToken, Object newValue) {
        if(values.containsKey(identifierToken.value)) {
            values.put((String)identifierToken.value, newValue);
            return;
        }

        if(hasParent()) {
            parent.setValue(identifierToken, newValue);
            return;
        }

        ErrorHandler.throwRuntimeError(identifierToken, "The variable '" + identifierToken.value.toString() + "' is undefined.");
    }

    private Environment getAncestorEnvironment(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.parent;
        }

        return environment;
    }
}
