package ua_parser.hive;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import ua_parser.Client;
import ua_parser.Parser;

/**
 * @author fanliwen
 */
@Description(name="parse",
        value="_FUNC_ returns Map, which has keys such as 'os_family', 'os_major', 'os_minor', 'device', 'ua_family', 'ua_major' and 'ua_minor'.",
        extended="_FUNC_(user_agent_string)")
public final class Parse extends UDF {

    private static final Parser PARSER = new Parser();

    public Map<String,String> evaluate(final Text s) {
        Client client = PARSER.parse(s == null ? null : s.toString());

        Map<String, String> map = new HashMap<String, String>(10);
        if (client.os != null) {
            map.put("os_family", client.os.family);
            map.put("os_major", client.os.major);
            map.put("os_minor", client.os.minor);
            map.put("os_patch", client.os.patch);
            map.put("os_patchMinor", client.os.patchMinor);
        }
        if (client.device != null) {
            map.put("device", client.device.family);
        }
        if (client.userAgent != null) {
            map.put("ua_family", client.userAgent.family);
            map.put("ua_major", client.userAgent.major);
            map.put("ua_minor", client.userAgent.minor);
            map.put("ua_patch", client.userAgent.patch);
        }
        return map;
    }
}
