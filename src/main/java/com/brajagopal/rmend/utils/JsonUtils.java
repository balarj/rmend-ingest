package com.brajagopal.rmend.utils;

import com.brajagopal.rmend.data.beans.DocumentBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author <bxr4261>
 */
public class JsonUtils {

    private static final Gson gsonInstance;

    static {
        // Configure GSON
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DocumentBean.class, new DocumentBean.DocumentSerDe());
        gsonInstance = gsonBuilder.create();
    }

    public static Gson getGsonInstance() {
        return gsonInstance;
    }
}
