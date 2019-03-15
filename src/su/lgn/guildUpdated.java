package su.lgn;

import org.json.JSONObject;
import su.lgn.VK.VK;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

public class guildUpdated {
    public static boolean shedulerStatus = false;

    public static String getPrefix(String rank) {
        switch (rank) {
            case "PLAYER": return "";
            case "VIP": return "[VIP] ";
            case "PREMIUM": return "[Premium] ";
            case "HOLY": return "[Holy] ";
            case "IMMORTAL": return "[Immortal] ";
            case "BUILDER": return "[Билдер] ";
            case "MAPLEAD": return "[Гл. Билдер] ";
            case "YOUTUBE": return "[YouTube] ";
            case "MODER": return "[Модер] ";
            case "WARNED": return "[Пр. Модер] ";
            case "CHIEF": return "[Гл. Модер] ";
            case "ORGANIZER": return "[Организатор] ";
            case "DEV": return "[Разработчик] ";
            case "ADMIN": return "[Гл. Админ] ";
            default: return "[" + rank + "] ";
        }
    }
    public static boolean update(int guildID) throws IOException {
        System.out.println("Обновление гильдии.");
        long start = System.currentTimeMillis();

        String configText = new String(Files.readAllBytes(Paths.get("topicText.txt")));
        String text;
        String guild_leaders = "";
        String guild_officers = "";
        String guild_members = "";

        JSONObject guild = new JSONObject(Utils.request("https://api.vime.world/guild/get?id=" + guildID));

        if(!guild.has("id")) {
            System.out.println("Гильдия не найдена.");
            return false;
        }

        for (int i = 0; i < guild.getJSONArray("members").length(); i++) {
            JSONObject params = guild.getJSONArray("members").getJSONObject(i);

            if(params.getString("status").equals("LEADER")) guild_leaders += getPrefix(params.getJSONObject("user").getString("rank")) + " " + params.getJSONObject("user").getString("username") + "\n";
            else if(params.getString("status").equals("OFFICER")) guild_officers += getPrefix(params.getJSONObject("user").getString("rank")) + " " + params.getJSONObject("user").getString("username") + "\n";
            else guild_members += getPrefix(params.getJSONObject("user").getString("rank")) + " " + params.getJSONObject("user").getString("username") + "\n";
        }

        configText = configText.replace("%guildName%", guild.getString("name"));
        configText = configText.replace("%guildLevel%", String.valueOf(guild.getInt("level")));
        configText = configText.replace("%guild_leaders%", guild_leaders);
        configText = configText.replace("%guild_officers%", guild_officers);
        configText = configText.replace("%guild_members%", guild_members);

        LinkedHashMap params = new LinkedHashMap();
        params.put("group_id", infoGuildVW.groupID);
        params.put("topic_id", infoGuildVW.topicID);
        params.put("comment_id", infoGuildVW.commentID);
        params.put("message", configText);
        params.put("access_token", infoGuildVW.accessToken);

        VK.request("board.editComment", params);

        if(!infoGuildVW.topicName.trim().equals("")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date currentDate = new Date();

            params = new LinkedHashMap();
            params.put("group_id", infoGuildVW.groupID);
            params.put("topic_id", infoGuildVW.topicID);
            params.put("title", infoGuildVW.topicName.trim().replace("%dateUpdate%", dateFormat.format(currentDate)));
            params.put("access_token", infoGuildVW.accessToken);

            VK.request("board.editTopic", params);
        }


        System.out.println("Гильдия обновлена за (" + (System.currentTimeMillis() - start) + ") мс.");
        return true;
    }
}
