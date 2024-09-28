package org.example.depenndance_tree;

import org.example.code_parser.CodeDescriptionEntity;
import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.List;

public class AddImplementationsToDependencyTreeStrategy implements IStrategy {
    @Override
    public Object execute(Object... args) {
        CodeDescriptionEntity codeDescriptionEntity = (CodeDescriptionEntity) args[0];

        codeDescriptionEntity.classImplements.forEach(x -> {
            AbstractList<String> v = IoC.resolve("Variables.GetFromDependenceTree", x);
            if (v == null) {
                v = IoC.resolve("Variables.Create.List");
            }
            v.add(codeDescriptionEntity.className);
            IoC.resolve("Variables.AddToDependenceTree", x, v);
        });
        return null;
    }
}
