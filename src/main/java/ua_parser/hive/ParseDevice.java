package ua_parser.hive;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import ua_parser.Device;
import ua_parser.Parser;

/**
 * @author fanliwen
 */
@Description(name="parse_device",
        value="_FUNC_ returns Map, which has keys such as 'device'.",
        extended="_FUNC_(user_agent_string)")
public class ParseDevice extends UDF {

    private static final Parser PARSER = new Parser();

    public Map<String,String> evaluate(final Text s) {
        Device device = PARSER.parseDevice(s == null ? null : s.toString());

        Map<String, String> map = new HashMap<String, String>(1);
        if (device != null) {
            map.put("device", device.family);
        }
        return map;
    }
}
