package org.example.code_parser;


import org.example.interfaces.IStrategy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetPatternStrategy implements IStrategy {

    private final Pattern pattern;
    public GetPatternStrategy(Pattern pattern) {
        this.pattern = pattern;
    }
    @Override
    public Object execute(Object... args) {
        Matcher m = this.pattern.matcher(args[0].toString());
        if (m.find()){
            return m.group();
        } else {
            return "None";
        }
//        return  ? m.group() : "None";
    }
}
