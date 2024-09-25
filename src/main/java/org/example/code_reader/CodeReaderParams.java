package org.example.code_reader;

import java.io.File;
import java.nio.charset.Charset;

public class CodeReaderParams {
    public File file;
    public Charset charset;
    public CodeReaderParams(File file, Charset charset) {
        this.file = file;
        this.charset = charset;
    }
}
