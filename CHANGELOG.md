Undertow for Ninja Framework by Fizzed
======================================

5.7.0.undertow1 - 2016-08-23

 - Bump to Ninja v5.7.0
 - Bump to undertow v1.3.24.Final
 - Support for HTTP/2.0 (see README.md for configuration & usage)
 - Wireshark feature renamed to tracing

5.6.0.undertow1 - 2016-06-23

 - Bump to Ninja v5.6.0
 - Support for new ParamParsers feature in Ninja 5.6.0

5.5.0.undertow2 - 2016-05-25

 - Form parsing uses default UTF-8 charset (undertow defaults to Latin1, while
   modern browsers default to UTF-8).  We will be submitting a bug report to
   undertow, but this is a workaround for now (jfendler)

5.5.0.undertow1 - 2016-05-23

 - Bump to ninja v5.5.0
 - Bump to undertow v1.3.22.Final
 - Bump project maven parent to com.fizzed:maven-parent to v2.0.3
 - Support for Ninja advanced FileItem handling (jfendler)
 - Wireshark feature only enabled for non-assets (skips uris with prefix /assets)
 - Bump project to use Blaze v0.10.0 for build scripting

5.4.0.undertow1 - 2016-03-10
 
 - Bump to ninja v5.4.0
 - Bump to undertow v1.3.18.Final

5.3.1.undertow2 - 2016-02-29

 - META-INF/services resource included to take advantage of Ninja
   support for automatically loading standalone on boot
 - `ninja-core` now a `provided` dependency so `ninja-undertow` does
   not pull in more transitive dependencies
 - Cleaned up blaze/blaze.java benchmark script

5.3.1.undertow1 - 2016-02-27

 - Initial release
