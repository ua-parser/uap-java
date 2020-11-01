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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User Agent parser using ua-parser regexes
 *
 * @author Steve Jiang (@sjiang) &lt;gh at iamsteve com&gt;
 */
public class UserAgentParser {

  private final List<UAPattern> patterns;

  public UserAgentParser(List<UAPattern> patterns) {
    this.patterns = patterns;
  }

  /**
   * Constructs a thread-safe UserAgentParser
   * @param configList configure a user-agent parser from a list of regexp hashmaps
   * @return user-agent parser
   */
  public static UserAgentParser fromList(List<Map<String,String>> configList) {
    List<UAPattern> configPatterns = new ArrayList<>();

    for (Map<String, String> configMap : configList) {
      configPatterns.add(UserAgentParser.patternFromMap(configMap));
    }
    return new UserAgentParser(new CopyOnWriteArrayList<>(configPatterns));
  }

  public UserAgent parse(String agentString) {
    if (agentString == null) {
      return null;
    }

    UserAgent agent;
    for (UAPattern p : patterns) {
      if ((agent = p.match(agentString)) != null) {
        return agent;
      }
    }
    return UserAgent.OTHER;
  }

  protected static UAPattern patternFromMap(Map<String, String> configMap) {
    String regex = configMap.get("regex");
    if (regex == null) {
      throw new IllegalArgumentException("User agent is missing regex");
    }

    return(new UAPattern(Pattern.compile(regex),
                         configMap.get("family_replacement"),
                         configMap.get("v1_replacement"),
                         configMap.get("v2_replacement")));
  }

  protected static class UAPattern {
    private final Pattern pattern;
    private final String familyReplacement, v1Replacement, v2Replacement;

    public UAPattern(Pattern pattern, String familyReplacement, String v1Replacement, String v2Replacement) {
      this.pattern = pattern;
      this.familyReplacement = familyReplacement;
      this.v1Replacement = v1Replacement;
      this.v2Replacement = v2Replacement;
    }

    public UserAgent match(String agentString) {
      String family = null, v1 = null, v2 = null, v3 = null, v4 = null;
      Matcher matcher = pattern.matcher(agentString);

      if (!matcher.find()) {
        return null;
      }

      int groupCount = matcher.groupCount();

      if (familyReplacement != null) {
        if (familyReplacement.contains("$1") && groupCount >= 1 && matcher.group(1) != null) {
          family = familyReplacement.replaceFirst("\\$1", Matcher.quoteReplacement(matcher.group(1)));
        } else {
          family = familyReplacement;
        }
      } else if (groupCount >= 1) {
        family = matcher.group(1);
      }

      if (v1Replacement != null) {
        v1 = v1Replacement;
      } else if (groupCount >= 2) {
        String group2 = matcher.group(2);
        if (!isBlank(group2)) {
          v1 = group2;
        }
      }

      if (v2Replacement != null) {
        v2 = v2Replacement;
      } else if (groupCount >= 3) {
        String group3 = matcher.group(3);
        if (!isBlank(group3)) {
          v2 = group3;
        }
        if (groupCount >= 4) {
          String group4 = matcher.group(4);
          if (!isBlank(group4)) {
            v3 = group4;
          }
        }
        if (groupCount >= 5) {
          String group5 = matcher.group(5);
          if (!isBlank(group5)) {
            v4 = group5;
          }
        }
      }
      return family == null ? null : new UserAgent(family, v1, v2, v3, v4);
    }
    
    private boolean isBlank(String value) {
      return value == null || value.isEmpty();
    }
  }
}
