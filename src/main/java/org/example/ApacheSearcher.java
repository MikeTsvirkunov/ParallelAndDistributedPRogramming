package org.example;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.example.code_parser.CodeDescriptionEntity;
import org.sparkproject.jetty.server.session.SessionContext;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ApacheSearcher
{
    static Pattern childClassPattern = Pattern.compile("\\s*\\b(class|interface)\\b\\s+[A-Z]+\\w*");
    static Pattern implementsClassPattern = Pattern.compile("\\s*implements\\s+[\\w\\s,]+");
    static Pattern extendsClassPattern = Pattern.compile("\\s*extends\\s+\\w+");


    public static CodeDescriptionEntity mapperFromCodeToCDE(String code) {
        code = code.replaceAll("<[\\w,\\s]+>", "");
        code = code.replaceAll("<\\s*,\\s*>", ",");
        Matcher childClassMatcher = childClassPattern.matcher(code);
        Matcher implementsClassMatcher = implementsClassPattern.matcher(code);
        Matcher extendsClassMatcher = extendsClassPattern.matcher(code);
        var cde = new CodeDescriptionEntity();
        cde.className = "None";
        cde.classImplements = List.of(new String[]{"None"});
        cde.classExtends = "None";
        if (childClassMatcher.find()){
            cde.className = childClassMatcher
                    .group()
                    .replaceAll("\\s*class\\s+", "")
                    .replaceAll("\\s*interface\\s+", "")
                    .replaceAll("\\s+", "");

            if (extendsClassMatcher.find()){
                cde.classExtends = extendsClassMatcher
                        .group()
                        .replaceAll("\\s*extends\\s+", "")
                        .replaceAll("\\s+", "");

            }
            if (implementsClassMatcher.find()){
                cde.classImplements = List.of(implementsClassMatcher.group()
                        .replaceAll("\\s*implements\\s+", "")
                        .replaceAll("\\s+", "")
                        .split(","));
            }
        }
        return cde;
    }


    public static AbstractMap<String, List<String>> mapperFromCDEToMap(CodeDescriptionEntity cde){
        var map = new HashMap<String, List<String>>();
        var child = List.of(new String[]{cde.className,});
        map.put(cde.classExtends, child);
        cde.classImplements.forEach(
            (x) -> {
                map.put(x, child);
            }
        );
        return map;
    }


    public static boolean lineFilter(String line){
        Matcher childClassMatcher = childClassPattern.matcher(line);
        return childClassMatcher.find();
    }


    public static void main(String[] args) {
//        System.setProperty("hadoop.home.dir", "/home/mike/spark_test");
//        SparkConf sparkConf = new SparkConf().setAppName("spark.TestSpark")
//                .setMaster("spark://10.1.50.165:7077").set("textinputformat.record.delimiter","\n");
////                .setJars(jars);
//
//        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        SparkSession spark = SparkSession
                .builder()
                .appName("JavaWordCount")
                .config("spark.master", "local")
                .getOrCreate();

        JavaRDD<String> lines = spark.read()
                .option("recursiveFileLookup", "true")
                .textFile("/home/mike/Downloads/spring-framework-6.1.13/")
                .javaRDD();
        var filteredLines = lines.filter(ApacheSearcher::lineFilter);
        var mappedFromCodeToCDE = filteredLines.map(ApacheSearcher::mapperFromCodeToCDE);
        var mappedFromCDEToMap = mappedFromCodeToCDE.map(ApacheSearcher::mapperFromCDEToMap);

        var reduced = mappedFromCDEToMap.reduce((a, b) -> {
            HashMap<String, List<String>> concat = new HashMap<>(a);
            b.forEach((k, v) -> concat.merge(k, v, (v1, v2) -> Stream.concat(v1.stream(), v2.stream()).toList()));
            return concat;
        });
        reduced.forEach((a, b) -> System.out.println(a + ": " + b));
        spark.stop();
    }
}
