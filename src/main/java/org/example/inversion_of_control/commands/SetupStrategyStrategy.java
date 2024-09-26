package org.example.inversion_of_control.commands;

import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;

public class SetupStrategyStrategy implements IStrategy {

    @Override
    public Object execute(Object... args)  {
        IoC.strategy = IoC.caster.cast(args[0]);
        return null;
    }
}
