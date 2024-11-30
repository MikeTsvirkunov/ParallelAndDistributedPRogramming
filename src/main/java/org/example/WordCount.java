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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

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

    public static void initScope(){
        new InitIoCStrategy().execute();
        IScope scopeRoot = IoC.resolve("Scopes.Root");
        IScope scopeCurrent = IoC.resolve("Scopes.New", scopeRoot);
        IoC.<IStrategy>resolve("Scopes.Current.Set", scopeCurrent).execute(scopeCurrent);
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.EchoStrategy", new DefaultStrategy(x -> x));
    }

    public static void initPackageParser(){
        IoC.<IStrategy>resolve("IoC.Register", "Strategies.CodeParser.PackageReaderStrategy", new PackageReaderStrategy());
        IoC.<IStrategy>resolve("IoC.Register", "Variables.SourcePaths.Add", new DefaultStrategy(
                x -> {
                    var sourcePaths = IoC.<List<File>>resolve("Globals.listOfFiles");
                    var lof = IoC.caster.<Iterable<File>>cast(x[0]);
                    lof.forEach(sourcePaths::add);
                    return null;
                }
        ));
    }


    public static class TokenizerMapper extends Mapper<Object, Text, Text, Text>{
        private final static Text childrenClassName = new Text();
        private final static Text parentClassName = new Text();
        private final Pattern childClassPattern = Pattern.compile(
                "\\s*\\b(class|interface)\\b\\s+[A-Z]+\\w*"
        );
        private final Pattern implementsClassPattern = Pattern.compile(
                "\\s*implements\\s+[\\w\\s,]+"
        );
        private final Pattern extendsClassPattern = Pattern.compile(
                "\\s*extends\\s+\\w+"
        );
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            var x = value.toString();
            x = x.replaceAll("<[\\w,\\s]+>", "");
            x = x.replaceAll("<\\s*,\\s*>", ",");

            Matcher childClassMatcher = childClassPattern.matcher(x);
            Matcher implementsClassMatcher = implementsClassPattern.matcher(x);
            Matcher extendsClassMatcher = extendsClassPattern.matcher(x);

            if (childClassMatcher.find()){
                var child = childClassMatcher.group();
                var cleanChild = child.replaceAll("\\s*class\\s+", "").replaceAll("\\s*interface\\s+", "").replaceAll("\\s+", "");
                context.write(
                    new Text(cleanChild),
                    new Text("")
                );
                if (extendsClassMatcher.find()){
                    var extendsClass = extendsClassMatcher.group();
                    var cleanExtendsClass = extendsClass.replaceAll("\\s*extends\\s+", "").replaceAll("\\s+", "");
                    context.write(
                        new Text(cleanExtendsClass),
                        new Text(cleanChild)
                    );
                }
                if (implementsClassMatcher.find()){
                    var implementsClasses = implementsClassMatcher.group();
                    Arrays.stream(implementsClasses
                            .replaceAll("\\s*implements\\s+", "")
                            .replaceAll("\\s+", "")
                            .split(",")).forEach(a -> {
                        try {
                            context.write(new Text(a), new Text(cleanChild));
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        }
    }

    public static class IntSumReducer extends Reducer<Text,Text,Text,Text> {
        private Text result = new Text();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuilder sum = new StringBuilder();
            for (Text val : values) {
                sum.append(" ");
                sum.append(val.toString());
            }
            result.set(sum.toString().replaceAll("\\s+", " "));
            context.write(key, result);
        }
    }




    public static void main(String[] args) throws Exception {
        initScope();
        initConstants();
        initCreators();
        initPackageParser();

        var listOfFiles = IoC.<Iterable<File>>resolve("Variables.Create.List");
        IoC.resolve("IoC.Register", "Globals.listOfFiles", new DefaultStrategy(x -> listOfFiles));
        String rootFilePath = IoC.resolve("Constants.Path");
        File rootFile = IoC.resolve("Variables.Create.File", rootFilePath);
        IoC.resolve("Strategies.CodeParser.PackageReaderStrategy", rootFile);

        Configuration conf = new Configuration();
        conf.setBoolean("mapreduce.input.fileinputformat.input.dir.recursive", true);
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(rootFilePath));
//        listOfFiles.forEach(a -> {
//            try {
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });

        FileOutputFormat.setOutputPath(job, new Path("/home/mike/Downloads/message__processed.txt"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
