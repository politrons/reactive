/**
 *
 * With Java 9 introduce module system where we can create our fat jar just with the module we need
 * for the execution of our application.
 * For now Java just start moving some package to some modules that are not part of the JDK by default
 * In our example if we want to use the package junit we need to specify in the module file.
 * Where I specify the package of my classes and the package of the classes that I need to use.
 */
module reactive {
    requires junit;
    requires jdk.incubator.httpclient;
    requires rxjava;
    requires vertx.core;
}