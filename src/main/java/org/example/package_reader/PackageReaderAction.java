package org.example.package_reader;

import org.example.interfaces.IAction;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// Потокобезопасность - класс соответствует своей классификации при многопоточности для любого состояния

public class PackageReaderAction implements IAction {
    PackageReaderParams params;
    List<File> accumulator = new ArrayList<>();
    private File currentAcc = null;

    public PackageReaderAction(PackageReaderParams params) {
        this.params = params;
    }

    @Override
    public Object action() {
        File f = this.currentAcc ==null ? new File(this.params.packagePath) : this.currentAcc;
        accumulator.addAll(Arrays.asList(Objects.requireNonNull(f.listFiles(File::isFile))));
        Arrays.asList(Objects.requireNonNull(f.listFiles(File::isDirectory))).forEach(x->{
            this.currentAcc = x;
            this.action();
        });
        return accumulator;
    }
}
