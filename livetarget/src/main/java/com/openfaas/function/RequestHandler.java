package com.openfaas.function;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;
import jdk.jshell.execution.Util;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RequestHandler {
    public static OkHttpClient httpClient   = new OkHttpClient.Builder().build();
    public static MediaType JSON            = MediaType.parse("application/json");
    public static Logger logging            = Logger.getLogger("LIVETARGET");
    public static Gson gson                 = new Gson();
    public static String midtransBaseUrl    = System.getenv("MIDTRANS_URL");
    public static String midtransMerchantId = System.getenv("MIDTRANS_MERCHANT_ID");
    public static String midtransClientKey  = System.getenv("MIDTRANS_CLIENT_KEY");
    public static String midtransServerKey  = System.getenv("MIDTRANS_SERVER_KEY");
    public static String oracleDBHandler    = System.getenv("ORACLE_DB_HANDLER");

    public static IResponse vulnHandler(IRequest req) {
        logging.info("======: Vuln Handler");
        Request request;
        String body      = req.getBody();
        Response res     = new Response();

        Map<String, Object> mapReqBody = gson.fromJson(body, Map.class);
        String reqBody                 = "{\n" +
                                        "    \"payment_type\": \"bank_transfer\",\n" +
                                        "    \"transaction_details\": {\n" +
                                        "        \"order_id\": \"" + mapReqBody.get("order_id") + "\",\n" +
                                        "        \"gross_amount\": " + Integer.valueOf(mapReqBody.get("gross_amount").toString()) + "\n" +
                                        "    },\n" +
                                        "    \"bank_transfer\": {\n" +
                                        "        \"bank\": \"" + mapReqBody.get("bank") + "\"\n" +
                                        "    }\n" +
                                        "}";
        RequestBody requestBody        = RequestBody.create(reqBody, JSON);
        String secretKey               = hashBaset64(midtransServerKey + ":");
        String authHeaders = "Basic " + secretKey;

        logging.info("======: Secretkey: " + secretKey);
        logging.info("======: RequestBody: " + reqBody);

        request = new Request.Builder()
                .url(midtransBaseUrl)
                .post(requestBody)
                .addHeader("Authorization", authHeaders)
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
                .build();

        logging.info("======: Hitting endpoint: " + request.url());
        logging.info("======: Header request: " + request.headers().toMultimap().toString());

        try {
            okhttp3.Response response1 = httpClient.newCall(request).execute();
            String respon              = response1.body().string();
            Map mapResp                = gson.fromJson(respon, Map.class);
            logging.info("======: " + respon);

            if (mapResp.get("status_code").equals("201")) {
                Response result = UtilsResponse.rpvResponse(Boolean.TRUE, 1000, "Berhasil", mapResp);
                res.setBody(result.getBody());
            } else {
                Response result = UtilsResponse.rpvResponse(Boolean.FALSE, 1666, String.valueOf(mapResp.get("status_message")), "");
                res.setBody(result.getBody());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Response result = UtilsResponse.rpvResponse(Boolean.FALSE, 1000, e.getMessage(), "");
            res.setBody(result.getBody());
            return res;
        }

        res.setContentType("application/json");
        return res;
    }

    public static IResponse productHandler(IRequest req) {
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
            Map mapResponse       = gson.fromJson(resp.body().string(), Map.class);
            if (mapResponse.containsKey("isSuccess")) {
                if (mapResponse.get("isSuccess").equals(Boolean.TRUE)) {
                    Response result = UtilsResponse.rpvResponse(Boolean.TRUE, 2000, "Success get products", mapResponse);
                    response.setBody(result.getBody());
                    response.setHeader("Access-Control-Allow-Origin", "*");
                    response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD");
                } else {
                    Response result = UtilsResponse.rpvResponse(Boolean.FALSE, 2666, "Failed get products", "");
                    response.setBody(result.getBody());
                }
            } else {
                Response result = UtilsResponse.rpvResponse(Boolean.FALSE, 2666, "Failed get products", "");
                response.setBody(result.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Response result = UtilsResponse.rpvResponse(Boolean.FALSE, 2666, e.getMessage(), "");
            response.setBody(result.getBody());
        }

        response.setContentType("application/json");
        response.setStatusCode(200);
        return response;
    }

    public static IResponse fixHandler(IRequest req) {
        Request request;
        Response response              = new Response();
        Map<String, Object> mapReqBody = gson.fromJson(req.getBody(), Map.class);
        String reqBody                 = gson.toJson(mapReqBody);
        RequestBody requestBody        = RequestBody.create(reqBody, JSON);
        String secretKey               = hashBaset64(midtransServerKey + ":");

        request = new Request.Builder()
                .url(midtransBaseUrl)
                .post(requestBody)
                .addHeader("Authentication", "Bearer " + secretKey)
                .addHeader("content-type", "application/json")
                .addHeader("accept", "application/json")
                .build();

        logging.info("====== Hitting endpoint: " + request.url());

        try {
            okhttp3.Response midtransResponse = httpClient.newCall(request).execute();
            response.setBody(midtransResponse.body().string());
            response.setContentType("application/json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String hashBaset64(String key) {
        String hashBase64 = Base64.getEncoder().withoutPadding().encodeToString(key.getBytes());

        return hashBase64;
    }
}
