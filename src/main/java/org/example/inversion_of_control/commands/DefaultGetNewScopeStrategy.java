package org.example.inversion_of_control.commands;

import org.example.interfaces.IScope;
import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;
import org.example.inversion_of_control.scope.Scope;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultGetNewScopeStrategy implements IStrategy {
    @Override
    public Object execute(Object... args) throws Exception {
        System.out.println("\tARGS.Length DefaultGetNewScopeStrategy " + args.length);
        ConcurrentHashMap<String, IStrategy> x = IoC.resolve("Scopes.Storage");
        return new Scope(IoC.caster.cast(args[0]), x);
    }
}
