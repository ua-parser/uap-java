ua_parser Java Library
======================

This is the Java implementation of [ua-parser](https://github.com/ua-parser).
The implementation uses the shared regex patterns and overrides from [regexes.yaml](https://github.com/ua-parser/uap-core/blob/master/regexes.yaml).

Build:
------

uap-java depends on the uap-core project therefore it uses a Git submodule to represent that dependency.  
Before building uap-java, a copy of the uap-core project must be checked out within the local uap-java repository.  
In order to do this, execute the following command (from the base `uap-java` folder) to initialize and checkout the submodule.  

```
git submodule update --init --remote --checkout --recursive
```

you will then have the following folder `uap-java/uap-core` which contains the child repository.

To build the project, execute
```
mvn package
```

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

### Changelog
Changelog can be found [here](https://github.com/ua-parser/uap-java/wiki/ChangeLog).

Original Author:
-------

  * Steve Jiang [@sjiang](https://twitter.com/sjiang)

  Based on the python implementation by Lindsey Simon and using agent data from BrowserScope
