# Stripe Java examples

This repository contains example projects showing how to implement some of [Stripe](https://stripe.com)'s features using the Java programming language.

### Charge creation examples

These example projects illustrate how to [create a charge](https://stripe.com/docs/tutorials/charges).

- [stripe-charge-spark1](./stripe-charge-spark1) uses the Spark framework version 1.x and is suitable for use with Java 1.7.
- [stripe-charge-spark2](./stripe-charge-spark2) uses the Spark framework version 2.x and is suitable for use with Java 1.8 and later.

### Charge creation with Plaid examples

These example projects illustrate how to [create a charge with Plaid](https://stripe.com/docs/guides/ach).

- [stripe-charge-plaid-spark1](./stripe-charge-plaid-spark1) uses the Spark framework version 1.x and is suitable for use with Java 1.7.
- [stripe-charge-plaid-spark2](./stripe-charge-plaid-spark2) uses the Spark framework version 2.x and is suitable for use with Java 1.8 and later.

### ~~Charge creation with 3D-Secure examples~~

~~These example projects illustrate how to [create a charge with 3D-Secure](https://stripe.com/docs/3d-secure).~~

- ~~[stripe-charge-3ds-spark1](./stripe-charge-3ds-spark1) uses the Spark framework version 1.x and is suitable for use with Java 1.7.~~
- ~~[stripe-charge-3ds-spark2](./stripe-charge-3ds-spark2) uses the Spark framework version 2.x and is suitable for use with Java 1.8 and later.~~

These examples use an old API that is no longer available. You should now use the [sources API](https://stripe.com/docs/sources/three-d-secure) to implement 3D Secure. Examples coming soon.

### Webhooks examples

These example projects illustrate how to implement a [Stripe webhooks endpoint](https://stripe.com/docs/webhooks).

- [example-stripe-java7-webhook](https://github.com/ob-stripe/example-stripe-java7-webhook) uses the Spark framework version 1.x and is suitable for use with Java 1.7.
- [example-stripe-java8-webhook](https://github.com/ob-stripe/example-stripe-java8-webhook) uses the Spark framework version 2.x and is suitable for use with Java 1.8 and later.

### OAuth examples

These example projects illustrate how to implement [Stripe's OAuth flow](https://stripe.com/docs/connect/standalone-accounts) to connect standalone accounts to a platform.

- [stripe-oauth-spark1](./stripe-oauth-spark1) uses the Spark framework version 1.x and is suitable for use with Java 1.7.
- [stripe-oauth-spark2](./stripe-oauth-spark2) uses the Spark framework version 2.x and is suitable for use with Java 1.8 and later.
