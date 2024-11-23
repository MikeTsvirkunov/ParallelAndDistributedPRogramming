package org.example.code_parser;

import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;

import java.util.List;

public class ParseCodeFileStrategy implements IStrategy {
    @Override
    public Object execute(Object... args) {
        CodeDescriptionEntity cde = new CodeDescriptionEntity();
        cde.codeText = IoC.caster.cast(args[0]);
        cde.className =  IoC.<String>resolve("Strategies.CodeParser.GetClassNameStrategy", cde.codeText);
        cde.classImplements = IoC.<List<String>>resolve("Strategies.CodeParser.GetImplementationStrategy", cde.codeText);
        cde.classExtends = IoC.resolve("Strategies.CodeParser.GetExtendsStrategy", cde.codeText);
        return cde;
    }
}
