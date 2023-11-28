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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

/**
 * Java implementation of <a href="https://github.com/ua-parser">UA Parser</a>
 *
 * @author Steve Jiang (@sjiang) &lt;gh at iamsteve com&gt;
 */
public class Parser {

  private static final String REGEX_YAML_PATH = "/ua_parser/regexes.yaml";

  public static final int CODE_POINT_LIMIT = 3455764;
  private UserAgentParser uaParser;
  private OSParser osParser;
  private DeviceParser deviceParser;

  /**
   * Creates a parser using the regular expression yaml file bundled in the jar.
   * @throws RuntimeException if there's a problem reading the file from the classpath
   */
  public Parser() {
    this(getDefaultLoaderOptions());
  }

  /**
   * Creates a parser using the regular expression yaml file bundled in the jar.
   *
   * @param loaderOptions configuration for loading parser safe limits.
   * @throws RuntimeException if there's a problem reading the file from the classpath.
   */
  public Parser(LoaderOptions loaderOptions) {
    try (InputStream is = Parser.class.getResourceAsStream(REGEX_YAML_PATH)) {
      initialize(is, loaderOptions);
    } catch (IOException e) {
      throw new RuntimeException("failed to initialize parser from regexes.yaml bundled in jar", e);
    }
  }

  /**
   * Creates a parser using the supplied regular expression yaml file.
   * It is the responsibility of the caller to close the InputStream after construction.
   * @param regexYaml the yaml file containing the regular expressions
   */
  public Parser(InputStream regexYaml) {
    this(regexYaml, getDefaultLoaderOptions());
  }
  /**
   * Creates a parser using the supplied regular expression yaml file.
   * It is the responsibility of the caller to close the InputStream after construction.
   * @param regexYaml the yaml file containing the regular expressions
   * @param loaderOptions configuration for loading parser safe limits.
   */
  public Parser(InputStream regexYaml, LoaderOptions loaderOptions) {
    initialize(regexYaml, loaderOptions);
  }

  public Client parse(String agentString) {
    UserAgent ua = uaParser.parse(agentString);
    OS os = osParser.parse(agentString);
    Device device = deviceParser.parse(agentString);
    return new Client(ua, os, device);
  }

  public UserAgent parseUserAgent(String agentString) {
    return uaParser.parse(agentString);
  }

  public Device parseDevice(String agentString) {
    return deviceParser.parse(agentString);
  }

  public OS parseOS(String agentString) {
    return osParser.parse(agentString);
  }

  public static LoaderOptions getDefaultLoaderOptions(){
    LoaderOptions options = new LoaderOptions();
    options.setCodePointLimit(CODE_POINT_LIMIT);
    return options;
  }

  private void initialize(InputStream regexYaml, LoaderOptions loaderOptions) {
    Yaml yaml = new Yaml(new SafeConstructor(loaderOptions));

    @SuppressWarnings("unchecked")
    Map<String,List<Map<String,String>>> regexConfig = (Map<String,List<Map<String,String>>>) yaml.load(regexYaml);

    List<Map<String,String>> uaParserConfigs = regexConfig.get("user_agent_parsers");
    if (uaParserConfigs == null) {
      throw new IllegalArgumentException("user_agent_parsers is missing from yaml");
    }
    uaParser = UserAgentParser.fromList(uaParserConfigs);

    List<Map<String,String>> osParserConfigs = regexConfig.get("os_parsers");
    if (osParserConfigs == null) {
      throw new IllegalArgumentException("os_parsers is missing from yaml");
    }
    osParser = OSParser.fromList(osParserConfigs);

    List<Map<String,String>> deviceParserConfigs = regexConfig.get("device_parsers");
    if (deviceParserConfigs == null) {
      throw new IllegalArgumentException("device_parsers is missing from yaml");
    }
    deviceParser = DeviceParser.fromList(deviceParserConfigs);
  }
}
