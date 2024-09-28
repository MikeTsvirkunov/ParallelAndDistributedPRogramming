package org.example.code_parser;

import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;

import java.util.List;

public class ParseCodeFileStrategy implements IStrategy {
    @Override
    public Object execute(Object... args) {
        CodeDescriptionEntity cde = new CodeDescriptionEntity();
        String code = IoC.resolve("Strategies.CodeParser.CodeReader", args[0]);
        cde.codeText = code;
        cde.sourceFile = IoC.caster.cast(args[0]);
        cde.className = IoC.resolve("Strategies.CodeParser.TrashCleaner", IoC.<String>resolve("Strategies.CodeParser.GetClassNameStrategy", code));
        cde.classImplements = IoC.<List<String>>resolve("Strategies.CodeParser.GetImplementationStrategy", code).stream().map(x -> IoC.<String>resolve("Strategies.CodeParser.TrashCleaner", x)).toList();
        String i = IoC.resolve("Strategies.CodeParser.GetExtendsStrategy", code);
        cde.classExtends = IoC.resolve("Strategies.CodeParser.TrashCleaner", i);
        return cde;
    }
}
