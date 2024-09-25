package org.example.inversion_of_control.commands;

import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;
import org.example.inversion_of_control.scope.ScopeBaseDependencyResolve;

public class GetSetScopeInCurrentThreadStrategy implements IStrategy {

    @Override
    public Object execute(Object... args) throws Exception {
        return new SetScopeInCurrentThreadStrategy();
    }
}
