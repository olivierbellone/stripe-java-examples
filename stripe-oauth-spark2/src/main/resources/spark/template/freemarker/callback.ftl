<!doctype html>
  <head>
    <title>Stripe OAuth Example</title>
  </head>
  <body>
    <p>Congratulations! The Stripe account <code>${account_id}</code> is now connected to your platform. You should save this id in your database, and use the <a href="https://stripe.com/docs/connect/authentication#authentication-via-the-stripe-account-header"><code>Stripe-Account</code></a> header to issue API calls on behalf of this account.</p>

    <p>Here is the raw body of the <a href="https://stripe.com/docs/connect/reference#post-token-response">response</a> to the token request:<br/>
    <pre>${raw_body}</pre>
    </p>
  </body>
</html>
