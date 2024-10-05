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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

public class Main {

    public static void initScope(AbstractSet<File> sourcePaths, AbstractMap<String, AbstractList<String>> dependenciesTree){

        new InitIoCStrategy().execute();
        IScope scopeRoot = IoC.resolve("Scopes.Root");
        IScope scopeCurrent = IoC.resolve("Scopes.New", scopeRoot);
        IoC.<IStrategy>resolve("Scopes.Current.Set", scopeCurrent).execute(scopeCurrent);
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.EchoStrategy", new DefaultStrategy(x -> x));

        IoC.<IStrategy>resolve("IoC.Register", "Constants.Path", new DefaultStrategy(x -> "/home/mike/Downloads/spring-framework-6.1.13/"));
        IoC.<IStrategy>resolve("IoC.Register", "Constants.EmptyCodePart", new DefaultStrategy(x -> "None"));
        IoC.<IStrategy>resolve("IoC.Register", "Constants.Charset", new DefaultStrategy(
                x -> StandardCharsets.UTF_8
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Variables.GetSourcesPaths", new DefaultStrategy(
                x -> sourcePaths
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Variables.GetDependenceTree", new DefaultStrategy(
                x -> dependenciesTree
        ));

        IoC.<IStrategy>resolve("IoC.Register", "Variables.Create.File", new DefaultStrategy(
                x -> new File(IoC.caster.<String>cast(x[0]))
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Variables.AddToDependenceTree", new DefaultStrategy(
                x -> {
                    dependenciesTree.put(IoC.caster.cast(x[0]), IoC.caster.cast(x[1]));
                    return null;
                }
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Variables.GetFromDependenceTree", new DefaultStrategy(
                x -> dependenciesTree.getOrDefault(IoC.caster.<String>cast(x[0]), IoC.resolve("Variables.Create.List"))
        ));
        IoC.resolve("IoC.Register", "Variables.AddToSourcePaths", new DefaultStrategy(
                x0 -> {
                    sourcePaths.addAll(IoC.caster.cast(x0[0]));
                    return null;
                }
        ));


        IoC.<IStrategy>resolve("IoC.Register", "Variables.Create.List", new DefaultStrategy(x -> new ArrayList<Object>(List.of())));

        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.PackageReaderStrategy", new PackageReaderStrategy());
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.CodeReader", new CodeReader());
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.TrashCleaner", new TrashCleanerStrategy());
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetClassNameStrategy", new GetPatternStrategy(
                "\\s+\\b(class|interface)\\b\\s+\\w+"
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetImplementationText", new GetPatternStrategy(
                "\\s+implements\\s+\\w+(,\\s+\\w+)*"
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetImplementationStrategy", new DefaultStrategy(
                x -> {
                    String imp = IoC.resolve("Strategies.CodeParser.GetImplementationText", x);
                    return Arrays.asList(imp.split(",\\s+"));
                }
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetExtendsStrategy", new GetPatternStrategy(
                "\\s+extends\\s+\\w+"
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.ParseCodeFileStrategy", new ParseCodeFileStrategy());

        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.AddExtendsToDependencyTreeStrategy", new AddExtendsToDependencyTreeStrategy());
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.AddImplementationsToDependencyTreeStrategy", new AddImplementationsToDependencyTreeStrategy());
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.PrintDependencyTreeStrategy", new PrintDependencyTreeStrategy());
        IoC.<IStrategy>resolve("IoC.Register", "Variables.Create.Thread", new DefaultStrategy(
                x0 -> new Thread(IoC.caster.<Runnable>cast(x0[0]))
        ));
    }


    public static void main(String[] args) {

        ConcurrentSkipListSet<File> sourcePaths = new ConcurrentSkipListSet<>();
        ConcurrentHashMap<String, AbstractList<String>> dependenciesTree = new ConcurrentHashMap<>();

        initScope(sourcePaths, dependenciesTree);

        String rootFilePath = IoC.resolve("Constants.Path");
        File rootFile = IoC.resolve("Variables.Create.File", rootFilePath);
        IoC.resolve("Strategies.CodeParser.PackageReaderStrategy", rootFile);

        AbstractList<CodeDescriptionEntity> codeDescriptions = IoC.resolve("Variables.Create.List");
        sourcePaths.forEach(x -> codeDescriptions.add(IoC.resolve("Strategies.CodeParser.ParseCodeFileStrategy", x)));
        List<Thread> lot = IoC.resolve("Variables.Create.List");

        ReentrantLock lock = new ReentrantLock();

        codeDescriptions.forEach(
            x0 -> {
                Runnable f = () -> {
                    lock.lock();
                    try{
                        initScope(sourcePaths, dependenciesTree);
                        IoC.resolve("Strategies.CodeParser.AddImplementationsToDependencyTreeStrategy", x0);
                        IoC.resolve("Strategies.CodeParser.AddExtendsToDependencyTreeStrategy", x0);
                    } catch (RuntimeException e) {
                        throw new RuntimeException(e);
                    }
                    finally {
                        lock.unlock();
                    }
                };
                Thread thread = IoC.resolve(
                    "Variables.Create.Thread", f
                );
                thread.start();
                lot.add(thread);
            }
        );
        lot.forEach(x -> {
            try{
                x.join();
            }
            catch (InterruptedException _){}
        });
        dependenciesTree.remove(IoC.<String>resolve("Constants.EmptyCodePart"));
        dependenciesTree.forEach((x, v) -> System.out.println(x + ": " + String.join(", ", v)));
    }
}
