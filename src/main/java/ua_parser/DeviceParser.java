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
import java.util.HashMap;
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
  List<DevicePattern> patterns;

  public DeviceParser(List<DevicePattern> patterns) {
    this.patterns = patterns;
  }

  public Device parse(String agentString) {
    if (agentString == null) {
      return null;
    }

    Map<String, String> replacements = null;
    for (DevicePattern p : patterns) {
      if ((replacements = p.match(agentString)) != null) {
        break;
      }
    }

    if (replacements == null) {
      return new Device("Other", null, null);
    }
    else {
      return new Device(replacements.get("device_replacement"), replacements.get("brand_replacement"), replacements.get("model_replacement"));
    }
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
    if (regex == null) {
      throw new IllegalArgumentException("Device is missing regex");
    }    
    Pattern pattern = "i".equals(configMap.get("regex_flag")) // no ohter flags used (by now) 
    		? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex);
    return new DevicePattern(pattern, configMap.get("device_replacement"), configMap.get("brand_replacement"), configMap.get("model_replacement"));
  }

  protected static class DevicePattern {
	private static final Pattern SUBSTITUTIONS_PATTERN = Pattern.compile("\\$\\d");
    private final Pattern pattern;
    private final String deviceReplacement;
    private final String brandReplacement;
    private final String modelReplacement;

    public DevicePattern(Pattern pattern, String deviceReplacement, String brandReplacement, String modelReplacement) {
      this.pattern = pattern;
      this.deviceReplacement = deviceReplacement;
      this.brandReplacement = brandReplacement;
      this.modelReplacement = modelReplacement;
    }

    public Map<String, String> match(String agentString) {
      Matcher matcher = pattern.matcher(agentString);
      if (!matcher.find()) {
        return null;
      }

      Map<String, String> replacements = new HashMap<String, String>();
      replacements.put("device_replacement", getReplacement(deviceReplacement, matcher, 1));
      replacements.put("brand_replacement", getReplacement(brandReplacement, matcher, -1));
      replacements.put("model_replacement", getReplacement(modelReplacement, matcher, 1));

      return replacements;
    }

    private String getReplacement(String replacement, Matcher matcher, int groupNum) {
      String replacementOut = null;
      if (replacement != null) {
        if (replacement.contains("$")) {
          replacementOut = replacement;
          for (String substitution : getSubstitutions(replacement)) {
            int i = Integer.valueOf(substitution.substring(1));
            String replacementGroup = matcher.groupCount() >= i && matcher.group(i) != null
                    ? Matcher.quoteReplacement(matcher.group(i)) : "";
            replacementOut = replacementOut.replaceFirst("\\" + substitution, replacementGroup);
          }
          replacementOut = replacementOut.trim();
        } else {
          replacementOut = replacement;
        }
      } else if (groupNum > 0 && matcher.groupCount() >= 1) {
        replacementOut = matcher.group(groupNum);
      }

      if(replacementOut == null || replacementOut.isEmpty())
        return null;
      else
        return replacementOut;
    }
    
    private List<String> getSubstitutions(String replacement) {
      Matcher matcher = SUBSTITUTIONS_PATTERN.matcher(replacement);
      List<String> substitutions = new ArrayList<String>();
      while (matcher.find()) {
        substitutions.add(matcher.group());
      }
      return substitutions;
    }
    
  }

}