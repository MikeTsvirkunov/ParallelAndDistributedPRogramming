package org.example.code_parser;

import java.io.File;
import java.util.List;

public class CodeDescriptionEntity {
    public String className;
    public String classExtends;
    public List<String> classImplements;
    public File sourceFile;
    public String codeText;
}
