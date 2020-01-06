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
On Mac OS X, the `gpg.executable` property should be `gpg2`.  

### Instructions for Build/Deploy/Release  
1. bump pom.xml version to non-snapshot version  
1. commit and push to github  
1. `mvn clean deploy -P ossrh`  
1. log in to [Sonatype OSSRH](https://oss.sonatype.org/)  
1. click Staging Repositories  
1. find the release `comgithubua-parser-*`  
1. verify the contents are ok  
1. click Close (wait for validation steps under the Activity tab to complete)  
1. click Release  
1. bump pom.xml version to next snapshot version  
1. commit and push to GitHub  
1. update changelog wiki page  

### Instructions for Build/Deploy/Releas


### Changelog
Changelog can be found [here](https://github.com/ua-parser/uap-java/wiki#changelog).

Author:
-------

* Steve Jiang [@sjiang](https://twitter.com/sjiang)

Based on the python implementation by Lindsey Simon and using agent data from BrowserScope
