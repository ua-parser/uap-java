ua_parser Java Library
======================

This is the Java implementation of [ua-parser](https://github.com/ua-parser/uap-core).
The implementation uses the shared regex patterns and overrides from regexes.yaml.

Build:
------

    mvn package

Usage:
--------
```java
import ua_parser.Parser;
import ua_parser.Client;

...

  String uaString = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3";

  Parser uaParser = new Parser();
  Client c = uaParser.parse(uaString);

  System.out.println(c.userAgent.family); // => "Mobile Safari"
  System.out.println(c.userAgent.major);  // => "5"
  System.out.println(c.userAgent.minor);  // => "1"

  System.out.println(c.os.family);        // => "iOS"
  System.out.println(c.os.major);         // => "5"
  System.out.println(c.os.minor);         // => "1"

  System.out.println(c.device.family);    // => "iPhone"
```
in Hive: (copy ua-parser.jar into your CLASSPATH, and create function)
```sql
-- add jar to classpath
add jar ua-parser.jar;
-- create function
CREATE TEMPORARY FUNCTION parse_agent as 'ua_parser.hive.ParseAgent';
-- count visits
SELECT parsed_agent['os_family'] AS os_family, COUNT(*) AS cnt
FROM (
  SELECT parse_agent(user_agent) AS parsed_agent
  FROM table_name
  WHERE date='today'
) x
GROUP BY parsed_agent['os_family']
ORDER BY cnt DESC LIMIT 1000;
```
Author:
-------

  * Steve Jiang [@sjiang](https://twitter.com/sjiang)

  Based on the python implementation by Lindsey Simon and using agent data from BrowserScope
