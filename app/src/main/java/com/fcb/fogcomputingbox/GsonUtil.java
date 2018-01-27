package com.fcb.fogcomputingbox;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by borui on 2017/12/20.
 */

public class GsonUtil {

    private static Gson gson = null;

    static {
        if (gson == null) {
//            gson = new Gson();
            gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        }
    }

    private GsonUtil() {
    }

    public static String GsonToString(Object object) {
        String gsonString = gson.toJson(object);
        return gsonString;
    }

    public static <T> T GsonToBean(String gsonString, Class<T> cls) {
        T t = gson.fromJson(gsonString, cls);
        return t;
    }


    public static <T> List<T> jsonToList(String gsonString, Class<T> cls) {

        ArrayList<T> list = new ArrayList<>();
        JsonArray jsonArray = new JsonParser().parse(gsonString).getAsJsonArray();
        for (JsonElement jsonElement : jsonArray) {
            list.add(gson.fromJson(jsonElement,cls));
        }
//        List<T> list = null;
//        if (gson != null) {
//            Type type = new TypeToken<List<T>>() {
//            }.getType();
//            LogUtils.e(type);
//            list = gson.fromJson(gsonString, type);
//        }
        return list;
    }

    public static <T> Map<String, T> GsonToMaps(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    public static BaseServerObj getObj(String json){

        if(TextUtils.isEmpty(json)){
            return new BaseServerObj();
        }

        BaseServerObj obj = new BaseServerObj();

        try {
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject != null){

                obj.IsSuccess = jsonObject.getBoolean("IsSuccess");

                obj.ReasonCode = jsonObject.getInt("ReasonCode");

                obj.Message = jsonObject.getString("Message");

                obj.DataCount = jsonObject.getString("DataCount");

                obj.Data = jsonObject.getString("Data");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;

    }

    public static class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringNullAdapter();
        }
    }

    public static class StringNullAdapter extends TypeAdapter<String> {
        @Override
        public String read(JsonReader reader) throws IOException {
            // TODO Auto-generated method stub
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }
        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            // TODO Auto-generated method stub
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }


}
