import com.openfaas.function.RequestHandler;
import com.openfaas.model.Request;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.openfaas.function.Handler;
import com.openfaas.model.IHandler;

import java.util.HashMap;
import java.util.Properties;

public class HandlerTest {
    Properties props;
    @Before
    public void initialize() {
        props = new Properties();
        props.setProperty("midtransBaseUrl", "https://api.sandbox.midtrans.com/v2/charge");
        props.setProperty("midtransMerchantId", "M112183");
        props.setProperty("midtransClientKey", "SB-Mid-client-AFW54mwyHkUu3aie");
        props.setProperty("midtransServerKey", "SB-Mid-server-JwoLUwQriJYXexyXE79Xlo0t");
        props.setProperty("oracleDBHandler", "http://10.70.133.83:8080/function/oracle-db-handler");
    }

    @Test public void vulnTester() {
        Handler handler = new Handler();
        RequestHandler requestHandler = new RequestHandler();

        requestHandler.midtransBaseUrl    = props.getProperty("midtransBaseUrl");
        requestHandler.midtransMerchantId = props.getProperty("midtransMerchantId");
        requestHandler.midtransClientKey  = props.getProperty("midtransClientKey");
        requestHandler.midtransServerKey  = props.getProperty("midtransServerKey");
        requestHandler.oracleDBHandler    = props.getProperty("oracleDBHandler");

        String reqBody = "{\n" +
                        "    \"order_id\": \"order-107\",\n" +
                        "    \"gross_amount\": \"44000\",\n" +
                        "    \"bank\": \"bca\"\n" +
                        "}";

        Request request = new Request(reqBody, new HashMap<>(), new String(), "/vulnerable");
        System.out.println(handler.Handle(request).getBody());

        assertTrue(handler != null);
    }

    @Test public void productTester() {
        Handler handler = new Handler();
        Request request = new Request(null, new HashMap<>(), new String(), "/all-products");
        System.out.println(handler.Handle(request).getBody());
    }
}
