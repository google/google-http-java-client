# Google HTTP Client Library for Java

## Description
Written by Google, the Google HTTP Client Library for Java is a flexible, efficient, and powerful
Java library for accessing any resource on the web via HTTP. The library has the following features:

- Pluggable HTTP transport abstraction that allows you to use any low-level library such as java.net.HttpURLConnection, Apache HTTP Client, or URL Fetch on Google App Engine.
- Efficient JSON and XML data models for parsing and serialization of HTTP response and request content.  The JSON and XML libraries are also fully pluggable, and they include support for [Jackson](https://github.com/FasterXML/jackson) and Android's GSON libraries for JSON.

The library supports the following Java environments:

- Java 5 (or higher), standard (SE) and enterprise (EE)
- Android Android 1.5 (or higher)
- GoogleAppEngine Google App Engine

The following related projects  are built on the Google HTTP Client Library for Java:

- [Google OAuth Client Library for Java](https://github.com/google/google-oauth-java-client), for the OAuth 2.0 and OAuth 1.0a authorization standards.
- [Google APIs Client Library for Java](https://github.com/google/google-api-java-client), for access to Google APIs.

This is an open-source library, and [contributions](https://developers.google.com/api-client-library/java/google-http-java-client/contribute) are welcome.

## Documentation

- [Developer's Guide](https://developers.google.com/api-client-library/java/google-http-java-client/)
- [Setup Instructions](https://developers.google.com/api-client-library/java/google-http-java-client/setup)
- [JavaDoc](https://developers.google.com/api-client-library/java/google-http-java-client/reference/index)
- [Release Notes](https://developers.google.com/api-client-library/java/google-http-java-client/release-notes)
- [Support (Questions, Bugs)](https://developers.google.com/api-client-library/java/google-http-java-client/support)

## Links

- [Announcements](http://google-http-java-client.blogspot.com/)
- [Discuss](http://groups.google.com/group/google-http-java-client)

## Notice: Ending Java 5 Support

Please note: since Java 5 is no longer supported by Oracle or in Android, we will begin ending
Java 5 support in release 1.24.0. Users may stay still use these libraries in Java 5 projects for
some time, but going forward we will not ensure that these libraries work in such an environment.
After 1.24.0, our supported versions will include Java 6 and onward.

For questions or concerns, please file an issue.