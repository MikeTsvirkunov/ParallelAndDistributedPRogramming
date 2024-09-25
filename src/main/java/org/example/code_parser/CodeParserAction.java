package org.example.code_parser;

import org.example.interfaces.IAction;
import org.example.interfaces.IStrategy;

import javax.swing.*;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

public class CodeParserAction implements IStrategy {
    AbstractMap<String, IStrategy> parserMap;
    public CodeParserAction(AbstractMap<String, IStrategy> parserMap) {
        this.parserMap = parserMap;
    }

    @Override
    public Object execute(Object... args) {
        String x = String.valueOf(args[0]);
        HashMap<String, String> m = new HashMap<String, String>();
        parserMap.forEach((k, v) -> {
            try {
                m.put(k, (String) v.execute(x));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return m;
    }
}
