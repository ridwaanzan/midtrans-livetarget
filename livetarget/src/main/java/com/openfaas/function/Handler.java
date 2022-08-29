package com.openfaas.function;

import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;
import okhttp3.OkHttpClient;

import java.util.logging.Logger;

public class Handler extends com.openfaas.model.AbstractHandler {
    public static OkHttpClient httpClient = new OkHttpClient.Builder().build();
    public static Logger logging          = Logger.getLogger("LIVETARGET");

    public IResponse Handle(IRequest req) {
        logging.info("======: tester");
        Response res = new Response();
	    res.setBody("Hello, world!");

        String path    = req.getPathRaw();
        String rawPath = req.getQueryRaw();

        if (rawPath != null) {
            path = path + rawPath;
        }

        if (path.contains("vulnerable")) {
            logging.info("======: vuln-payment");
            return RequestHandler.vulnHandler(req);
        } else if (path.contains("fixed")) {
            logging.info("======: fixing-payment");
            return RequestHandler.fixHandler(req);
        } else if (path.contains("all-products")) {
            return RequestHandler.productHandler(req);
        }

        return res;
    }
}
