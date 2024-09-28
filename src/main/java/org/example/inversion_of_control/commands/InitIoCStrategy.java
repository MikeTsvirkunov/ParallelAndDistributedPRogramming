package org.example.inversion_of_control.commands;

import org.example.interfaces.IAction;
import org.example.interfaces.IScope;
import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;
import org.example.inversion_of_control.scope.LeafScope;
import org.example.inversion_of_control.scope.Scope;
import org.example.inversion_of_control.scope.ScopeBaseDependencyResolve;

import java.lang.reflect.Array;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class InitIoCStrategy implements IStrategy {
    @Override
    public Object execute(Object... args) {
        ConcurrentHashMap<String, IStrategy> cd = new ConcurrentHashMap<>();
        IStrategy defaultIoC = IoC.resolve("IoC.Default.ResolveStrategy", new Object[]{});
        IScope leafScope = new LeafScope(defaultIoC);
        Scope scope = new Scope(leafScope, cd);
        cd.put("Scopes.Storage",  new DefaultGetNewScopeStorageStrategy());
        cd.put("Scopes.New", new DefaultGetNewScopeStrategy());
        cd.put("Scopes.Current", new DefaultGetScopesCurrentStrategy());
        cd.put("Scopes.Current.Set", new GetSetScopeInCurrentThreadStrategy());
        cd.put("IoC.Register", new DefaultRegisterIoCDependencyStrategy());
        ScopeBaseDependencyResolve.root = scope;
        IoC.<IStrategy>resolve("IoC.SetupStrategy").execute(new Object[]{new ScopeBaseDependencyResolveStrategy()});
        new SetScopeInCurrentThreadStrategy().execute(scope);
        return null;
    }
}
