package org.example.inversion_of_control.scope;

import org.example.interfaces.IScope;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ScopeBaseDependencyResolve {
    public static Scope root;
    public static Supplier<Object> defaultScope = () -> root;
    public static ThreadLocal<Scope> currentScope = new ThreadLocal<>();

    public static Object resolve(String key, Object... args) throws Exception {
        if (Objects.equals(key, "Scopes.Root")){
            return root;
        }
        Scope scope = currentScope.get();
        if (scope == null){
            scope = (Scope)defaultScope.get();
        }
        return scope.resolve(key, args);
    }
}
