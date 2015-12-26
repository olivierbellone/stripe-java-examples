<!doctype html>
  <head>
    <title>Stripe charge creation example</title>
  </head>
  <body>
    <form action="/charge" method="POST">
      <script
        src="https://checkout.stripe.com/checkout.js" class="stripe-button"
        data-key="${publishable_key}"
        data-image="/img/marketplace.png"
        data-name="Stripe.com"
        data-description="Example charge"
        data-amount="${amount}"
        data-currency="${currency}"
        data-locale="auto">
      </script>
    </form>
  </body>
</html>
