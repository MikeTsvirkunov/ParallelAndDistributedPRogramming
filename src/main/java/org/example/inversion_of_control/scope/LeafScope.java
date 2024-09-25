package org.example.inversion_of_control.scope;

import org.example.interfaces.IScope;
import org.example.interfaces.IStrategy;

import java.util.List;
import java.util.function.Function;

public class LeafScope implements IScope {
    private final IStrategy strategy;
    public LeafScope(IStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Object resolve(String key, Object... args) throws Exception {
        return this.strategy.execute(key, args);
    }
}
