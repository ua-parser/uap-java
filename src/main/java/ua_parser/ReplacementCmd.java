package ua_parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Per: https://github.com/ua-parser/uap-core/blob/master/docs/specification.md
 *
 * Replacements to OS, Device  can be literal like "NT" or Regex Backreferences
 * like $1, $2 or actually nothing, in which case the replcement can be extracted
 * directly from the main regex match
 *
 * This class returns -given a matcher- what ua parser calls a replacement, the
 * "value" assigned to that field given a match for a userAgent for OS or
 * Device
 */
public class ReplacementCmd {

    private static final Pattern SUBSTITUTIONS_PATTERN = Pattern.compile("\\$\\d");

    private static ReplacementCmd instance = new ReplacementCmd();

    public static ReplacementCmd getInstance(){
        return instance;
    }

    /**
     * Given an OS replacement like "NT" or "$1-S2" returns a value from the
     * matcher
     * Example:
     * uastring: "Mozilla/5.0 (Windows; U; Win95; en-US; rv:1.1) Gecko/20020826"
     * regex: 'Win(95|98|3.1|NT|ME|2000)'
     * replacement: 'Windows $1'
     * result of this method: 'Windows 95'
     *
     * @param replacement
     * @param matcher
     * @param groupCount
     *
     * @return
     */
    public String execute(String replacement, Matcher matcher, int groupCount){

        String value = null;
        if (replacement != null) {
            // if there is a replcement given , explore what it might be
            if (!replacement.contains("$")) {
                // this is a literal replacement
                value = replacement;
            } else {
                // this must be a regex backreference
                value = getValueForRegex(replacement, matcher);
            }
        } else if (matcher.groupCount() >= groupCount) {
            // the main regex migh have "()", return corresponding match
            value = matcher.group(groupCount);
        }
        return value;
    }


    private String getValueForRegex(String replacement, Matcher matcher){

        String value = replacement;

        for (String substitution : splitRegexBackReferences(replacement)) {

            // from $1 gets '1'
            int backreference = Integer.valueOf(substitution.substring(1));

            String _replacement = matcher.groupCount() >= backreference && matcher.group(backreference) != null
                ? Matcher.quoteReplacement(matcher.group(backreference)) : "";
            value = value.replaceFirst("\\" + substitution, _replacement);
        }
        return value.trim();
    }

    /**
     * @param replacement String like "$1 - $2 "
     *
     * @return list($1, $2) where $1 and $2 are String literals
     */
    private List<String> splitRegexBackReferences(String replacement){
        Matcher matcher = SUBSTITUTIONS_PATTERN.matcher(replacement);
        List<String> substitutions = new ArrayList<String>();
        while (matcher.find()) {
            substitutions.add(matcher.group());
        }
        return substitutions;
    }
}
