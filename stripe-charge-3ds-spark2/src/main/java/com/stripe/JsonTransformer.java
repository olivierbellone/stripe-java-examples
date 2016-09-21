package com.stripe;

import spark.ResponseTransformer;

import com.google.gson.Gson;

public class JsonTransformer implements ResponseTransformer {

    private Gson gson = new Gson();

    @Override
    public final String render(final Object model) {
        return gson.toJson(model);
    }
}
