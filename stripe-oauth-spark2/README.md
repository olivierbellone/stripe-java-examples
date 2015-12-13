# Stripe OAuth example, using Java and Spark 2.x

This is a simple example project illustrating how to implement [Stripe's OAuth flow](https://stripe.com/docs/connect/standalone-accounts) to connect standalone accounts to a platform.

This example uses the Spark framework version 2.x, so it requires Java 1.8 or later. If you're using Java 1.7, you should look at the example using Spark 1.x [here](https://github.com/olivierbellone/stripe-java-examples/stripe-oauth-spark1).

Requirements
============

- Java 1.8 and later.
- Maven (<https://maven.apache.org/>)

The project has some dependencies, Maven will take care of downloading everything.

Usage
=====

Clone the repository:

    git clone https://github.com/olivierbellone/stripe-java-examples.git

Move into the project's directory:

    cd stripe-java-examples/stripe-oauth-spark2

Update the `src/main/resources/stripe/keys.ini` file with your Stripe platform's client ID (available in the [Connect tab](https://dashboard.stripe.com/account/applications/settings) of your account settings) and your secret API key (available in the [API Keys tab](https://dashboard.stripe.com/account/apikeys) of your account settings).

Update the redirect URI in your [platform settings](https://dashboard.stripe.com/account/applications/settings) to `http://localhost:4567/oauth/callback`.

Compile the project using Maven:

    mvn compile

Run the project:

    mvn exec:java

Finally, point your browser to <http://localhost:4567> to see the example in action.
