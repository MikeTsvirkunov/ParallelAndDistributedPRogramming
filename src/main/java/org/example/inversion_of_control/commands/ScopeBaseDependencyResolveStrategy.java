package org.example.inversion_of_control.commands;

import org.example.interfaces.IScope;
import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;
import org.example.inversion_of_control.scope.ScopeBaseDependencyResolve;

import java.util.Arrays;

public class ScopeBaseDependencyResolveStrategy implements IStrategy {
    @Override
    public Object execute(Object... args) {
        String k = IoC.caster.cast(args[0]);
        Object[] v = IoC.caster.cast(args[1]);
        return ScopeBaseDependencyResolve.resolve(k, v);
    }
}
