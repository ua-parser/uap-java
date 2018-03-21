package ua_parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReplacementOps {
    private static final Pattern SUBSTITUTIONS_PATTERN = Pattern.compile("\\$\\d");

    public static String replace(String replacement, Matcher matcher) {
        String result = replacement;
        for (String substitution : getSubstitutions(replacement)) {
            int i = Integer.valueOf(substitution.substring(1));
            String toInsert = matcher.groupCount() >= i && matcher.group(i) != null
                    ? Matcher.quoteReplacement(matcher.group(i)) : "";

            result = result.replaceFirst("\\$" + i, toInsert);
        }
        result = result.trim();
        if ("".equals(result)) {
            result = null;
        }
        return result;
    }

    private static List<String> getSubstitutions(String replacement) {
        Matcher matcher = SUBSTITUTIONS_PATTERN.matcher(replacement);
        List<String> substitutions = new ArrayList<String>();
        while (matcher.find()) {
            substitutions.add(matcher.group());
        }
        return substitutions;
    }
}
