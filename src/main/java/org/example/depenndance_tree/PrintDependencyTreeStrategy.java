package org.example.depenndance_tree;

import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.List;

public class PrintDependencyTreeStrategy implements IStrategy {
    @Override
    public Object execute(Object... args) {
        String k = IoC.caster.cast(args[0]);
        String s = IoC.caster.cast(args[1]);

        AbstractList<String> v = IoC.resolve("Variables.GetFromDependenceTree", k);
        if (v != null){
            v.forEach(x -> IoC.resolve("Strategies.CodeParser.PrintDependencyTreeStrategy", x, ((s != null) ? s : k) + " -> " + x));
        }
        if (s != null){
            System.out.println(s);
        }
        return null;
    }
}
