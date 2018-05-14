ua_parser Java Library
======================

This is the Java implementation of [ua-parser](https://github.com/ua-parser).
The implementation uses the shared regex patterns and overrides from [regexes.yaml](https://github.com/ua-parser/uap-core/blob/master/regexes.yaml).

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

## Publish to Sonatype OSSRH and Maven Central Repository

Create a ~/.m2/settings.xml file with the following contents:
```
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>YOUR_OSSRH_USERNAME</username>
      <password>YOUR_OSSRH_PASSWORD</password>
    </server>
  </servers>

  <profiles>
    <profile>
      <id>ossrh</id>
      <properties>
        <gpg.keyname>YOUR_GPG_KEY_ID</gpg.keyname>
        <gpg.executable>GPG_EXECUTABLE</gpg.executable>
        <gpg.passphrase>YOUR_KEY_PASSPHRASE</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
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

### Additional Resources for Deploying to Sonatype OSSRH and Maven Central Repository    
[Deploying to Sonatype OSSRH using Maven](http://central.sonatype.org/pages/apache-maven.html)  
[Releasing the artifact in Sonatype OSSRH](http://central.sonatype.org/pages/releasing-the-deployment.html)  

Sonatype OSSRH is synced with Maven Central Repository so the artifacts will appear in Maven Central Repo
automatically shortly after releasing.  

Author:
-------

  * Steve Jiang [@sjiang](https://twitter.com/sjiang)

  Based on the python implementation by Lindsey Simon and using agent data from BrowserScope
