package org.example;

import org.example.code_parser.CodeParserAction;
import org.example.code_parser.GetClassNameStrategy;
import org.example.code_reader.CodeReader;
import org.example.code_reader.CodeReaderParams;
import org.example.datatype_caster.DataTypeCaster;
import org.example.interfaces.IScope;
import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;
import org.example.inversion_of_control.commands.DefaultStrategy;
import org.example.inversion_of_control.commands.InitIoCStrategy;
import org.example.package_reader.PackageReaderAction;
import org.example.package_reader.PackageReaderParams;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        new InitIoCStrategy().execute();
        IScope scopeRoot = IoC.resolve("Scopes.Root");
        IScope scopeCurrent = IoC.resolve("Scopes.New", scopeRoot);
        IoC.<IStrategy>resolve("Scopes.Current.Set", scopeCurrent).execute(scopeCurrent);
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.EchoStrategy", new DefaultStrategy(x -> x));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetClassNameStrategy", new GetClassNameStrategy());
//        Object[] echoStrategy = IoC.resolve("Strategies.EchoStrategy", "asd", "ggg");
//        Arrays.stream(echoStrategy).forEach(System.out::println);

        PackageReaderParams x0 = new PackageReaderParams(StandardCharsets.UTF_8, "./src/main/");
        PackageReaderAction x1 = new PackageReaderAction(x0);
        Object r0 = x1.action();
        HashMap<String, IStrategy> hmcp = new HashMap<String, IStrategy>();
        hmcp.put("className", new GetClassNameStrategy());
        CodeParserAction a0 = new CodeParserAction(hmcp);
        List<String> r1 = (IoC.caster.<List<File>>cast(r0)).stream()
                .map(x -> new CodeReader(new CodeReaderParams(x, StandardCharsets.UTF_8)).action())
                .map(x -> String.join("\n", IoC.caster.<List<String>>cast(x))).toList();
        r1.forEach(x -> System.out.println(a0.execute(x)));


    }
}