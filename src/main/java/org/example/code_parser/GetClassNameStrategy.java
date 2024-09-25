package org.example.code_parser;

import org.example.interfaces.IStrategy;

import java.util.regex.Pattern;

public class GetClassNameStrategy implements IStrategy {
    private final IStrategy strategy = new GetPatternStrategy("\\b(class|interface)\\b\\s+\\w+");
    @Override
    public Object execute(Object... args) throws Exception {
        String x = (String) strategy.execute(args[0]);
        return Pattern.compile("\\b(class|interface)\\b\\s+").matcher(x).replaceAll("");
//        return x.replace("class", "").replace("interface", "").replace(" ", "");
    }
}
