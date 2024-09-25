package org.example.code_reader;

import org.example.interfaces.IAction;

import java.io.IOException;
import java.nio.file.Files;

public class CodeReader implements IAction {
    private final CodeReaderParams params;

    public CodeReader(CodeReaderParams params) {
        this.params = params;
    }
    @Override
    public Object action() {
       try {
           return Files.readAllLines(params.file.toPath(), params.charset);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
    }
}
