/**
 * Copyright 2012 Twitter, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
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

/**
 * Device parser using ua-parser regexes. Extracts device information from user agent strings.
 *
 * @author Steve Jiang (@sjiang) <gh at iamsteve com>
 */
public class DeviceParser {
  final List<DevicePattern> patterns;

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

  public static DeviceParser fromList(List<Map<String,String>> configList) {
    List<DevicePattern> configPatterns = new ArrayList<DevicePattern>();
    for (Map<String,String> configMap : configList) {
      configPatterns.add(DeviceParser.patternFromMap(configMap));
    }
    return new DeviceParser(configPatterns);
  }

  protected static DevicePattern patternFromMap(Map<String, String> configMap) {
    String regex = configMap.get("regex");
    String regex_flag = configMap.get("regex_flag");
    if (regex == null) {
      throw new IllegalArgumentException("Device is missing regex");
    }

    Pattern pattern;
    if (regex_flag != null && regex_flag.equals("i")) {
      pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    } else {
      pattern = Pattern.compile(regex);
    }

    /**
     * To maintains backwards compatibility the familyReplacement
     * field has been named "device_replacement"
     */
    return new DevicePattern(pattern,
                             configMap.get("device_replacement"),
                             configMap.get("brand_replacement"),
                             configMap.get("model_replacement"));
  }

  protected static class DevicePattern {
    private final Pattern pattern;
    private final String familyReplacement;
    private final String brandReplacement;
    private final String modelReplacement;

    private static final Pattern GROUP_PATTERN = Pattern.compile("\\$(\\d+)");

    public DevicePattern(Pattern pattern, String familyReplacement, String brandReplacement, String modelReplacement) {
      this.pattern = pattern;
      this.familyReplacement = familyReplacement;
      this.brandReplacement = brandReplacement;
      this.modelReplacement = modelReplacement;
    }

    private String performReplacement(Matcher matcher, String replacement) {
      int count = matcher.groupCount();
      StringBuffer buffer = new StringBuffer();
      Matcher replaceMatcher = GROUP_PATTERN.matcher(replacement);
      while (replaceMatcher.find()) {
        String group = null;
        try {
          int id = Integer.parseInt(replaceMatcher.group(1));
          if (id >= 0 && id <= count) {
            group = matcher.group(id);
          }
        }
        catch (NumberFormatException ignored) {}
        catch (IllegalArgumentException ignored) {}
        catch (IndexOutOfBoundsException ignored) {}
        replaceMatcher.appendReplacement(buffer, group == null ? "" : Matcher.quoteReplacement(group));
      }
      replacement = buffer.toString();
      return replacement;
    }

    private String replace(Matcher matcher, String replacement, int position) {
      if (replacement == null) {
        if (position > 0) {
          replacement = "$" + position;
        } else {
          return null;
        }
      }

      if (replacement.contains("$")) {
        replacement = performReplacement(matcher, replacement);
      }
      replacement = replacement.trim();
      if (replacement.length() == 0) {
        return null;
      } else {
        return replacement;
      }
    }

    public Device match(String agentString) {

      Matcher matcher = pattern.matcher(agentString);

      if (!matcher.find()) {
        return null;
      }

      String family = replace(matcher, familyReplacement, 1);
      String brand = replace(matcher, brandReplacement, -1);
      String model = replace(matcher, modelReplacement, 1);

      if (family != null) {
        return new Device(family, brand, model);
      } else {
        return null;
      }

    }
  }

}