package com.xiay.applib.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

/**
 * Created by Xiay on 2016/12/1.
 */

public class GsonUtil  <T>{

    /**
     *  将json数组转成 ArrayList
     * @param json
     * @param cls
     * @return
     */
    public  ArrayList<T> fromJsonArrayList(String json, Class<T> cls) {
        ArrayList<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        Gson gson = new Gson();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }
}
