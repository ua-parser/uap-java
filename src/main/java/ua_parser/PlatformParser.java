/**
 * Platform parser using custom list of devices. Will parse and categorize the platform
 * of the UA into either Desktop, Mobile or Tablet.
 *
 * @author Salvatore D'Agostino (@iToto)
 */
package ua_parser;

import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlatformParser {
    private final Map<String, String> mobileDevicePatters;
    private final Map<String, String> tabletDevicePatters;
    private final Logger LOGGER = Logger.getLogger(PlatformParser.class.getName());

    public PlatformParser(Map<String, String> mobileDevicePatters, Map<String, String> tabletDevicePatters) {
        this.mobileDevicePatters = mobileDevicePatters;
        this.tabletDevicePatters = tabletDevicePatters;
    }

    public Platform parse(String uaString) {

        if (isMobile(uaString)) {
            return new Platform(Constants.MOBILE);

        } else if (isTablet(uaString)) {
            return  new Platform(Constants.TABLET);
        }
        else { // if not a mobile or tablet, will assume desktop!
            return new Platform(Constants.DESKTOP);
        }
    }

    private boolean isMobile(String ua) {
        LOGGER.info("Checking For Mobile Device");
        return runPatternsOnString(mobileDevicePatters, ua);
    }

    private boolean isTablet(String ua) {
        LOGGER.info("Checking For Tablet Device");
        return runPatternsOnString(tabletDevicePatters, ua);
    }

    private boolean runPatternsOnString(Map<String, String> patterns, String input) {
        for (Map.Entry<String, String> entry : patterns.entrySet()) {
            final Pattern pattern = Pattern.compile(entry.getValue());
            final Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                // Found a match
                LOGGER.info("Found match in device with key: " + entry.getKey());
                return true;
            }
        }
        LOGGER.info("No match found, using default platform value");
        return false;
    }
}
