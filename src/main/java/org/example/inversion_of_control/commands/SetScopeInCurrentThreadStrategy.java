package org.example.inversion_of_control.commands;
import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;
import org.example.inversion_of_control.scope.Scope;
import org.example.inversion_of_control.scope.ScopeBaseDependencyResolve;


public class SetScopeInCurrentThreadStrategy implements IStrategy {

    @Override
    public Object execute(Object... args) throws Exception {
        ScopeBaseDependencyResolve.currentScope.set(IoC.caster.cast(args[0]));
        return null;
    }
}