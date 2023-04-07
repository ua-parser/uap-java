package ua_parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public class RegexesBuilder {

  static final String REGEX_YAML_PATH = "/ua_parser/regexes.yaml";

  public static final int CODE_POINT_LIMIT = 3455764;

  public static LoaderOptions getDefaultLoaderOptions() {
    LoaderOptions options = new LoaderOptions();
    options.setCodePointLimit(CODE_POINT_LIMIT);
    return options;
  }

  public static String buildRegexesContent() {
    LoaderOptions loaderOptions = getDefaultLoaderOptions();
    Map<String,List<Map<String,String>>> regexConfig;
    try (InputStream is = Parser.class.getResourceAsStream(REGEX_YAML_PATH)) {
      regexConfig = readRegexConfig(is, loaderOptions);
    } catch (IOException e) {
      throw new RuntimeException("failed to initialize parser from regexes.yaml bundled in jar", e);
    }

    return buildRegexesContent(regexConfig);
  }

  static String buildRegexesContent(Map<String, List<Map<String, String>>> regexConfig) {
	List<Map<String,String>> uaParserConfigs = regexConfig.get("user_agent_parsers");
    if (uaParserConfigs == null) {
      throw new IllegalArgumentException("user_agent_parsers is missing from yaml");
    }

    List<Map<String,String>> osParserConfigs = regexConfig.get("os_parsers");
    if (osParserConfigs == null) {
      throw new IllegalArgumentException("os_parsers is missing from yaml");
    }

    List<Map<String,String>> deviceParserConfigs = regexConfig.get("device_parsers");
    if (deviceParserConfigs == null) {
      throw new IllegalArgumentException("device_parsers is missing from yaml");
    }

    StringBuilder sb = new StringBuilder();
	sb.append("");
	sb.append("/**\n");
	sb.append(" * Copyright 2023 Twitter, Inc\n");
	sb.append(" *\n");
	sb.append(" * Licensed under the Apache License, Version 2.0 (the \"License\");\n");
	sb.append(" * you may not use this file except in compliance with the License.\n");
	sb.append(" * You may obtain a copy of the License at\n");
	sb.append(" *\n");
	sb.append(" *      http://www.apache.org/licenses/LICENSE-2.0\n");
	sb.append(" *\n");
	sb.append(" * Unless required by applicable law or agreed to in writing, software\n");
	sb.append(" * distributed under the License is distributed on an \"AS IS\" BASIS,\n");
	sb.append(" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
	sb.append(" * See the License for the specific language governing permissions and\n");
	sb.append(" * limitations under the License.\n");
	sb.append(" */\n");
	sb.append("\n");
	sb.append("package ua_parser;\n");
	sb.append("\n");
	sb.append("import java.util.List;\n");
	sb.append("import java.util.concurrent.CopyOnWriteArrayList;\n");
	sb.append("import java.util.regex.Pattern;\n");
	sb.append("\n");
	sb.append("import ua_parser.DeviceParser.DevicePattern;\n");
	sb.append("import ua_parser.OSParser.OSPattern;\n");
	sb.append("import ua_parser.UserAgentParser.UAPattern;\n");
	sb.append("\n");
	sb.append("import java.util.ArrayList;\n");
	sb.append("\n");
	sb.append("/**\n");
	sb.append(" * This class is generated at build time, based on the content of \"uap-core/regexes.yaml\"\n");
	sb.append(" */\n");
	sb.append("class Regexes {\n");
	sb.append("\n");
	sb.append("    public static List<UAPattern> getUserAgentPatterns() {\n");
	sb.append("        List<UAPattern> configPatterns = new ArrayList<>();\n");
	appendUAPatterns(sb, uaParserConfigs);
	sb.append("        return new CopyOnWriteArrayList<>(configPatterns);\n");
	sb.append("    }\n");
	sb.append("\n");
	sb.append("    public static List<OSPattern> getOSPatterns() {\n");
	sb.append("        List<OSPattern> configPatterns = new ArrayList<>();\n");
	appendOSPatterns(sb, osParserConfigs);
	sb.append("        return new CopyOnWriteArrayList<>(configPatterns);\n");
	sb.append("    }\n");
	sb.append("\n");
	sb.append("    public static List<DevicePattern> getDevicePatterns() {\n");
	sb.append("        List<DevicePattern> configPatterns = new ArrayList<>();\n");
    appendDevicePatterns(sb, deviceParserConfigs);
	sb.append("        return new CopyOnWriteArrayList<>(configPatterns);\n");
	sb.append("    }\n");
	sb.append("}\n");
	return sb.toString();
}


  private static void appendUAPatterns(StringBuilder sb, List<Map<String, String>> configList) {
	    for (Map<String, String> configMap : configList) {
	        String regex = configMap.get("regex");
	        if (regex == null) {
	          throw new IllegalArgumentException("User agent is missing regex");
	        }

	        sb.append("        configPatterns.add(new UAPattern(Pattern.compile(" + stringToCode(regex) + "), " 
		        + stringToCode(configMap.get("family_replacement"))+ ", "
		        + stringToCode(configMap.get("v1_replacement")) + ", " 
		        + stringToCode(configMap.get("v2_replacement")) + "));\n");
	      }
	
}

private static void appendOSPatterns(StringBuilder sb, List<Map<String, String>> configList) {
    for (Map<String, String> configMap : configList) {
        String regex = configMap.get("regex");
        if (regex == null) {
          throw new IllegalArgumentException("OS is missing regex");
        }

        sb.append("        configPatterns.add(new OSPattern(Pattern.compile(" + stringToCode(regex) + "), " 
            + stringToCode(configMap.get("os_replacement"))+ ", "
            + stringToCode(configMap.get("os_v1_replacement")) + ", " 
            + stringToCode(configMap.get("os_v2_replacement")) + ", " 
            + stringToCode(configMap.get("os_v3_replacement")) + "));\n");
    }
}

private static void appendDevicePatterns(StringBuilder sb, List<Map<String, String>> configList) {
    for (Map<String, String> configMap : configList) {
        String regex = configMap.get("regex");
        if (regex == null) {
          throw new IllegalArgumentException("Device is missing regex");
        }

        String regexFlag = configMap.get("regex_flag");
        String compileArguments;
		if(regexFlag == null) {
          compileArguments= "";
        } else if("i".equals(regexFlag)) {
          compileArguments= ", Pattern.CASE_INSENSITIVE";
        } else {
          // no other flags used (by now) 
          throw new IllegalArgumentException("Unexpected 'regex_flag' value");
        }
        sb.append("        configPatterns.add(new DevicePattern(Pattern.compile(" + stringToCode(regex) + compileArguments + "), " 
            + stringToCode(configMap.get("device_replacement")) + "));\n");
    }
}

static Map<String, List<Map<String, String>>> readRegexConfig(InputStream regexYaml, LoaderOptions loaderOptions) {
    Yaml yaml = new Yaml(new SafeConstructor(loaderOptions));

    @SuppressWarnings("unchecked")
    Map<String,List<Map<String,String>>> regexConfig = (Map<String,List<Map<String,String>>>) yaml.load(regexYaml);

    return regexConfig;
  }

private static String stringToCode(String value) {
  if(value == null) {
    return "null";
  }
  String sanitizedValue = value
		  .replace("\\", "\\\\")
		  .replace("\"", "\\\"")
		  .replace("\t", "\\t")
		  .replace("\b", "\\b")
		  .replace("\n", "\\n")
		  .replace("\r", "\\r")
		  .replace("\f", "\\f");
return "\"" + sanitizedValue + "\"";
}

}
