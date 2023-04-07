package ua_parser;

import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

public class RegexesBuilderTest {

  private static final String CLASS_HEADER = ""
          + "/**\n"
          + " * Copyright 2023 Twitter, Inc\n"
          + " *\n"
          + " * Licensed under the Apache License, Version 2.0 (the \"License\");\n"
          + " * you may not use this file except in compliance with the License.\n"
          + " * You may obtain a copy of the License at\n"
          + " *\n"
          + " *      http://www.apache.org/licenses/LICENSE-2.0\n"
          + " *\n"
          + " * Unless required by applicable law or agreed to in writing, software\n"
          + " * distributed under the License is distributed on an \"AS IS\" BASIS,\n"
          + " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
          + " * See the License for the specific language governing permissions and\n"
          + " * limitations under the License.\n"
          + " */\n"
          + "\n"
          + "package ua_parser;\n"
          + "\n"
          + "import java.util.List;\n"
          + "import java.util.concurrent.CopyOnWriteArrayList;\n"
          + "import java.util.regex.Pattern;\n"
          + "\n"
          + "import ua_parser.DeviceParser.DevicePattern;\n"
          + "import ua_parser.OSParser.OSPattern;\n"
          + "import ua_parser.UserAgentParser.UAPattern;\n"
          + "\n"
          + "import java.util.ArrayList;\n"
          + "\n"
          + "/**\n"
          + " * This class is generated at build time, based on the content of \"uap-core/regexes.yaml\"\n"
          + " */\n";

  @Test
  public void testBuilder() throws Exception {
    String testConfig = "user_agent_parsers:\n"
                      + "  - regex: 'Mozilla.{1,200}Android.{1,200}(GSA)/(\\d+)\\.(\\d+)\\.(\\d+)'\n"
                      + "    family_replacement: 'Google'\n"
                      + "    v1_replacement: '2016'\n"
                      + "    v2_replacement: '2017'\n"
                      + "  - regex: '(Crosswalk)/(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)'\n"
                      + "\n"
                      + "os_parsers:\n"
                      + "  - regex: '(Android) Donut'\n"
                      + "    os_replacement: 'Android'\n"
                      + "    os_v1_replacement: '1'\n"
                      + "    os_v2_replacement: '2'\n"
                      + "    os_v3_replacement: '3'\n"
                      + "  - regex: '(Android) Honeycomb'\n"
                      + "    os_v1_replacement: '3'\n"
                      + "\n"
                      + "device_parsers:\n"
                      + "  - regex: '; {0,2}(one ?touch) (EVO7|T10|T20)(?: Build|\\) AppleWebKit)'\n"
                      + "    device_replacement: 'Alcatel One Touch $2'\n"
                      + "    brand_replacement: 'Alcatel'\n"
                      + "    model_replacement: 'One Touch $2'\n"
                      + "  - regex: '; {0,2}(andromax[^;/]{1,100}?)(?: Build|\\) AppleWebKit)'\n"
                      + "    regex_flag: 'i'\n"
                      + "    device_replacement: 'Hisense $1'\n"
                      + "    brand_replacement: 'Hisense'\n"
                      + "    model_replacement: '$1'";

      String content = codeFromStringConfig(testConfig);
      String expected = CLASS_HEADER
                      + "class Regexes {\n"
                      + "\n"
                      + "    public static List<UAPattern> getUserAgentPatterns() {\n"
                      + "        List<UAPattern> configPatterns = new ArrayList<>();\n"
                      + "        configPatterns.add(new UAPattern(Pattern.compile(\"Mozilla.{1,200}Android.{1,200}(GSA)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"), \"Google\", \"2016\", \"2017\"));\n"
                      + "        configPatterns.add(new UAPattern(Pattern.compile(\"(Crosswalk)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"), null, null, null));\n"
                      + "        return new CopyOnWriteArrayList<>(configPatterns);\n"
                      + "    }\n"
                      + "\n"
                      + "    public static List<OSPattern> getOSPatterns() {\n"
                      + "        List<OSPattern> configPatterns = new ArrayList<>();\n"
                      + "        configPatterns.add(new OSPattern(Pattern.compile(\"(Android) Donut\"), \"Android\", \"1\", \"2\", \"3\"));\n"
                      + "        configPatterns.add(new OSPattern(Pattern.compile(\"(Android) Honeycomb\"), null, \"3\", null, null));\n"
                      + "        return new CopyOnWriteArrayList<>(configPatterns);\n"
                      + "    }\n"
                      + "\n"
                      + "    public static List<DevicePattern> getDevicePatterns() {\n"
                      + "        List<DevicePattern> configPatterns = new ArrayList<>();\n"
                      + "        configPatterns.add(new DevicePattern(Pattern.compile(\"; {0,2}(one ?touch) (EVO7|T10|T20)(?: Build|\\\\) AppleWebKit)\"), \"Alcatel One Touch $2\"));\n"
                      + "        configPatterns.add(new DevicePattern(Pattern.compile(\"; {0,2}(andromax[^;/]{1,100}?)(?: Build|\\\\) AppleWebKit)\", Pattern.CASE_INSENSITIVE), \"Hisense $1\"));\n"
                      + "        return new CopyOnWriteArrayList<>(configPatterns);\n"
                      + "    }\n"
                      + "}\n";
    MatcherAssert.assertThat(content, is(expected));
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

    String content = codeFromStringConfig(testConfig);
    String expected = CLASS_HEADER
                    + "class Regexes {\n"
                    + "\n"
                    + "    public static List<UAPattern> getUserAgentPatterns() {\n"
                    + "        List<UAPattern> configPatterns = new ArrayList<>();\n"
                    + "        configPatterns.add(new UAPattern(Pattern.compile(\"ABC([\\\\\\\\0-9]+)\"), \"ABC ($1)\", null, null));\n"
                    + "        return new CopyOnWriteArrayList<>(configPatterns);\n"
                    + "    }\n"
                    + "\n"
                    + "    public static List<OSPattern> getOSPatterns() {\n"
                    + "        List<OSPattern> configPatterns = new ArrayList<>();\n"
                    + "        configPatterns.add(new OSPattern(Pattern.compile(\"CatOS OH-HAI=/\\\\^\\\\.\\\\^\\\\\\\\=\"), \"CatOS 9000\", null, null, null));\n"
                    + "        return new CopyOnWriteArrayList<>(configPatterns);\n"
                    + "    }\n"
                    + "\n"
                    + "    public static List<DevicePattern> getDevicePatterns() {\n"
                    + "        List<DevicePattern> configPatterns = new ArrayList<>();\n"
                    + "        configPatterns.add(new DevicePattern(Pattern.compile(\"CashPhone-([\\\\$0-9]+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"), \"CashPhone $1\"));\n"
                    + "        return new CopyOnWriteArrayList<>(configPatterns);\n"
                    + "    }\n"
                    + "}\n";
    MatcherAssert.assertThat(content, is(expected));
  }

  @Test (expected=IllegalArgumentException.class)
  public void testInvalidConfigThrows() throws Exception {
    codeFromStringConfig("user_agent_parsers:\n  - family_replacement: 'a'");
  }

  @Test
  public void testCode() throws Exception {
    Path file = Paths.get("src/main/java/ua_parser/Regexes.java");
    String actualContent = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    String expectedContent = RegexesBuilder.buildRegexesContent();
    if(!Objects.equals(actualContent, expectedContent)) {
      Files.write(file, expectedContent.getBytes(StandardCharsets.UTF_8));
      Assert.fail("The file 'Regexes.java' does not match with the input '" + RegexesBuilder.REGEX_YAML_PATH + "', run the test and commit the changes");
    }
  }

  private String codeFromStringConfig(String configYamlAsString) {
    InputStream yamlInput = new ByteArrayInputStream(configYamlAsString.getBytes(StandardCharsets.UTF_8));
    Map<String, List<Map<String, String>>> regexConfig = RegexesBuilder.readRegexConfig(yamlInput, RegexesBuilder.getDefaultLoaderOptions());
    String content = RegexesBuilder.buildRegexesContent(regexConfig);
    return content;
  }
}
