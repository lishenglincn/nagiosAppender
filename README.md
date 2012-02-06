Nagios Appender for Log4J
=========================

Provides a nagios appender that is capable of logging directly to nagios without parsing any error logs, just by adding a jar and a config option to your project.

An example can be found in the "docs" directory.

Know Limitations
================

The support for encryption is limited. This is just a thin wrapper for the stuff provided through [jsendnsca](http://code.google.com/p/jsendnsca/), which limits the encyption to XOR obfuscation and Triple-DES, which makes this projet a little bit more capable than the appender mentioned in [this blog post](http://www.novell.com/communities/node/4131/application-monitoring-made-easy-java-applications-using-nagios).

License
=======

The MIT License (MIT)
Copyright (c) 2012 Sebastian Schuth

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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

