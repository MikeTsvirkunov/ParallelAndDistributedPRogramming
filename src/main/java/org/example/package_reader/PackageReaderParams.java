package org.example.package_reader;

import java.nio.charset.Charset;

public class PackageReaderParams {
    String packagePath;

    public PackageReaderParams(Charset codec, String packagePath) {
        this.packagePath = packagePath;
    }
}
