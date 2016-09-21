function selectCardNumber(cardNumber) {
  document.getElementById("card-number").value = cardNumber;

  // make up an expiry date
  document.getElementById("card-expiry-month").value = '12';
  document.getElementById("card-expiry-year").value = '2025';
}

function displayProcessing() {
  document.getElementById("processing").style.display = 'block';

  document.getElementById("charge-form").style.display = 'none';
  document.getElementById("result").style.display = 'none';
}

function displayResult(resultText) {
  document.getElementById("processing").style.display = 'none';

  document.getElementById("charge-form").style.display = 'block';
  document.getElementById("result").style.display = 'block';
  document.getElementById("result").innerText = resultText;
}

function initiateCharge(amount, currency) {
  var cardNumber = document.getElementById("card-number").value,
      expMonth = document.getElementById("card-expiry-month").value,
      expYear = document.getElementById("card-expiry-year").value,
      cvc = document.getElementById("card-cvc").value;

  var cardParams = {
    number: cardNumber,
    exp_month: expMonth,
    exp_year: expYear,
  };

  if (cvc.length > 0) {
    cardParams['cvc'] = cvc;
  }

  Stripe.token.create({ 'card': cardParams }, function(status, result) {
    if (status != 200) {
      var message = result['error']['message'];
      displayResult("Unexpected token response status: " + status + ". Error: " + message);
      return;
    }

    Stripe.threeDSecure.create({
      card: result.id,
      amount: amount,
      currency: currency,
    }, created3DSecure);
  });

  displayProcessing();

  return false;
}

// this function will be called when the 3D Secure creation call has completed
function created3DSecure(status, result) {
  if (status != 200) {
    var message = result['error']['message'];
    displayResult("Unexpected 3D Secure response status: " + status + ". Error: " + message);
    return;
  }

  var tdsToken = result['id'],
      redirectURL = result['redirect_url'],
      redirectStatus = result['status'];

  if (redirectStatus == 'succeeded') {
    displayResult("This card does not support 3D Secure authentication, but liability will be shifted to the card issuer.");
    return;
  } else if (redirectStatus != 'redirect_pending') {
    displayResult("Unexpected 3D Secure status: " + redirectStatus);
    return;
  }

  // we're able to continue with 3D Secure.
  // insert the iframe, and register our callback
  var container = document.getElementById("iframe-container");
  Stripe.threeDSecure.createIframe(redirectURL, container, function(response) {
    // hide the modal dialog again
    $("#modal").modal('hide');

    if (response.status == 'success') {
      $.post(
        "/complete",
        {
          tds_token: tdsToken,
        },
        createdCharge
      );
    } else {
      var msg = '3D Secure authentication failed: ' + response.error_code;
      displayResult(msg);
    }
  });

  // open the modal dialog
  $("#modal").modal({
    // don't allow the modal to be closed
    backdrop: "static",
    keyboard: false
  });
}

// this function will be called when the charge creation call has completed.
function createdCharge(response) {
  console.log(response);

  var status = response['status'],
      chargeId = response['charge_id'],
      errorMessage = response['error_message'];

  if (status == 200) {
    displayResult("Charge " + chargeId + " was successful!");
  } else {
    displayResult("Charge failed with status " + status + ". Error message was: " + errorMessage);
  }
}
