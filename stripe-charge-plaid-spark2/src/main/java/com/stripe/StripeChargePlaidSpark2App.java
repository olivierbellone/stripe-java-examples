package com.stripe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;


public final class StripeChargePlaidSpark2App {
    public static final String PLAID_EXCHANGE_TOKEN_URI
        = "https://tartan.plaid.com/exchange_token";

    private static final int CHARGE_AMOUNT = 400; // amount in cents
    private static final String CHARGE_CURRENCY = "usd";

    private static final Logger LOGGER
        = LoggerFactory.getLogger(StripeChargePlaidSpark2App.class);

    private StripeChargePlaidSpark2App() { }

    public static void main(final String[] args) throws IOException {
        // Read Stripe platform's client ID and secret API key
        ClassLoader classLoader
            = StripeChargePlaidSpark2App.class.getClassLoader();
        File keyFile = new File(classLoader.getResource("stripe/keys.ini")
                                .getFile());
        IniPreferences prefs = new IniPreferences(new Ini(keyFile));
        final String stripeSecretApiKey
            = prefs.node("stripe").get("secret_api_key", null);
        final String plaidClientId
            = prefs.node("plaid").get("client_id", null);
        final String plaidSecret = prefs.node("plaid").get("secret", null);
        final String plaidPublicKey
            = prefs.node("plaid").get("public_key", null);

        // Set the secret API key
        Stripe.apiKey = stripeSecretApiKey;

        get("/", (request, response) -> {
            // Display the index.ftl template, with the parameters for the
            // Plaid form
            Map<String, Object> viewObjects
                = new HashMap<String, Object>();
            viewObjects.put("plaid_public_key", plaidPublicKey);
            viewObjects.put("amount", CHARGE_AMOUNT);
            viewObjects.put("currency", CHARGE_CURRENCY);
            return new ModelAndView(viewObjects, "index.ftl");
        }, new FreeMarkerEngine());

        post("/charge", (request, response) -> {
            Map<String, Object> viewObjects
                = new HashMap<String, Object>();

            // Get the parameters returned by Plaid
            String publicToken = request.queryParams("public_token");
            String accountId = request.queryParams("account_id");

            try {
                // Make request to Plaid's API
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost(PLAID_EXCHANGE_TOKEN_URI);

                List<NameValuePair> plaidParams
                    = new ArrayList<NameValuePair>();
                plaidParams.add(new BasicNameValuePair("client_id",
                                                       plaidClientId));
                plaidParams.add(new BasicNameValuePair("secret", plaidSecret));
                plaidParams.add(new BasicNameValuePair("public_token",
                                                       publicToken));
                plaidParams.add(new BasicNameValuePair("account_id",
                                                       accountId));

                HttpEntity postParams
                    = new UrlEncodedFormEntity(plaidParams);
                httpPost.setEntity(postParams);

                CloseableHttpResponse resp = httpClient.execute(httpPost);

                // Grab stripe_bank_account_token
                String bodyAsString
                    = EntityUtils.toString(resp.getEntity());
                System.out.println(bodyAsString);
                Type t = new TypeToken<Map<String, String>>() { }.getType();
                Map<String, String> map
                    = new GsonBuilder().create().fromJson(bodyAsString, t);
                String bankToken = map.get("stripe_bank_account_token");

                // Create the charge
                Map<String, Object> chargeParams
                    = new HashMap<String, Object>();
                chargeParams.put("amount", CHARGE_AMOUNT);
                chargeParams.put("currency", CHARGE_CURRENCY);
                chargeParams.put("source", bankToken);
                chargeParams.put("description", "Test Plaid charge");

                Charge charge = Charge.create(chargeParams);

                // Display the success page, with the charge ID
                viewObjects.put("charge_id", charge.getId());
                return new ModelAndView(viewObjects, "success.ftl");
            } catch (StripeException e) {
                // An error happened during the charge creation: display
                // the error message
                viewObjects.put("error", e.getMessage());
                return new ModelAndView(viewObjects, "error.ftl");
            }
        }, new FreeMarkerEngine());
    }
}
