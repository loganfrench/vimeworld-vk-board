package su.lgn;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {
    public static String request(String url) {
        return request(url, new LinkedHashMap<>());
    }

    public static String request(String request, Map<String, Object> params) {
        try {
            URL url = new URL(request);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("charset", "utf-8");
            connection.setDoOutput(true);

            if(params.size() > 0) {
                connection.setRequestMethod("POST");
                StringBuilder data = new StringBuilder();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (data.length() != 0) data.append('&');
                    data.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    data.append('=');
                    data.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }

                byte[] dataBytes = data.toString().getBytes("UTF-8");
                connection.getOutputStream().write(dataBytes);
            }

            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = reader.readLine();
            reader.close();
            return result;

        } catch (Exception ex) {
            //ex.printStackTrace();
            return "{\"error\":{}}";
        }
    }
}
