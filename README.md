Undertow for Ninja Framework by Fizzed
=======================================

[![Build Status](https://travis-ci.org/fizzed/ninja-undertow.svg?branch=master)](https://travis-ci.org/fizzed/ninja-undertow)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fizzed/ninja-undertow/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fizzed/ninja-undertow)

[Fizzed, Inc.](http://fizzed.com) (Follow on Twitter: [@fizzed_inc](http://twitter.com/fizzed_inc))

## Overview

Standalone implementation for the [Ninja Framework](https://github.com/ninjaframework/ninja)
using Undertow.  Ninja-undertow does *not use servlets* under-the-hood -- therefore
it bypasses a significant amount of code that Ninja's default Jetty-based standalone
uses.  Ninja-undertow is a drop-in replacement for Ninja's Jetty-based standalone.

## Performance

Based on this [benchmark](ninja-benchmark/src/main/java/ninja/benchmark/NinjaBenchmark.java) ninja-undertow
is 15.5% faster than ninja-jetty for standard GET requests and 7.6% faster than
ninja-jetty for POST requests w/ JSON.  Future optimizations and tuning should
continue to wident the performance gap.

```
Benchmark results for ninja.undertow.NinjaUndertow
----------------------------------------------------
 threads: 50
requests: 50000
get_with_params benchmark: 2096 ms (23854.96/sec)
post_object_as_form benchmark: 2913 ms (17164.43/sec)
post_object_as_json benchmark: 2246 ms (22261.80/sec)

Benchmark results for ninja.standalone.NinjaJetty
----------------------------------------------------
 threads: 50
requests: 50000
get_with_params benchmark: 2521 ms (19833.40/sec)
post_object_as_form benchmark: 3098 ms (16139.44/sec)
post_object_as_json benchmark: 2402 ms (20815.99/sec)
```

## Differences with ninja-standalone (jetty)?

Ninja-undertow passes **all** current Ninja unit tests except one.  Ninja-undertow
does not implement any of Ninja's async-machine-beta features (which Ninja will 
probably deprecate in a future release).

Ninja-undertow is compiled with Java 8, whereas Ninja supports Java 7+.

## Usage

Ninja-undertow is on maven central.  The version will always be the Ninja
version it was compiled against + `undertowN` which represents the undertow
build increment.  As of Ninja v5.4.0, as long as you don't have `ninja.standalone.NinjaJetty`
on your classpath, Ninja will automatically find ninja-undertow and use it
for everything (maven-plugin, testing, standalone). So if you previously had
a dependency on `ninja-standalone`, you'll want to make sure you are only
pulling `ninja-core` and `ninja-undertow`.

```xml
<dependency>
    <groupId>org.ninjaframework</groupId>
    <artifactId>ninja-core</artifactId>
    <version>5.7.0</version>
</dependency>
<dependency>
    <groupId>com.fizzed</groupId>
    <artifactId>ninja-undertow</artifactId>
    <version>5.7.0.undertow1</version>
</dependency>
```

## Configuration

The following Ninja (conf/application.conf) properties are supported.

`ninja.host = <address>`

Host interface to bind undertow server to.  "0.0.0.0" will bind to any address or
"localhost" would bind to just the local interface.

`ninja.port = <integer>`

HTTP port to bind undertow server to.  Any value greater than zero activates
the HTTP server.

`ninja.ssl.port = <integer>`

HTTPS port to bind undertow server to.  Any value greater than zero activates
the HTTPS server.  All Ninja `ninja.ssl.*` properties are supported for setting
up the keystore and truststore.

`undertow.tracing = <boolean>`

If true then an undertow `RequestDumpingHandler` will be added to the request
handling pipeline to log all requests and responses.  Please note that this
is an expensive feature and should only be enabled during development or in
cases of troubleshooting in production.  False by default.

`undertow.http2 = <boolean>`

If true then HTTP/2.0 will be activated for the HTTPS port.  Please note that
a valid ALPN-enabled SSL engine must be enabled in the JVM in order for this
feature to actually work.  Java 9 will have ALPN by default, but for Java 7/8
you'll need to include a valid ALPN jar on the boot classpath (not the regular
JVM classpath) or use a javaagent to load one on-the-fly.  See http://www.eclipse.org/jetty/documentation/9.4.x/alpn-chapter.html
for detailed info.  We recommend just using the alpn agent to load the correct
classes at runtime.  See https://github.com/jetty-project/jetty-alpn-agent

## Demo and benchmark

This project uses [Blaze](https://github.com/fizzed/blaze) for assisting with
running sample apps.  To run a demo:

    java -jar blaze.jar demo

To run a benchmark

    java -jar blaze.jar benchmark

## Testing

Compile and test project by itself and install it

    mvn test

Use Ninja's unit tests to verify.  Install ninja-undertow followed by a [Blaze](https://github.com/fizzed/blaze)
script to clone Ninja's upstream git repo.

    mvn install
    java -jar blaze.jar cloneOrRebaseNinjaRepo

Then edit ninja's standalone module to include ninja-undertow.  This means any
other module in ninja that pulls in ninja-standalone will also have ninja-undertow.

    nano ninja-upstream/ninja-standalone/pom.xml

Add the following as a dependency

```xml
<dependency>
    <groupId>com.fizzed</groupId>
    <artifactId>ninja-undertow</artifactId>
    <version><!-- replace with version in pom.xml --></version>
</dependency>
```
    cd ninja-upstream
    mvn test -Dninja.standalone.class=ninja.undertow.NinjaUndertow

That system property controls the underlying implementation that ninja creates
for its own standalone instance.  Results in NinjaUndertow entirely replacing
every occurrence where NinjaJetty is used.  Therefore, it taps into all the
NinjaFramework unit tests :-)

## License

Copyright (C) 2016 Fizzed, Inc.

This work is licensed under the Apache License, Version 2.0. See LICENSE for details.
