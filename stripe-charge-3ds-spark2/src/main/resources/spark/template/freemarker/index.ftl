<!doctype html>
  <head>
    <title>Stripe charge creation example</title>
    <script src="https://code.jquery.com/jquery-2.2.4.min.js" integrity="sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44=" crossorigin="anonymous"></script>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
    <script type="text/javascript" src="https://js.stripe.com/v2/"></script>
    <script>Stripe.setPublishableKey('${publishable_key}');</script>
    <script type="text/javascript" src="/js/3d-secure-demo.js"></script>

    <style type="text/css">
#processing {
  display: none;
}

#result {
  padding: 15px;
  display: none;
}

#iframe-container iframe {
  width: 100%;
  height: 400px;
}
    </style>
  </head>
  <body>
    <div class="container">

      <div class="page-header">
        <h1>Stripe 3D Secure demo</h1>
        <p class="lead">
          This page demonstrates Stripe's 3D Secure API.
        </p>
      </div>

      <p id="result" class="bg-info"></p>

      <form class="form-horizontal" id="charge-form">
        <div class="form-group">
          <label for="amount" class="col-sm-2">Amount</label>
          <div class="col-sm-10">
            10.99 EUR
          </div>
        </div>

        <div class="form-group">
          <label for="card-number" class="col-sm-2">Card Number</label>
          <div class="col-sm-10">
            <input type="text" class="form-control" id="card-number" placeholder="4242...">
          </div>
        </div>
        <div class="form-group">
          <label for="card-expiry-month" class="col-sm-2">Expiry Date</label>
          <div class="col-sm-10 form-inline">
            <input type="text" class="form-control" id="card-expiry-month" placeholder="12"> /
            <input type="text" class="form-control" id="card-expiry-year" placeholder="2019">
          </div>
        </div>
        <div class="form-group">
          <label for="card-cvc" class="col-sm-2">CVC</label>
          <div class="col-sm-10 form-inline">
            <input type="text" class="form-control" id="card-cvc" placeholder="123">
          </div>
        </div>

        <div class="form-group">
          <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-default" onclick="initiateCharge(1099, 'eur'); return false">Charge Card</button>
          </div>
        </div>
      </form>

      <div id="processing">
        <p class="text-center">Processing...</p>
      </div>

      <div id="modal" class="modal fade" tabindex="-1" role="dialog">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-body" id="iframe-container">
            </div>
          </div>
        </div>
      </div>

      <h2>Test Cards</h2>
      <table id="test-card-numbers" class="table">
        <tr>
          <td>
            <a href="#" onclick="selectCardNumber('4242424242424242'); return false">
                4242424242424242
              </a>
          </td>
          <td>
            3D Secure authentication not supported, but will charge successfully
          </td>
        </tr>
        <tr>
          <td>
            <a href="#" onclick="selectCardNumber('4000000000003055'); return false">
                4000000000003055
              </a>
          </td>
          <td>
            3D Secure authentication supported, will go through the full 3D Secure flow
          </td>
        </tr>
        <tr>
          <td>
            <a href="#" onclick="selectCardNumber('4000000000003063'); return false">
                4000000000003063
              </a>
          </td>
          <td>
            3D Secure authentication required, charge will be declined without it
          </td>
        </tr>
      </table>
    </div>
  </body>
</html>
