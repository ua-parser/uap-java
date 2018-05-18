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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Operating System parser using ua-parser. Extracts OS information from user agent strings.
 *
 * @author Steve Jiang (@sjiang) <gh at iamsteve com>
 */
public class OSParser {
  private  List<OSPattern> patterns;


  public OSParser (List<Map<String,String>> configList) {
    List<OSPattern> configPatterns = new ArrayList<OSPattern>();

    for (Map<String,String> configMap : configList) {
      configPatterns.add(OSParser.patternFromMap(configMap));
    }
    this.patterns = configPatterns;
  }

  public OS parse(String agentString) {
    if (agentString == null) {
      return null;
    }

    OS os;
    for (OSPattern p : patterns) {
      if ((os = p.match(agentString)) != null) {
        return os;
      }
    }
    return new OS("Other", null, null, null, null);
  }

  protected static OSPattern patternFromMap(Map<String, String> configMap) {
    String regex = configMap.get("regex");
    if (regex == null) {
      throw new IllegalArgumentException("OS is missing regex");
    }

    return(new OSPattern(Pattern.compile(regex),
                         configMap.get("os_replacement"),
                         configMap.get("os_v1_replacement"),
                         configMap.get("os_v2_replacement"),
                         configMap.get("os_v3_replacement")));
  }

  protected static class OSPattern {

    private final Pattern pattern;
    private final String osReplacement, v1Replacement, v2Replacement, v3Replacement;

    private ReplacementCmd cmd = ReplacementCmd.getInstance();

    public OSPattern(Pattern pattern, String osReplacement,
                     String v1Replacement, String v2Replacement,
                     String v3Replacement) {
      this.pattern = pattern;
      this.osReplacement = osReplacement;
      this.v1Replacement = v1Replacement;
      this.v2Replacement = v2Replacement;
      this.v3Replacement = v3Replacement;
    }

    public OS match(String agentString) {
      String family = null, v1 = null, v2 = null, v3 = null, v4 = null;
      Matcher matcher = pattern.matcher(agentString);

      if (!matcher.find()) {
        return null;
      }

      int groupCount = matcher.groupCount();

      if (osReplacement != null) {
          if (groupCount >= 1) {
              family = Pattern.compile("(" + Pattern.quote("$1") + ")")
                              .matcher(osReplacement)
                              .replaceAll(matcher.group(1));
          } else {
              family = osReplacement;
          }
      } else if (groupCount >= 1) {
        family = matcher.group(1);
      }

      // See specification, replacements can be regex backreferences
      // https://github.com/ua-parser/uap-core/blob/master/docs/specification.md

      if (groupCount >= 5) {
        v4 = matcher.group(5);
      }

      return family == null ? null : new OS(cmd.execute(osReplacement, matcher,1), cmd.execute(v1Replacement,matcher, 2),
          cmd.execute(v2Replacement, matcher, 3), cmd.execute(v3Replacement, matcher, 4),
          v4);
    }






  }
}
