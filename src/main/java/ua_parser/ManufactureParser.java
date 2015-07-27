/**
 * Manufaacture parser using a list of manufactures with a set of regex expressions. Will determine the
 * manufacture of the host's OS.
 *
 * @author Salvatore D'Agostino (@iToto)
 */
package ua_parser;

import com.google.common.collect.ImmutableMultimap;

import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sdagostino on 2015-07-27.
 */
public class ManufactureParser {
    private final Logger LOGGER = Logger.getLogger(PlatformParser.class.getName());

    public ManufactureParser() { }

    /**
     * Calculate the manufacture of the hosting OS from the User Agent
     *
     * @param uaString The User Agent string
     * @return The manufacture of the host OS
     */
    public Manufacture parse(String uaString) {
        for (Map.Entry<String, String> entry : getManufactureOsMap().entries()) {

            // Itterate over all expressions assigned to a key
            for (String regex : getManufactureOsMap().get(entry.getKey())) {
                final Pattern pattern = Pattern.compile(regex);
                final Matcher matcher = pattern.matcher(uaString);

                if (matcher.find()) {
                    LOGGER.info("Found match in manufacture with key: " + entry.getKey());
                    return  new Manufacture(entry.getKey());
                }
            }
        }

        LOGGER.info("Could not find a manufacturer, using default manufaacture value");
        return new Manufacture(Constants.OTHER);
    }

    /**
     * A simple map with manufactures along with a regex for it's representation
     *
     * @return a immutable map with manufacture => regex
     */
    private static ImmutableMultimap<String, String> getManufactureOsMap() {
        return ImmutableMultimap.<String, String>builder().
            put(Constants.APPLE, "iOS").
            put(Constants.APPLE, "OS X").
            put(Constants.APPLE, "Macintosh").
            put(Constants.GOOGLE, "Android").
            put(Constants.MICROSOFT, "Windows").
            put(Constants.LINUX, "Linux").
            build();
    }
}
