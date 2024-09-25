package org.example.inversion_of_control.commands;

import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;
import org.example.inversion_of_control.scope.ScopeBaseDependencyResolve;

public class DefaultRegisterIoCDependencyStrategy implements IStrategy {
    @Override
    public Object execute(Object... args) throws Exception {
        String k = IoC.caster.cast(args[0]);
        IStrategy v = IoC.caster.cast(args[1]);
        ScopeBaseDependencyResolve.currentScope.get().memory.put(k, v);
        return null;
    }
}
