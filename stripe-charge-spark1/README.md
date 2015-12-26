# Stripe charge creation example, using Java and Spark 1.x

This is a simple example project illustrating how to [create a charge](https://stripe.com/docs/tutorials/charges).

This example uses the Spark framework version 1.x in order to be compatible with Java 1.7. If you're using Java 1.8 or later, you should look at the example using Spark 2.x [here](../stripe-charge-spark2).

Requirements
============

- Java 1.7 and later.
- Maven (<https://maven.apache.org/>)

The project has some dependencies, Maven will take care of downloading everything.

Usage
=====

Clone the repository:

    git clone https://github.com/olivierbellone/stripe-java-examples.git

Move into the project's directory:

    cd stripe-java-examples/stripe-charge-spark1

Update the `src/main/resources/stripe/keys.ini` file with your Stripe API keys (available in the [API Keys tab](https://dashboard.stripe.com/account/apikeys) of your account settings).

Compile the project using Maven:

    mvn compile

Run the project:

    mvn exec:java

Finally, point your browser to <http://localhost:4567> to see the example in action.
