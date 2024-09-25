package org.example.inversion_of_control.commands;

import org.example.interfaces.IStrategy;

import java.util.function.Function;

public class DefaultStrategy implements IStrategy {
    private final Function<Object[], Object> strategy;
    public DefaultStrategy(Function<Object[], Object> strategy) {
        this.strategy = strategy;
    }
    @Override
    public Object execute(Object[] args) throws Exception {
        return strategy.apply(args);
    }
}
