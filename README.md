# Java SpringBoot Consul Autopilot Example

In this repository you will find a small Java application. This application
is written such that in can be managed by [ContainerPilot](https://www.joyent.com/containerpilot).

## This means that
 * The Java web application (backed by Tomcat) is a [12-factor app](https://12factor.net/).
 * The application is not backgrounded like is typical with Tomcat.
 * The application can dynamically reload its configuration upon receiving a SIGHUP signal.
 * The application connects to Consul and can read service registry information directly
   from it. This combined with dynamic reloads allow for the app to reconfigure its backends
   with zero downtime.
 * The application is ready to be installed into Docker and executed by ContainerPilot.
 
 
## This example requires
 
 * Java 8
 * Maven 3+
