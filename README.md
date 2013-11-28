# Background

The goal of this example is to demonstrate the tradeoffs found in
the mobile + cloud architecture, where one has a choice between
doing work locally (on the mobile device) versus remotely (in 
the cloud) with different performance considerations in each case.

This is a very rough initial attempt and still needs significant work.

# Learning Objectives

- Show how CPU-intensive computation can be off-loaded from a mobile app to
  the cloud, by comparison to a mobile device, an unlimited resource for
  computation and storage.
- asynchronous local background tasks in Android using [AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html)
- asynchronous access of remote resources (usually RESTful web services) in Android using [AsyncHttpClient](http://loopj.com/android-async-http)

See also the [corresponding web service](https://bitbucket.org/loyolachicagocs_distributed/primenumbers-spray-scala).

# Setting up the Environment

Check out the project using Android Studio. This creates the `local.properties` file
with the required line

    sdk.dir=<root folder of Android Studio's Android SDK installation>

# Running the Application

In Android Studio: `Run > Run PrimeNumbers`

## Sample prime numbers to try

The underlying links invoke the corresponding web service on the number.

- [1013](http://laufer-primechecker.herokuapp.com/1013)
- [10007](http://laufer-primechecker.herokuapp.com/10007)
- [100003](http://laufer-primechecker.herokuapp.com/100003)
- [1000003](http://laufer-primechecker.herokuapp.com/1000003)
- [10000169](http://laufer-primechecker.herokuapp.com/10000169)
- [100000007](http://laufer-primechecker.herokuapp.com/100000007)

# A Tale of Two AsyncHttpClients

Do not use [this AsyncHttpClient](http://sonatype.github.io/async-http-client) in Android.
It will throw the infamous `android.os.NetworkOnMainThreadException` without explicit configuration
and has a heavyweight dependency on non-Android logging (slf4j).

# References

- [Jason Christensen's OOPSLA 2009 presentation](http://www.slideshare.net/jasonc411/oopsla-2009-combining-rest-and-cloud-a-practitioners-report)

# TODO

* improve UI
* improve architecture
* architectural diagram
* testing
* define task in a way that it can be decomposed! 
  (e.g. each task to check divisibility within a specific range)
