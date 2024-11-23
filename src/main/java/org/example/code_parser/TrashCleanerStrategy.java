package org.example.code_parser;

import org.example.interfaces.IStrategy;
import org.example.inversion_of_control.IoC;

import java.awt.font.NumericShaper;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrashCleanerStrategy implements IStrategy {
    private final Pattern x = Pattern.compile("\\s+\\b(class|interface|implements|extends)\\b\\s+\\w+");
    @Override
    public Object execute(Object... args) {
        if (args[0] == null) {
            return IoC.resolve("Constants.EmptyCodePart");
        }
        String text = IoC.caster.cast(args[0]);
        String s1 = "//.*";
        String s2 = "\\s+\\*.*";
//        Pattern s2 = Pattern.compile("/\\*([^*]|(\\*+[^*/]))*\\*+/", Pattern.DOTALL | Pattern.MULTILINE);
//        Pattern s3 = Pattern.compile("\\{([^*]|(\\*+[^*]))*}", Pattern.DOTALL | Pattern.MULTILINE);
        text = text.replaceAll(s1, "");
        text = text.replaceAll(s2, "");
//        text = s2.matcher(text).replaceAll("");
//        text = s3.matcher(text).replaceAll("");
        text = text.split("\\{")[0];
        Matcher v = this.x.matcher(text);
        if (v.find()) {
            return text;
        }
        return IoC.resolve("Constants.EmptyCodePart");
    }
}
