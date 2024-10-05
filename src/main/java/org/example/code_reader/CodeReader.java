package org.example.code_reader;

import org.example.interfaces.IAction;
import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class CodeReader implements IStrategy {
    @Override
    public Object execute(Object... args) {
        try {
            File file = IoC.caster.cast(args[0]);
            Path path = file.toPath();
            Charset charset = IoC.resolve("Constants.Charset");
            String s1 = "/+.*";
            String s2 = "\\*.+";
            return Files.readString(path, charset).replaceAll(s1, "").replaceAll(s2, "");
       } catch (IOException e) {
           throw new RuntimeException("CodeReader: " + e);
       }
    }
}
