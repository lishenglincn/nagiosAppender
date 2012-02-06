
Deployment on Sonatype Nexus
============================

To publish a snapshot, simply run
---------------------------------
mvn clean deploy

Stage a Release
---------------
mvn release:clean release:prepare
mvn release:perform

Follow the instructions on
 * https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide
 * https://oss.sonatype.org/index.html#welcome
to deploy on Sonatype OSS Maven Repository.


Go to https://oss.sonatype.org/index.html#welcome + Login
- Click Staging Repositories
- Select artifact "de.viaboxx"
- Click Close     +  enter a message
- Click Release   +  enter a message
- Check http://search.maven.org/#search|ga|1|nagiosAppender
