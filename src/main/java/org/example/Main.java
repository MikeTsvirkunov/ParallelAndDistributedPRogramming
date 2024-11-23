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
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.regex.Pattern;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

public class Main {


    public static void initConstants() {
        IoC.<IStrategy>resolve("IoC.Register", "Constants.Path", new DefaultStrategy(x -> "/home/mike/Downloads/spring-framework-6.1.13/"));
        IoC.<IStrategy>resolve("IoC.Register", "Constants.EmptyCodePart", new DefaultStrategy(x -> "None"));
        IoC.<IStrategy>resolve("IoC.Register", "Constants.Charset", new DefaultStrategy(
                x -> StandardCharsets.UTF_8
        ));
        IoC.resolve("IoC.Register", "Constants.SizeOfPipeline", new DefaultStrategy(x -> 5));
    }


    public static void initCreators(){
        IoC.<IStrategy>resolve("IoC.Register", "Variables.Create.File", new DefaultStrategy(
                x -> new File(IoC.caster.<String>cast(x[0]))
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Variables.Create.List", new DefaultStrategy(x -> new ArrayList<Object>(List.of())));
        IoC.<IStrategy>resolve("IoC.Register", "Variables.Create.HashMap", new DefaultStrategy(x -> new HashMap<>()));
        IoC.<IStrategy>resolve("IoC.Register", "Variables.Create.BlockingQueue.Default", new DefaultStrategy(
            x -> {
                var sizeOfQueue = IoC.<Integer>resolve("Constants.SizeOfPipeline");
                return new LinkedBlockingQueue<>(sizeOfQueue);
            }
        ));

    }


    public static void initCodeParser(){
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetClassNameText", new GetPatternStrategy(
                Pattern.compile("\\s+\\b(class|interface)\\b\\s+\\w+")
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetClassNameStrategy", new DefaultStrategy(
                x -> {
                    String imp = IoC.resolve("Strategies.CodeParser.GetClassNameText", x[0].toString());
                    return imp.replaceAll("\\s*class\\s+", "")
                            .replaceAll("\\s*implements\\s+", "")
                            .replaceAll("\\s*extends\\s+", "")
                            .replaceAll("\\s+", "");
                }
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetImplementationText", new GetPatternStrategy(
                Pattern.compile("\\s+implements\\s+\\w+(,\\s+\\w+)*")
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetImplementationStrategy", new DefaultStrategy(
                x -> {
                    String imp = IoC.resolve("Strategies.CodeParser.GetImplementationText", x[0].toString());
                    imp = imp.replaceAll("\\s*class\\s+", "")
                            .replaceAll("\\s*implements\\s+", "")
                            .replaceAll("\\s*extends\\s+", "");
                    return Arrays.stream(imp.split(",\\s+")).map(a -> a.replaceAll("\\s+", "")).toList();
                }
        ));

        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetExtendsText", new GetPatternStrategy(
                Pattern.compile("\\s+extends\\s+\\w+")
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.GetExtendsStrategy", new DefaultStrategy(
            x -> {
                String ext = IoC.resolve("Strategies.CodeParser.GetExtendsText", x[0].toString());
                return ext.replaceAll("\\s*class\\s+", "")
                        .replaceAll("\\s*implements\\s+", "")
                        .replaceAll("\\s*extends\\s+", "")
                        .replaceAll("\\s+", "");
            }
        ));
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.ParseCodeFileStrategy", new ParseCodeFileStrategy());
    }


    public static void initPackageParser(){
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.PackageReaderStrategy", new PackageReaderStrategy());
        IoC.<IStrategy>resolve("IoC.Register", "Variables.SourcePaths.Add", new DefaultStrategy(
                x -> {
                    var sourcePaths = IoC.<BlockingQueue<Callable<File>>>resolve("Pipelines.CodeReader");
                    var lof = IoC.caster.<Iterable<File>>cast(x[0]);
                    lof.forEach(
                        a -> {
                            try {
                                sourcePaths.put(
                                        () -> a
                                );
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    );
                    return null;
                }
        ));
    }


    public static  void  initCodeReader(){
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.CodeReader", new CodeReader());
    }


    public static void initCodeCleaner(){
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.TrashCleaner", new TrashCleanerStrategy());
    }


    public static void initTreeExtender(){
        IoC.<IStrategy>resolve("IoC.Register", "Variables.AddToDependenceTree", new DefaultStrategy(
            x -> {
                HashMap<String, AbstractList<String>> dependenciesTree = IoC.resolve("Variables.GetDependenceTree");
                dependenciesTree.put(IoC.caster.cast(x[0]), IoC.caster.cast(x[1]));
                return null;
            }
        ));
    }


    public static void initScope(){
        new InitIoCStrategy().execute();
        IScope scopeRoot = IoC.resolve("Scopes.Root");
        IScope scopeCurrent = IoC.resolve("Scopes.New", scopeRoot);
        IoC.<IStrategy>resolve("Scopes.Current.Set", scopeCurrent).execute(scopeCurrent);
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.EchoStrategy", new DefaultStrategy(x -> x));
    }


    public static void main(String[] args) {
        initScope();
        initConstants();
        initCreators();
        initPackageParser();

        // фон нейман полиморфные проги
        BlockingQueue<Callable<File>> pipelineOfCodeReader = IoC.resolve("Variables.Create.BlockingQueue.Default");
        BlockingQueue<Callable<String>> pipelineOfCodeCleaner = IoC.resolve("Variables.Create.BlockingQueue.Default");
        BlockingQueue<Callable<String>> pipelineOfCodeParser = IoC.resolve("Variables.Create.BlockingQueue.Default");
        BlockingQueue<Callable<CodeDescriptionEntity>> pipelineOfTreeExtender = IoC.resolve("Variables.Create.BlockingQueue.Default");

        HashMap<String, Collection<String>> codeTree = new HashMap<>();

        IoC.resolve("IoC.Register", "Pipelines.CodeReader", new DefaultStrategy(x -> pipelineOfCodeReader));
        String rootFilePath = IoC.resolve("Constants.Path");
        File rootFile = IoC.resolve("Variables.Create.File", rootFilePath);


        Runnable pipelineCodeReaderWorker = () -> {
            initScope();
            initConstants();
            initCreators();
            initCodeReader();
            while (true) {
                try {
                    var fileToRead = pipelineOfCodeReader.take();
                    File f = fileToRead.call();
                    String code = IoC.resolve("Strategies.CodeParser.CodeReader", f);
                    pipelineOfCodeCleaner.put(
                            () -> code
                    );
                } catch (InterruptedException e) {
                    try {
                        pipelineOfCodeParser.put(
                                () -> {
                                    throw new InterruptedException();
                                }
                        );
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };


        Runnable pipelineCodeCleanerWorker = () -> {
            initScope();
            initConstants();
            initCreators();
            initCodeCleaner();
            while (true) {
                try {
                    String cleanCode = IoC.resolve(
                    "Strategies.CodeParser.TrashCleaner",
                        pipelineOfCodeCleaner.take().call()
                    );
                    pipelineOfCodeParser.put(
                            () -> cleanCode
                    );
                } catch (InterruptedException e) {
                    try {
                        pipelineOfCodeParser.put(
                            () -> {
                                throw new InterruptedException();
                            }
                        );
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };


        Runnable pipelineCodeParserWorker = () -> {
            initScope();
            initConstants();
            initCreators();
            initCodeParser();
            while (true) {
                try {
                    CodeDescriptionEntity cde = IoC.resolve(
                            "Strategies.CodeParser.ParseCodeFileStrategy",
                            pipelineOfCodeParser.take().call()
                    );
                    pipelineOfTreeExtender.put(
                            () -> cde
                    );
                } catch (InterruptedException e) {
                    try {
                        pipelineOfTreeExtender.put(
                                () -> {
                                    throw new InterruptedException();
                                }
                        );
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };


        Runnable pipelineOfTreeExtenderWorker = () -> {
            initScope();
            initConstants();
            initCreators();
            initTreeExtender();
            while (true) {
                try {
                    var cdeToTree = pipelineOfTreeExtender.take().call();
                    var listOfParents = codeTree.getOrDefault(cdeToTree.className, IoC.resolve("Variables.Create.List"));
                    listOfParents.addAll(cdeToTree.classImplements.stream().filter(a -> !Objects.equals(a, "None")).toList());
                    if (!Objects.equals(cdeToTree.classExtends, "None")){
                        listOfParents.add(cdeToTree.classExtends);
                    }
                    codeTree.put(cdeToTree.className, listOfParents);
                } catch (InterruptedException e) {
                    codeTree.forEach(
                            (k, v) -> System.out.println(k + ": " + v)
                    );
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        var firstWorkerThread = new Thread(pipelineCodeReaderWorker);
        var secondWorkerThread = new Thread(pipelineCodeCleanerWorker);
        var thirdWorkerThread = new Thread(pipelineCodeParserWorker);
        var fourthWorkerThread = new Thread(pipelineOfTreeExtenderWorker);
        firstWorkerThread.start();
        secondWorkerThread.start();
        thirdWorkerThread.start();
        fourthWorkerThread.start();
        IoC.resolve("Strategies.CodeParser.PackageReaderStrategy", rootFile);
        try {
            pipelineOfCodeReader.put(
                    () -> {
                        throw new InterruptedException();
                    }
            );
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("eow");
    }
}
