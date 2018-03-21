/**
 * Copyright 2012 Twitter, Inc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua_parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ua_parser.ReplacementOps.replace;

/**
 * Device parser using ua-parser regexes. Extracts device information from user agent strings.
 *
 * @author Steve Jiang (@sjiang) <gh at iamsteve com>
 */
public class DeviceParser {
    List<DevicePattern> patterns;

    public DeviceParser(List<DevicePattern> patterns) {
        this.patterns = patterns;
    }

    public Device parse(String agentString) {
        if (agentString == null) {
            return null;
        }

        Device device;
        for (DevicePattern p : patterns) {
            if ((device = p.match(agentString)) != null) {
                return device;
            }
        }

        return new Device("Other", null, null);
    }

    public static DeviceParser fromList(List<Map<String, String>> configList) {
        List<DevicePattern> configPatterns = new ArrayList<DevicePattern>();
        for (Map<String, String> configMap : configList) {
            configPatterns.add(DeviceParser.patternFromMap(configMap));
        }
        return new DeviceParser(configPatterns);
    }

    protected static DevicePattern patternFromMap(Map<String, String> configMap) {
        String regex = configMap.get("regex");
        if (regex == null) {
            throw new IllegalArgumentException("Device is missing regex");
        }
        Pattern pattern = "i".equals(configMap.get("regex_flag")) // no other flags used (by now)
                ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex);

        return (new DevicePattern(pattern,
                configMap.get("device_replacement"),
                configMap.get("brand_replacement"),
                configMap.get("model_replacement")));
    }

    protected static class DevicePattern {

        private final Pattern pattern;
        private final String deviceReplacement, brandReplacement, modelReplacement;

        public DevicePattern(Pattern pattern, String deviceReplacement, String brandReplacement, String modelReplacement) {
            this.pattern = pattern;
            this.deviceReplacement = deviceReplacement;
            this.brandReplacement = brandReplacement;
            this.modelReplacement = modelReplacement;
        }

        public Device match(String agentString) {
            Matcher matcher = pattern.matcher(agentString);
            if (!matcher.find()) {
                return null;
            }
            String device = null, brand = null, model = null;
            if (deviceReplacement != null) {
                device = replace(deviceReplacement, matcher);
            } else if (matcher.groupCount() >= 1) {
                device = matcher.group(1);
            }

            if (brandReplacement != null) {
                brand = replace(brandReplacement, matcher);
            }

            if (modelReplacement != null) {
                model = replace(modelReplacement, matcher);
            } else if (matcher.groupCount() >= 1) {
                model = matcher.group(1);
            }

            return new Device(device, brand, model);
        }

    }

}
