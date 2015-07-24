package ua_parser;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Platform parser using custom list of devices. Will parse and categorize the platform
 * of the UA into either Desktop, Mobile or Tablet.
 *
 * @author Salvatore D'Agostino (@iToto)
 */
public class PlatformParser {
    private final Map<String, String> mobileDevicePatters;
    private final Map<String, String> tabletDevicePatters;

    public PlatformParser(Map<String, String> mobileDevicePatters, Map<String, String> tabletDevicePatters) {
        this.mobileDevicePatters = mobileDevicePatters;
        this.tabletDevicePatters = tabletDevicePatters;
    }

    public Platform parse(String uaString) {
        final Platform platform;

        if (isMobile(uaString)) {
            platform = new Platform(Constants.MOBILE);

        } else if (isTablet(uaString)) {
            platform = new Platform(Constants.TABLET);
        }
        else { // if not a mobile or tablet, will assume desktop!
            platform = new Platform(Constants.DESKTOP);
        }

        return platform;
    }

    private boolean isMobile(String ua) {
        return runPatternsOnString(mobileDevicePatters, ua);
    }

    private boolean isTablet(String ua) {
        return runPatternsOnString(tabletDevicePatters, ua);
    }

    private boolean runPatternsOnString(Map<String, String> patterns, String input) {
        for (Map.Entry<String, String> entry : patterns.entrySet()) {
            final Pattern pattern = Pattern.compile(entry.getValue());
            final Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                // Found a match
                return true;
            }
        }
        return false;
    }
}
