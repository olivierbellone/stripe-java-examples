<!doctype html>
  <head>
    <title>Stripe charge creation example</title>
    <script src="https://code.jquery.com/jquery-2.2.0.min.js"></script>
  </head>

  <body>
    <button id='linkButton'>Open Plaid Link</button>
    <script src="https://cdn.plaid.com/link/stable/link-initialize.js"></script>
    <script>
    var linkHandler = Plaid.create({
      env: 'tartan',
      clientName: 'Stripe / Plaid Test',
      key: '${plaid_public_key}',
      product: 'auth',
      selectAccount: true,
      onSuccess: function(public_token, metadata) {
        console.log('public_token: ' + public_token);
        console.log('account ID: ' + metadata.account_id);

        // Send the public_token and account ID to your app server.
        var form = $('<form>', {
            'action': '/charge',
            'method': 'POST'
        }).append($('<input>', {
            'name': 'public_token',
            'value': public_token,
            'type': 'hidden'
        })).append($('<input>', {
            'name': 'account_id',
            'value': metadata.account_id,
            'type': 'hidden'
        }));
        form.submit();
      },
    });

    // Trigger the Link UI
    document.getElementById('linkButton').onclick = function() {
      linkHandler.open();
    };
    </script>
  </body>
</html>
