package su.lgn.VK;

import org.json.JSONObject;
import su.lgn.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class VK {
    public static JSONObject request(String method) {
        return request(method, new LinkedHashMap<>());
    }

    public static JSONObject request(String method, Map<String, Object> params) {
        params.put("v", "5.80");
        return new JSONObject(Utils.request("https://api.vk.com/method/" + method.trim(), params));
    }
}
