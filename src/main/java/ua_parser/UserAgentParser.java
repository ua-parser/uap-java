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
 * User Agent parser using ua-parser regexes
 *
 * @author Steve Jiang (@sjiang) <gh at iamsteve com>
 */
public class UserAgentParser {
  private List<UAPattern> patterns;

  public  UserAgentParser(List<Map<String,String>> configList) {
    List<UAPattern> configPatterns = new ArrayList<UAPattern>();

    for (Map<String, String> configMap : configList) {
      configPatterns.add(UserAgentParser.patternFromMap(configMap));
    }
    this.patterns = configPatterns;
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
    return new UserAgent("Other", null, null, null);
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

    private ReplacementCmd cmd = ReplacementCmd.getInstance();

    public UAPattern(Pattern pattern, String familyReplacement, String v1Replacement, String v2Replacement) {
      this.pattern = pattern;
      this.familyReplacement = familyReplacement;
      this.v1Replacement = v1Replacement;
      this.v2Replacement = v2Replacement;
    }

    public UserAgent match(String agentString) {
      String family = null, v1 = null, v2 = null, v3 = null;
      Matcher matcher = pattern.matcher(agentString);

      if (!matcher.find()) {
        return null;
      }

      int groupCount = matcher.groupCount();

      if (groupCount >= 4) {
        v3 = matcher.group(4);
      }
      return new UserAgent(
          cmd.execute(familyReplacement, matcher,1),
          cmd.execute(v1Replacement,matcher,2),
          cmd.execute(v2Replacement,matcher,3), v3);
    }
  }
}
