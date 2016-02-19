Undertow standalone for Ninja Framework by Fizzed
=================================================

[Fizzed, Inc.](http://fizzed.com) (Follow on Twitter: [@fizzed_inc](http://twitter.com/fizzed_inc))

## Overview

BETA standalone implementation for the [Ninja Framework](https://github.com/ninjaframework/ninja)
using Undertow.  Ninja-undertow does not use servlets under-the-hood and leverages
Undertow's small footprint and powerful features.  Can fully replace Ninja's
Jetty-based standalone.

## Testing

Compile and test project by itself and install it

    mvn install

Go into ninja-upstream

    mvn test -Dninja.standalone.class=ninja.undertow.NinjaUndertow

That system property controls the underlying implementation that ninja creates
for its own standalone instance.  Results in NinjaUndertow entirely replacing
every occurrence where NinjaJetty is used.  Therefore, it taps into all the
NinjaFramework unit tests :-)

