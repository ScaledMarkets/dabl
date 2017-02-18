# Test Suite

The test suite uses [`Cucumber-JVM`](https://github.com/cucumber/cucumber-jvm).
JUnit is not used: this is a
[behavioral](https://en.wikipedia.org/wiki/Behavior_Driven_Development)
test suite, not a unit test suite.

Unfortunately, Cucumber-JVM versions are often not backward compatible, and Cucumber-JVM
consists of a number of different components, each with its own
version lifecycle. If one is using Maven, the latter issue is not a problem,
but the lack of backward compatibility is a problem regardless: the Cucucmber-JVM API
has changed many times, and so one's test code depends on the versions of the
various Cucumber-JVM components.

This project uses these versions of the Cucumber-JVM components:

```
cucumber-core-1.1.8.jar
cucumber-java-1.1.8.jar
cucumber-jvm-deps-1.0.3.jar
gherkin-2.12.2.jar
```

These are specified in the [`makefile.inc`](https://github.com/ScaledMarkets/dabl/blob/master/makefile.inc)
configuration file. You can find them by searching for them online.

The test suite can be built via
the [`makefile`](https://github.com/ScaledMarkets/dabl/blob/master/makefile) with,
```
make compile_tests
```
and run via,
```
make test
```
