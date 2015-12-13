package com.stripe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import static spark.Spark.get;
import static spark.Spark.staticFileLocation;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.freemarker.FreeMarkerRoute;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public final class StripeOAuthSpark1App {
    public static final String AUTHORIZE_URI
        = "https://connect.stripe.com/oauth/authorize";
    public static final String TOKEN_URI
        = "https://connect.stripe.com/oauth/token";

    private StripeOAuthSpark1App() { }

    public static void main(final String[] args) throws IOException {
        // Read Stripe platform's client ID and secret API key
        ClassLoader classLoader = StripeOAuthSpark1App.class.getClassLoader();
        File keyFile = new File(classLoader.getResource("stripe/keys.ini")
                                .getFile());
        IniPreferences prefs = new IniPreferences(new Ini(keyFile));
        final String clientId = prefs.node("stripe").get("client_id", null);
        final String apiKey = prefs.node("stripe").get("api_key", null);

        // Path to static files
        staticFileLocation("/public");

        get(new FreeMarkerRoute("/") {
            @Override
            public ModelAndView handle(final Request request,
                                       final Response response) {
                // Simply display the index.ftl template
                Map<String, Object> viewObjects
                    = new HashMap<String, Object>();
                return modelAndView(viewObjects, "index.ftl");
            }
        });

        get(new Route("/authorize") {
            @Override
            public Object handle(final Request request,
                                 final Response response) {
                try {
                    URI uri = new URIBuilder(AUTHORIZE_URI)
                            .setParameter("response_type", "code")
                            .setParameter("scope", "read_write")
                            .setParameter("client_id", clientId)
                            .build();

                    // Redirect to Stripe /oauth/authorize endpoint
                    response.status(HttpStatus.SC_CREATED);
                    response.redirect(uri.toString());
                } catch (Exception e) {
                    response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                }
                return "";
            }
        });

        get(new FreeMarkerRoute("/oauth/callback") {
            @Override
            public ModelAndView handle(final Request request,
                                       final Response response) {
                Map<String, Object> viewObjects
                    = new HashMap<String, Object>();

                try {
                    CloseableHttpClient httpClient
                        = HttpClients.createDefault();
                    String code = request.queryParams("code");
                    URI uri = new URIBuilder(TOKEN_URI)
                            .setParameter("client_secret", apiKey)
                            .setParameter("grant_type", "authorization_code")
                            .setParameter("client_id", clientId)
                            .setParameter("code", code)
                            .build();

                    // Make /oauth/token endpoint POST request
                    HttpPost httpPost = new HttpPost(uri);
                    CloseableHttpResponse resp = httpClient.execute(httpPost);

                    // Grab stripe_user_id (use this to authenticate as the
                    // connected account)
                    String bodyAsString
                        = EntityUtils.toString(resp.getEntity());
                    Type t = new TypeToken<Map<String, String>>() { }.getType();
                    Map<String, String> map
                        = new GsonBuilder().create().fromJson(bodyAsString, t);
                    String accountId = map.get("stripe_user_id");

                    viewObjects.put("account_id", accountId);
                    viewObjects.put("raw_body", bodyAsString);

                    return modelAndView(viewObjects, "callback.ftl");
                } catch (Exception e) {
                    response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                    viewObjects.put("error", e.getMessage());
                    return modelAndView(viewObjects, "error.ftl");
                }
            }
        });
    }
}
