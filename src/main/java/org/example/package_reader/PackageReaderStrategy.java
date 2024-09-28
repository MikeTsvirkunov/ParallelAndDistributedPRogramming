package org.example.package_reader;

import org.example.interfaces.IAction;
import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

// Потокобезопасность - класс соответствует своей классификации при многопоточности для любого состояния

public class PackageReaderStrategy implements IStrategy {

    @Override
    public Object execute(Object... args) {
        File file = IoC.caster.cast(args[0]);
        List<File> lof = Arrays.asList(Objects.requireNonNull(file.listFiles(File::isFile)));
        IoC.resolve("Variables.AddToSourcePaths", lof);
        Arrays.stream(Objects.requireNonNull(file.listFiles(File::isDirectory))).forEach(x0 -> {
            IoC.resolve("Strategies.CodeParser.PackageReaderStrategy", x0);
        });
        return null;
    }
}