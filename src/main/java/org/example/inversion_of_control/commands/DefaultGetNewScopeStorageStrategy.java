package org.example.inversion_of_control.commands;

import org.example.interfaces.IScope;
import org.example.interfaces.IStrategy;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultGetNewScopeStorageStrategy implements IStrategy {
    @Override
    public Object execute(Object[] args) throws Exception {
        System.out.println("\tARGS.Length DefaultGetNewScopeStorageStrategy " + args.length);
        return new ConcurrentHashMap<String, IStrategy>();
    }
}