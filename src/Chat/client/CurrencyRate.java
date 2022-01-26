package Chat.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class CurrencyRate {

    public static String rate(String currency) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet("https://www.cbr-xml-daily.ru/latest.js");
        HttpResponse response = null;
        {
            try {
                response = httpClient.execute(get);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HttpEntity entity = response.getEntity();
        JSONObject obj = null;
        {
            try {
                obj = new JSONObject(EntityUtils.toString(entity));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        double rubCurr = obj.getJSONObject("rates").getDouble(currency.toUpperCase());
        double currRub = 1 / rubCurr;
        return String.format("%.4f", currRub);
    }
}