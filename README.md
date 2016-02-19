Undertow for Ninja Framework by Fizzed
======================================

[Fizzed, Inc.](http://fizzed.com) (Follow on Twitter: [@fizzed_inc](http://twitter.com/fizzed_inc))

## Overview

Standalone implementation for the [Ninja Framework](https://github.com/ninjaframework/ninja)
using Undertow.  Ninja-undertow does *not use servlets* under-the-hood -- therefore
it bypasses a significant amount of code that Ninja's default Jetty-based standalone
uses.  Ninja-undertow can fully replace Ninja's Jetty-based standalone.

## Performance

Based on this [benchmark](src/test/java/ninja/undertow/Benchmarker.java) ninja-undertow
is 15.5% faster than ninja-jetty for standard GET requests and 7.6% faster than
ninja-jetty for POST requests w/ JSON.  Future optimizations and tuning should
only improve performance.

## What isn't implemented?

Ninja-undertow passes all current Ninja unit tests except two.  Ninja-undertow
does not implement any of Ninja's async-machine-beta features (which Ninja will 
probably deprecate anyway) and some of the really recent File upload features
(which should be easy to fix if someone wants to submit a PR!).

Ninja-undertow is compiled with Java 8, whereas Ninja supports Java 7+.

## Usage

Ninja-undertow is on maven central.  The version will always be the Ninja
version it was compiled against + `undertowN` which represents the undertow
build increment.

```xml
<dependency>
    <groupId>com.fizzed</groupId>
    <artifactId>ninja-undertow</artifactId>
    <version>5.3.1.undertow1</version>
</dependency>
```

You can either add it to your build as a new dependency or remove your
`ninja-standalone` dependency and replace it with `ninja-undertow`.  If you add
it, remember that your final assembly will contain both jetty and undertow
dependencies and may be a bit more bloated than you need.

To use `ninja-undertow` in the `ninja-maven-plugin` add a new configuration
which sets the mainClass.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.ninjaframework</groupId>
            <artifactId>ninja-maven-plugin</artifactId>
            <version>5.3.1</version>
            <executions>
                <execution>
                    <configuration>
                        <mainClass>ninja.undertow.NinjaUndertow</mainClass>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

To use `ninja-undertow` in your final fat jar, command-line, etc. you'll simply
just need to run the main class `ninja.undertow.NinjaUndertow`.

To use `ninja-undertow` in your unit tests that use some of Ninja's standard
junit classes, you'll need to make sure the system property `ninja.standalone.class`
is set to a value of `ninja.undertow.NinjaUndertow`.

Hopefully, future version of Ninja can make it even easier to plugin a new 
standalone (e.g. with service loaders).

## Testing

Compile and test project by itself and install it

    mvn test

Use ninja unit tests to verify.  Install ninja-undertow followed by a [Blaze](https://github.com/fizzed/blaze)
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
    <version>5.3.1-SNAPSHOT</version>
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
