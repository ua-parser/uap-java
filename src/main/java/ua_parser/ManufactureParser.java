package ua_parser;

import com.google.common.collect.ImmutableMap;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sdagostino on 2015-07-27.
 */
public class ManufactureParser {
    private final Map<String, String> manufacureOsMap;

    public ManufactureParser(Map<String, String> manufacureOsMap) {
        this.manufacureOsMap = manufacureOsMap;
    }

    /**
     * Calculate the manufacture of the hosting OS from the User Agent
     *
     * @param uaString The User Agent string
     * @return The manufacture of the host OS
     */
    public Manufacture parse(String uaString) {
        for (Map.Entry<String, String> entry : getManufactureOsMap().entrySet()) {

            final Pattern pattern = Pattern.compile(entry.getValue());
            final Matcher matcher = pattern.matcher(uaString);

            if (matcher.find()) {
                return  new Manufacture(entry.getKey());
            }
        }
        return new Manufacture(Constants.OTHER);
    }

    /**
     * A simple map with manufactures along with a regex for it's representation
     *
     * @return a immutable map with manufacture => regex
     */
    private static Map<String, String> getManufactureOsMap() {
        return ImmutableMap.<String, String>builder().
            put(Constants.APPLE, "iOS").
            put(Constants.APPLE, "OS X").
            put(Constants.APPLE, "Macintosh").
            put(Constants.GOOGLE, "Android").
            put(Constants.MICROSOFT, "Windows").
            put(Constants.LINUX, "Linux").
            build();
    }
}
