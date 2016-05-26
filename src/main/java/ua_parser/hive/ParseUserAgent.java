package ua_parser.hive;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import ua_parser.Parser;
import ua_parser.UserAgent;

/**
 * @author fanliwen
 */
@Description(name="parse_user_agent",
        value="_FUNC_ returns Map, which has keys such as 'ua_family', 'ua_major' and 'ua_minor'.",
        extended="_FUNC_(user_agent_string)")
public class ParseUserAgent extends UDF {

    private static final Parser PARSER = new Parser();

    public Map<String,String> evaluate(final Text s) {
        UserAgent userAgent = PARSER.parseUserAgent(s == null ? null : s.toString());

        Map<String, String> map = new HashMap<String, String>(4);
        if (userAgent != null) {
            map.put("ua_family", userAgent.family);
            map.put("ua_major", userAgent.major);
            map.put("ua_minor", userAgent.minor);
            map.put("ua_patch", userAgent.patch);
        }
        return map;
    }
}
