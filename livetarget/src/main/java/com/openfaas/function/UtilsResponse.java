package com.openfaas.function;

import com.google.gson.Gson;
import com.openfaas.model.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class UtilsResponse {
    public static Gson gson = new Gson();
    public static Logger logging = Logger.getLogger("LIVETARGET");

    public static Response rpvResponse(Boolean status, Integer code, String message, Object payload) {
        Response res               = new Response();
        Map<String, Object> result = new HashMap<>();
        String codeResponse        = String.valueOf(code);

        result.put("isSuccess", status);
        result.put("responseCode", codeResponse);
        result.put("message", message);
        result.put("payload", payload);

        res.setBody(gson.toJson(result));
        res.setContentType("application/json");
        logging.info(res.getBody());
        return res;
    }
}
