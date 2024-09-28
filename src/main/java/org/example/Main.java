package org.example;

import org.example.code_parser.CodeDescriptionEntity;
import org.example.code_parser.GetPatternStrategy;
import org.example.code_parser.ParseCodeFileStrategy;
import org.example.code_parser.TrashCleanerStrategy;
import org.example.code_reader.CodeReader;
import org.example.depenndance_tree.AddExtendsToDependencyTreeStrategy;
import org.example.depenndance_tree.AddImplementationsToDependencyTreeStrategy;
import org.example.depenndance_tree.PrintDependencyTreeStrategy;
import org.example.interfaces.IScope;
import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;
import org.example.inversion_of_control.commands.DefaultStrategy;
import org.example.inversion_of_control.commands.InitIoCStrategy;
import org.example.package_reader.PackageReaderParams;
import org.example.package_reader.PackageReaderStrategy;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void initScope(){
        List<File> sourcePaths = new ArrayList<File>(List.of());
        HashMap<String, List<String>> dependenciesTree = new HashMap<String, List<String>>();

        new InitIoCStrategy().execute();
        IScope scopeRoot = IoC.resolve("Scopes.Root");
        IScope scopeCurrent = IoC.resolve("Scopes.New", scopeRoot);
        IoC.<IStrategy>resolve("Scopes.Current.Set", scopeCurrent).execute(scopeCurrent);
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.EchoStrategy", new DefaultStrategy(x -> x));

        IoC.<IStrategy>resolve("IoC.Register", "Constants.Path", new DefaultStrategy(x -> "./src/main/"));
        IoC.<IStrategy>resolve("IoC.Register", "Constants.Charset", new DefaultStrategy(x -> StandardCharsets.UTF_8));
        IoC.<IStrategy>resolve("IoC.Register", "Variables.GetSourcesPaths", new DefaultStrategy(x -> sourcePaths));
        IoC.<IStrategy>resolve("IoC.Register", "Variables.GetDependenceTree", new DefaultStrategy(x -> dependenciesTree));
        IoC.<IStrategy>resolve("IoC.Register", "Variables.AddToDependenceTree", new DefaultStrategy(x -> {
            dependenciesTree.put(IoC.caster.cast(x[0]), IoC.caster.cast(x[1]));
            return null;
        }));
        IoC.<IStrategy>resolve("IoC.Register", "Variables.GetFromDependenceTree", new DefaultStrategy(x -> dependenciesTree.getOrDefault(IoC.caster.<String>cast(x[0]), null)));
        IoC.resolve("IoC.Register", "Variables.AddToSourcePaths", new DefaultStrategy(x0 -> {
            sourcePaths.addAll(IoC.caster.cast(x0[0]));
            return null;
        }));


        IoC.<IStrategy>resolve("IoC.Register", "Variables.Create.List", new DefaultStrategy(x -> new ArrayList<Object>(List.of())));

        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.PackageReaderStrategy", new PackageReaderStrategy());
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.CodeReader", new CodeReader());
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.TrashCleaner", new TrashCleanerStrategy());
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetClassNameStrategy", new GetPatternStrategy("\\s+\\b(class|interface)\\b\\s+\\w+"));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetImplementationText", new GetPatternStrategy("\\s+implements\\s+\\w+(,\\s+\\w+)*"));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetImplementationStrategy", new DefaultStrategy(x -> {
            String imp = IoC.resolve("Strategies.CodeParser.GetImplementationText", x);
            return Arrays.asList(imp.split(",\\s+"));
        }));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetExtendsStrategy", new GetPatternStrategy("\\s+extends\\s+\\w+"));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.ParseCodeFileStrategy", new ParseCodeFileStrategy());

        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.AddExtendsToDependencyTreeStrategy", new AddExtendsToDependencyTreeStrategy());
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.AddImplementationsToDependencyTreeStrategy", new AddImplementationsToDependencyTreeStrategy());
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.PrintDependencyTreeStrategy", new PrintDependencyTreeStrategy());
    }


    public static void main(String[] args) {

        initScope();

        List<File> sourcePaths = IoC.resolve("Variables.GetSourcesPaths");
        AbstractMap<String, List<String>> dependenciesTree = IoC.resolve("Variables.GetDependenceTree");
        IoC.resolve("Strategies.CodeParser.PackageReaderStrategy", new File("./src/main/"));

        List<CodeDescriptionEntity> codeDescriptions = new ArrayList<CodeDescriptionEntity>(List.of());
        sourcePaths.forEach(x -> codeDescriptions.add(IoC.resolve("Strategies.CodeParser.ParseCodeFileStrategy", x)));

        codeDescriptions.forEach(
            x0 -> {
                IoC.resolve("Strategies.CodeParser.AddImplementationsToDependencyTreeStrategy", x0);
                IoC.resolve("Strategies.CodeParser.AddExtendsToDependencyTreeStrategy", x0);
            }
        );
        dependenciesTree.remove(null);
        dependenciesTree.keySet().forEach(x -> new PrintDependencyTreeStrategy().execute(x, null));
    }
}