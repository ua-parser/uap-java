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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Device parser using ua-parser regexes. Extracts device information from user agent strings.
 *
 * @author Steve Jiang (@sjiang) &lt;gh at iamsteve com&gt;
 */
public class DeviceParser {

  private final List<DevicePattern> patterns;

  public DeviceParser() {
    this.patterns = Regexes.getDevicePatterns();
  }

  public Device parse(String agentString) {
    if (agentString == null) {
      return null;
    }

    String device;
    for (DevicePattern p : patterns) {
      if ((device = p.match(agentString)) != null) {
        return new Device(device);
      }
    }
    return Device.OTHER;
  }

  protected static class DevicePattern {
	private static final Pattern SUBSTITUTIONS_PATTERN = Pattern.compile("\\$\\d");
    private final Pattern pattern;
    private final String deviceReplacement;

    public DevicePattern(Pattern pattern, String deviceReplacement) {
      this.pattern = pattern;
      this.deviceReplacement = deviceReplacement;
    }

    public String match(String agentString) {
      Matcher matcher = pattern.matcher(agentString);
      if (!matcher.find()) {
        return null;
      }
      String device = null;
      if (deviceReplacement != null) {
        if (deviceReplacement.contains("$")) {
          device = deviceReplacement;
          for (String substitution : getSubstitutions(deviceReplacement)) {    	  
        	int i = Integer.valueOf(substitution.substring(1));
            String replacement = matcher.groupCount() >= i && matcher.group(i) != null 
        			  ? Matcher.quoteReplacement(matcher.group(i)) : "";
              device = device.replaceFirst("\\" + substitution, replacement);  
          }
          device = device.trim();
    	} else {
          device = deviceReplacement;
        } 
      } else if (matcher.groupCount() >= 1) {
        device = matcher.group(1);
      }

      return device;
    }
    
    private List<String> getSubstitutions(String deviceReplacement) {
      Matcher matcher = SUBSTITUTIONS_PATTERN.matcher(deviceReplacement);
      List<String> substitutions = new ArrayList<>();
      while (matcher.find()) {
        substitutions.add(matcher.group());
      }
      return substitutions;
    }
    
  }

}