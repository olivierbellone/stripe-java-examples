package com.stripe;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import static spark.Spark.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpStatus;

import com.stripe.exception.AuthenticationException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Event;
import com.stripe.net.APIResource;
import com.stripe.net.RequestOptions;


public final class StripeWebhooksSpark2App {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(StripeWebhooksSpark2App.class);

    private StripeWebhooksSpark2App() { }

    public static void main(final String[] args) throws IOException {
        // Read Stripe secret API key
        ClassLoader classLoader = StripeWebhooksSpark2App.class
                .getClassLoader();
        File keyFile = new File(classLoader.getResource("stripe/keys.ini")
                                .getFile());
        IniPreferences prefs = new IniPreferences(new Ini(keyFile));
        final String apiKey = prefs.node("stripe").get("api_key", null);

        // Set the secret API key
        Stripe.apiKey = apiKey;

        post("/webhooks", (request, response) -> {
            Event eventJson;
            try {
                // Retrieve the request's body and parse it as JSON
                eventJson = APIResource.GSON.fromJson(request.body(),
                                                      Event.class);
            } catch (Exception e) {
                String error = "Error: " + e.getMessage();
                LOGGER.warn(error);
                response.status(HttpStatus.SC_BAD_REQUEST);
                return error;
            }

            LOGGER.info("Received event: " + eventJson.getId()
                        + ", type: " + eventJson.getType()
                        + ", user_id: " + eventJson.getUserId());

            // Event verification: account.application.deauthorized
            if (eventJson.getType()
                .equals("account.application.deauthorized")) {
                // (If you're not a Connect platform, or if you're a
                // Connect platform that only deals with managed
                // accounts and not standalone accounts, then you don't
                // need to worry about this event.)

                // account.application.deauthorized needs to be treated
                // differently: if the event is legit, then we cannot
                // verify it by fetching it from Stripe, as we are no
                // longer authorized to fetch events from this account!
                // So we try to get the account information: if we can't,
                // that means the event was legit.

                // NOTE: in practice, you should first verify that the
                // account was connected to your platform in the first
                // place, by checking your own database.
                try {
                    Account.retrieve(eventJson.getUserId(), null);
                } catch (AuthenticationException e) {
                    LOGGER.info("Event verified!");

                    // Do something here to handle the deauthorization

                    // Reply with 200 status code to acknowledge receipt of
                    // the webhook
                    response.status(HttpStatus.SC_OK);
                    return "";
                } catch (StripeException e) {
                    // Some other Stripe error happened
                    String error = "Stripe error: " + e.getMessage();
                    LOGGER.warn(error);
                    response.status(e.getStatusCode());
                    return error;
                }

                // Invalid event
                String error = "Invalid event: " + eventJson.getId();
                LOGGER.warn(error);
                response.status(HttpStatus.SC_BAD_REQUEST);
                return error;
            }

            // Event verification: general case
            Event event;
            try {
                // Fetch the event from Stripe
                RequestOptions requestOptions = null;
                if (eventJson.getUserId() != null) {
                    requestOptions = RequestOptions.builder()
                            .setStripeAccount(eventJson.getUserId()).build();
                }
                event = Event.retrieve(eventJson.getId(), requestOptions);
            } catch (InvalidRequestException e) {
                // Invalid event
                String error = "Invalid event: " + eventJson.getId();
                LOGGER.warn(error);
                response.status(HttpStatus.SC_BAD_REQUEST);
                return error;
            } catch (StripeException e) {
                // Some other Stripe error happened
                String error = "Stripe error: " + e.getMessage();
                LOGGER.warn(error);
                response.status(e.getStatusCode());
                return error;
            }

            LOGGER.info("Event verified!");

            // Do something with event

            // Reply with 200 status code to acknowledge receipt of the
            // webhook
            response.status(HttpStatus.SC_OK);
            return "";
        });
    }
}
