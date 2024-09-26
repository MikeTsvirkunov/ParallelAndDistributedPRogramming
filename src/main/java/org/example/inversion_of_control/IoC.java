package org.example.inversion_of_control;

import org.example.datatype_caster.DataTypeCaster;
import org.example.interfaces.ICaster;
import org.example.interfaces.IScope;
import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.commands.DefaultResolveStrategy;
import org.example.inversion_of_control.datatype_caters.DefaultDataTypeCaster;

import java.util.Arrays;

public class IoC {
    public static IStrategy strategy = new DefaultResolveStrategy();
    public static ICaster caster = new DefaultDataTypeCaster();


    public static <T> T resolve(String key, Object... args) throws Exception {
        Object[] x0 = IoC.caster.cast(args);
        Object x1 = strategy.execute(key, x0);
        return caster.cast(x1);
    }
}
