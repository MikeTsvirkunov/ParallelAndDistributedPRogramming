package org.example.code_parser;


import org.example.interfaces.IStrategy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetPatternStrategy implements IStrategy {

    private final Pattern pattern;
    GetPatternStrategy(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }
    @Override
    public Object execute(Object... args) {
        Matcher m = this.pattern.matcher(args[0].toString());
        return  m.find() ? m.group() : "None";
    }
}
