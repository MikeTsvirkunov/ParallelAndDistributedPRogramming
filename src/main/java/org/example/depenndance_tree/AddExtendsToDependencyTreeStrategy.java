package org.example.depenndance_tree;

import org.example.code_parser.CodeDescriptionEntity;
import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;

import java.util.AbstractList;
import java.util.AbstractMap;

public class AddExtendsToDependencyTreeStrategy implements IStrategy {
    @Override
    public Object execute(Object... args) {
        CodeDescriptionEntity codeDescriptionEntity = IoC.caster.cast(args[0]);
        AbstractList<String> v = IoC.resolve("Variables.GetFromDependenceTree", codeDescriptionEntity.classExtends);
        v.add(codeDescriptionEntity.className);
        IoC.resolve("Variables.AddToDependenceTree", codeDescriptionEntity.classExtends, v);
        return null;
    }
}
