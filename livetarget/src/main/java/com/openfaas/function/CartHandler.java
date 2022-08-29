package com.openfaas.function;

import com.google.gson.Gson;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CartHandler {
    public static OkHttpClient httpClient   = new OkHttpClient.Builder().build();
    public static MediaType JSON            = MediaType.parse("application/json");
    public static Logger logging            = Logger.getLogger("LIVETARGET");
    public static Gson gson                 = new Gson();
    public static String oracleDBHandler    = System.getenv("ORACLE_DB_HANDLER");

    public static IResponse newTransaction(IRequest req) {
        Request request;
        Response response = new Response();
        Map getProduct = new HashMap<>(1);
        getProduct.put("query", "SELECT id,name,kategori,price,images FROM midtrans_livetarget");
        getProduct.put("type", "findall");

        String jsonReqBody = gson.toJson(getProduct);
        RequestBody requestBody = RequestBody.create(jsonReqBody, JSON);

        request = new Request.Builder()
                .url(oracleDBHandler)
                .post(requestBody)
                .build();
        logging.info("======: Hitting to " + request.url());

        try {
            okhttp3.Response resp = httpClient.newCall(request).execute();
            Map mapResponse = gson.fromJson(resp.body().string(), Map.class);
            if (mapResponse.containsKey("isSuccess")) {
            } else {
            }

            return response;
        } catch (Exception e) {}
    }


    public static IResponse addToCart(Map req) {
        Request request;
        Response response = new Response();
        Map getProduct = new HashMap<>(1);
        getProduct.put("query", "SELECT id,name,kategori,price,images FROM midtrans_livetarget");
        getProduct.put("type", "findall");

        String jsonReqBody = gson.toJson(getProduct);
        RequestBody requestBody = RequestBody.create(jsonReqBody, JSON);

        request = new Request.Builder()
                .url(oracleDBHandler)
                .post(requestBody)
                .build();
        logging.info("======: Hitting to " + request.url());

        try {
            okhttp3.Response resp = httpClient.newCall(request).execute();
            Map mapResponse = gson.fromJson(resp.body().string(), Map.class);
            if (mapResponse.containsKey("isSuccess")) {
            } else {
            }

            return response;
        }
    }
}
