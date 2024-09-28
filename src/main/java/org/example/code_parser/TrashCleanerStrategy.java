package org.example.code_parser;

import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrashCleanerStrategy implements IStrategy {
    private final Pattern x = Pattern.compile("\\s+\\b(implements|class|interface|extends|)\\b\\s+");
    @Override
    public Object execute(Object... args) {
        if (args[0] == null) {
            return null;
        }
        Matcher v = this.x.matcher(IoC.caster.cast(args[0]));
        if (v.find()) {
            return v.replaceAll("");
        }
        return null;
    }
}
