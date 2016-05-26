package ua_parser.hive;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import ua_parser.OS;
import ua_parser.Parser;

/**
 * @author fanliwen
 */
@Description(name="parse_os",
        value="_FUNC_ returns Map, which has keys such as 'os_family', 'os_major', 'os_minor'.",
        extended="_FUNC_(user_agent_string)")
public class ParseOS extends UDF {

    private static final Parser PARSER = new Parser();

    public Map<String,String> evaluate(final Text s) {
        OS os = PARSER.parseOS(s == null ? null : s.toString());

        Map<String, String> map = new HashMap<String, String>(5);
        if (os != null) {
            map.put("os_family", os.family);
            map.put("os_major", os.major);
            map.put("os_minor", os.minor);
            map.put("os_patch", os.patch);
            map.put("os_patchMinor", os.patchMinor);
        }
        return map;
    }
}
