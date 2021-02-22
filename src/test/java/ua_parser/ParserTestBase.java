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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * Tests parsing results match the expected results in the test_resources yamls
 *
 * @author Steve Jiang (@sjiang) <gh at iamsteve com>
 */
abstract public class ParserTestBase {
  Yaml yaml = new Yaml();
  Parser parser;

  abstract public String resourcePath();

  @Before
  abstract public void initParser();

  @Test
  public void testParseUserAgent() {
    testUserAgentFromYaml("test_ua.yaml");
  }

  @Test
  public void testParseOS() {
    testOSFromYaml("test_os.yaml");
  }

  @Test
  public void testParseAdditionalOS() {
    testOSFromYaml("additional_os_tests.yaml");
  }


  @Test
  public void testParseDevice() {
    testDeviceFromYaml("test_device.yaml");
  }

  @Test
  public void testParseFirefox() {
    testUserAgentFromYaml("firefox_user_agent_strings.yaml");
  }

  @Test
  public void testParsePGTS() {
    testUserAgentFromYaml("pgts_browser_list.yaml");
  }

  @Test
  public void testReplacementQuoting() throws Exception {
    String testConfig = "user_agent_parsers:\n"
                      + "  - regex: 'ABC([\\\\0-9]+)'\n"
                      + "    family_replacement: 'ABC ($1)'\n"
                      + "os_parsers:\n"
                      + "  - regex: 'CatOS OH-HAI=/\\^\\.\\^\\\\='\n"
                      + "    os_replacement: 'CatOS 9000'\n"
                      + "device_parsers:\n"
                      + "  - regex: 'CashPhone-([\\$0-9]+)\\.(\\d+)\\.(\\d+)'\n"
                      + "    device_replacement: 'CashPhone $1'\n";

    Parser testParser = parserFromStringConfig(testConfig);
    Client result = testParser.parse("ABC12\\34 (CashPhone-$9.0.1 CatOS OH-HAI=/^.^\\=)");
    assertThat(result.userAgent.family, is("ABC (12\\34)"));
    assertThat(result.os.family, is("CatOS 9000"));
    assertThat(result.device.family, is("CashPhone $9"));
  }

  @Test (expected=IllegalArgumentException.class)
  public void testInvalidConfigThrows() throws Exception {
    parserFromStringConfig("user_agent_parsers:\n  - family_replacement: 'a'");
  }

  void testUserAgentFromYaml(String filename) {
    InputStream yamlStream = this.getClass().getResourceAsStream(resourcePath() + filename);

    @SuppressWarnings("unchecked")
    Map<String, List<Map<String,String>>> entries = (Map<String, List<Map<String,String>>>)yaml.load(yamlStream);
    
    List<Map<String, String>> testCases = entries.get("test_cases");
    for(Map<String, String> testCase : testCases) {
      // Skip tests with js_ua as those overrides are not yet supported in java
      if (testCase.containsKey("js_ua")) continue;

      String uaString = testCase.get("user_agent_string");
      assertThat(uaString, parser.parseUserAgent(uaString), is(UserAgent.fromMap(testCase)));
    }
  }

  void testOSFromYaml(String filename) {
    InputStream yamlStream = this.getClass().getResourceAsStream(resourcePath() + filename);

    @SuppressWarnings("unchecked")
    Map<String, List<Map<String,String>>> entries = (Map<String, List<Map<String,String>>>)yaml.load(yamlStream);
    
    List<Map<String,String>> testCases = entries.get("test_cases");
    for(Map<String, String> testCase : testCases) {
      // Skip tests with js_ua as those overrides are not yet supported in java
      if (testCase.containsKey("js_ua")) continue;

      String uaString = testCase.get("user_agent_string");
      assertThat(uaString, parser.parseOS(uaString), is(OS.fromMap(testCase)));
    }
  }

  void testDeviceFromYaml(String filename) {
    InputStream yamlStream = this.getClass().getResourceAsStream(resourcePath() + filename);

    @SuppressWarnings("unchecked")
    Map<String, List<Map<String,String>>> entries = (Map<String, List<Map<String,String>>>)yaml.load(yamlStream);
    
    List<Map<String,String>> testCases = entries.get("test_cases");
    for(Map<String, String> testCase : testCases) {

      String uaString = testCase.get("user_agent_string");
      assertThat(uaString, parser.parseDevice(uaString), is(Device.fromMap(testCase)));
    }
  }

  Parser parserFromStringConfig(String configYamlAsString) throws Exception {
    InputStream yamlInput = new ByteArrayInputStream(configYamlAsString.getBytes("UTF8"));
    return new Parser(yamlInput);
  }
}
