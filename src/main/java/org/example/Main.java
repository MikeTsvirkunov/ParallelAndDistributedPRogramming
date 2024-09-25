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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class S1 implements IStrategy{
    @Override
    public Object execute(Object... args) throws Exception {
        return new S2().execute(args);
    }
}

class S2 implements IStrategy{
    @Override
    public Object execute(Object... args) throws Exception {
        return args;
    }
}
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try{
//            S2 s2 = new S2();
//            Object x0 = s2.execute(1,2,3,4);
//            Object[] x1 = (Object[])x0;
//
//            Arrays.stream(x1).forEach(System.out::println);
            new InitIoCStrategy().execute();
            System.out.println("Success InitIoCStrategy!");
            IScope scopeRoot = IoC.resolve("Scopes.Root");
            System.out.println("Success scopeRoot get!");
            IScope scopeCurrent = IoC.resolve("Scopes.New", scopeRoot);
            System.out.println("Success scopeCurrent get!");
            IStrategy setUpStrategy = IoC.resolve("Scopes.Current.Set", scopeCurrent);
            System.out.println("Success setUpStrategy get!");
            setUpStrategy.execute(scopeCurrent);
            System.out.println("Success setUpStrategy execute!");
            IStrategy registerStrategy = IoC.<IStrategy>resolve("IoC.Register", "Strategies.EchoStrategy", new DefaultStrategy(x -> x)); // .execute();
            Object echoStrategy = IoC.resolve("Strategies.EchoStrategy", "asd");
            System.out.println(echoStrategy);
//            System.out.println("Success registration execute!");
//            IStrategy echoStrategy = IoC.<IStrategy>resolve("Strategies.EchoStrategy");
//            System.out.println("Success echoStrategy get!");
//            Object x = echoStrategy.execute("Echo");
//            System.out.println("Success echoStrategy execute!");
//            System.out.println(x.toString());

        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
//        PackageReaderParams x0 = new PackageReaderParams(StandardCharsets.UTF_8, "./src/main/");
//        PackageReaderAction x1 = new PackageReaderAction(x0);
//        Object r0 = x1.action();
//        DataTypeCaster dtc = new DataTypeCaster();
//        HashMap<String, IStrategy> hmcp = new HashMap<String, IStrategy>();
//        hmcp.put("className", new GetClassNameStrategy());
//        CodeParserAction a0 = new CodeParserAction(hmcp);
//        List<String> r1 = (dtc.<List<File>>cast(r0)).stream()
//                .map(x -> new CodeReader(new CodeReaderParams(x, StandardCharsets.UTF_8)).action())
//                .map(x -> String.join("\n", dtc.<List<String>>cast(x))).toList();
//        r1.forEach(x -> System.out.println(a0.execute(x)));


    }
}