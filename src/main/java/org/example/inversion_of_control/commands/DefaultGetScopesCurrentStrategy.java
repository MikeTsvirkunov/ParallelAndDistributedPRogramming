package org.example.inversion_of_control.commands;

import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;
import org.example.inversion_of_control.scope.Scope;
import org.example.inversion_of_control.scope.ScopeBaseDependencyResolve;

public class DefaultGetScopesCurrentStrategy implements IStrategy {
    @Override
    public Object execute(Object[] args) throws Exception {
        Scope value = ScopeBaseDependencyResolve.currentScope.get();
        value = (value != null) ? value : IoC.caster.cast(ScopeBaseDependencyResolve.defaultScope.get());
        return value;
    }
}
