package org.example.inversion_of_control.scope;

import org.example.interfaces.IScope;
import org.example.interfaces.IStrategy;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


public class Scope implements IScope {
    public AbstractMap<String, IStrategy> memory;
    IScope parent;
    public Scope(IScope parent, AbstractMap<String, IStrategy> memory) {
        this.parent = parent;
        this.memory = memory;
    }

    @Override
    public Object resolve(String key, Object... args) throws Exception {
        if (memory.containsKey(key)) {
            IStrategy strategy = memory.get(key);
            return strategy.execute(args);
        } else{
            return parent.resolve(key, args);
        }
    }
}
