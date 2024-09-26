package org.example.inversion_of_control.commands;

import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.datatype_caters.DefaultDataTypeCaster;

public class DefaultResolveStrategy implements IStrategy {
    @Override
    public Object execute(Object... args) throws Exception {
        if ("IoC.Default.ResolveStrategy".equals(args[0])) {
            return new DefaultResolveStrategy();
        }
        if ("IoC.DataTypeCaster".equals(args[0])) {
            return new DefaultDataTypeCaster();
        }
        if ("IoC.SetupStrategy".equals(args[0])) {
            return new SetupStrategyStrategy();
        }
        throw new IllegalArgumentException("Unknown dependency " + args[0]);
    }
}
