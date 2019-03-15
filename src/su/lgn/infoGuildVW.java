package su.lgn;

import org.json.JSONObject;
import su.lgn.VK.VK;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class infoGuildVW {
    public static String configFile = "config.ini";
    public static String accessToken;
    public static int groupID;

    public static int guildID = 0;
    public static int topicID = 0;
    public static int commentID = 0;
    public static String topicName = "";

    public static boolean membersUpdater = false;
    public static boolean groupBot = false;

    public static void main(String[] args) throws IOException {
        if(!new File(configFile).exists()) installApp();

        String content = new String(Files.readAllBytes(Paths.get(configFile)));
        String[] split = content.split("\n");
        if(split.length <= 0) {
            reinstallApp();
            return;
        }
        for (String line : split) {
            line = line.replaceAll("\\/\\/.[\\s\\S]*", "").trim();
            String[] params = line.split("=");

            switch(params[0]) {
                case "access_token": {
                    accessToken = params[1];
                    break;
                }
                case "topicName": {
                    topicName = params[1];
                    break;
                }
                case "group_id": {
                    groupID = Integer.parseInt(params[1]);
                    break;
                }
                case "guild_id": {
                    guildID = Integer.parseInt(params[1]);
                    break;
                }
                case "membersUpdater": {
                    membersUpdater = Boolean.valueOf(params[1]);
                    break;
                }
                case "groupBot": {
                    groupBot = Boolean.valueOf(params[1]);
                    break;
                }
                case "topic_id": {
                    topicID = Integer.parseInt(params[1]);
                    break;
                }
                case "comment_id": {
                    commentID = Integer.parseInt(params[1]);
                    break;
                }
                default: {
                    reinstallApp();
                    return;
                }
            }
        }
        System.out.println("Данные загружены.");

        guildUpdated.update(guildID);
    }

    private static void reinstallApp() {
        System.out.println("Ошибка загрузки конфига, выполните установку повторно.");
        new File(configFile).delete();
        System.exit(0);
    }

    private static void installApp() throws IOException {
        String loginVK;
        String passVK;

        while (true) {
            System.out.println("Выберите способ авторизации ВКонтакте:");
            System.out.println("- 1. Логин/пароль ВКонтакте");
            System.out.println("- 2. Токен ВКонтакте (с правами groups)");
            int authType;
            try {
                authType = new Scanner(System.in).nextInt();
            } catch(Exception e) {
                continue;
            }

            if (authType == 1) {
                System.out.println("Введите логин от ВКонтакте:");
                loginVK = new Scanner(System.in).nextLine();
                System.out.println("Введите пароль от ВКонтакте:");
                passVK = new Scanner(System.in).nextLine();

                LinkedHashMap params = new LinkedHashMap();
                params.put("username", loginVK);
                params.put("password", passVK);
                params.put("client_id", "3697615");
                params.put("client_secret", "AlVXZFMUqyrnABp8ncuU");
                params.put("grant_type", "password");
                params.put("v", "5.80");

                JSONObject vkParse = new JSONObject(Utils.request("https://oauth.vk.com/token", params));
                if (!vkParse.has("access_token")) {
                    System.out.println("Попробуйте авторизоваться снова.");
                    continue;
                } else {
                    accessToken = vkParse.getString("access_token");
                    System.out.println("Авторизация прошла успешно.");
                }
                break;
            }
            else if (authType == 2) {
                System.out.println("Введите токен ВКонтакте:");
                accessToken = new Scanner(System.in).nextLine();

                LinkedHashMap params = new LinkedHashMap();
                params.put("user_id", 1);
                params.put("access_token", accessToken);

                JSONObject vkParse = VK.request("users.get", params);
                if (!vkParse.has("response")) {
                    System.out.println("Попробуйте авторизоваться снова.");
                    continue;
                }
                else {
                    System.out.println("Авторизация прошла успешно.");
                }
                break;
            }
        }

        while (true) {
            System.out.println("Введите ID Вашей группы ВКонтакте:");
            String group = new Scanner(System.in).nextLine();

            LinkedHashMap params = new LinkedHashMap();
            params.put("group_ids", group);
            params.put("access_token", accessToken);
            JSONObject vkParse = VK.request("groups.getById", params);

            if (!vkParse.has("response")) {
                System.out.println("Введенная вами группа не существует.");
                continue;
            }
            if (vkParse.getJSONArray("response").getJSONObject(0).getInt("is_admin") != 1) {
                System.out.println("Вы не редактор/администратор группы " + vkParse.getJSONArray("response").getJSONObject(0).getInt("name"));
                continue;
            }
            groupID = vkParse.getJSONArray("response").getJSONObject(0).getInt("id");
            break;
        }

        System.out.println("• Вы успешно авторизовали группу. ");
        System.out.println(" ");

        while(true) {
            System.out.println("Выберите ID гильдии:");
            int guild_id = new Scanner(System.in).nextInt();

            JSONObject rParse = new JSONObject(Utils.request("https://api.vime.world/guild/get?id=" + guild_id));
            if(!rParse.has("id")) {
                System.out.println("Гильдия не найдена. Попробуйте снова.");
                continue;
            }
            System.out.println("Вы успешно авторизовали гильдию " + rParse.getString("name"));
            guildID = rParse.getInt("id");
            break;
        }

        while(true) {
            System.out.println(" ");
            System.out.println("Выберите функции для активации:");
            System.out.println((membersUpdater ? "+" : "-") + " 1. Вставлять в пост состав гильдии и обновлять его каждое n-ое кол-во минут");
            //System.out.println((groupBot ? "+" : "-") + " 2. Отвечать на команды в сообщениях группы.");
            System.out.println("0. Завершить установку");

            int funcType = new Scanner(System.in).nextInt();
            if(funcType == 0) {
                if(!membersUpdater && !groupBot) {
                    System.out.println("Выберите хотя бы одну функцию для активации:");
                    continue;
                }
                break;
            }
            else if(funcType == 1) {
                if(membersUpdater) {
                    membersUpdater = false;
                }
                else {
                    while(true) {
                        System.out.println("Введите ID темы:");
                        System.out.println(" Например из \"topic-157940016_38526885\", ID темы будет: 38526885");
                        topicID = new Scanner(System.in).nextInt();

                        LinkedHashMap params = new LinkedHashMap();
                        params.put("group_id", groupID);
                        params.put("topic_id", topicID);
                        params.put("access_token", accessToken);
                        JSONObject vkParse = VK.request("board.getComments", params);

                        if (!vkParse.has("response")) {
                            System.out.println("ID темы не существует. Попробуйте снова.");
                            continue;
                        }

                        System.out.println("Вы активировали параметр автоматического изменения состава в теме.");
                        System.out.println("В директории со скриптом создался файл \"topicText.txt\" с готовым текстом.");

                        File file = new File("topicText.txt");
                        file.createNewFile();
                        FileWriter writer = new FileWriter(file);
                        writer.write("Привет, друг!\n");
                        writer.write("Мы гильдия %guildName%!\n");
                        writer.write("Наш уровень %guildLevel%!\n");
                        writer.write("Наш состав: \n");
                        writer.write("Лидеры:\n%guild_leaders%\n\n");
                        writer.write("Офицеры:\n%guild_officers%\n\n");
                        writer.write("Участники:\n%guild_members%\n\n");
                        writer.write("Вступай к нам!");
                        writer.flush();
                        writer.close();

                        membersUpdater = true;
                        commentID = vkParse.getJSONObject("response").getJSONArray("items").getJSONObject(0).getInt("id");
                        break;
                    }
                }
            }
            /*else if(funcType == 2) {

            }*/
        }

        System.out.println("Идет загрузка конфига, пожалуйста, подождите.");

        File file = new File(configFile);

        if(!file.exists()) file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write("access_token=" + accessToken + " // авторизация\n");
        writer.write("group_id=" + groupID + " // ИД группы\n");
        writer.write("guild_id=" + guildID + " // ИД гильдии\n");
        writer.write("membersUpdater=" + membersUpdater + " // статус работы обновления состава\n");
        writer.write("topic_id=" + topicID + " // ид обсуждения\n");
        writer.write("comment_id=" + commentID + " // ид комментария\n");
        writer.write("topicName=Топик обновлен %dateUpdate% // название темы (оставить пустым если не менять)\n");
        writer.write("groupBot=" + groupBot + " // статус работы бота лс\n");
        writer.flush();
        writer.close();

        System.out.println("• Настройка успешно завершена. Перезапустите скрипт, чтобы начать его работу");

        //int group = new Scanner(System.in).nextInt();

    }
}
