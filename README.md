# Информация
Это самая первая тестовая софтина на Java, написанная кучу лет назад. Обсуждениям и критике не подлеждит!!!

# Использование
При первом запуске программы, программа предложит пройти установку.

Шаги:
1. Тип авторизации: логин/пароль, токен.
2. ID группы.
3. ID гильдии.
4. Функции (работа в обсуждении)

После ввода всех этих данных, программа создаст файл `topicText.txt`, в котором будет текст, который будет обновлять обсуждение.
В тексте присутствуют переменные, которые будут заменяться на нужные данные.

Переменные: 
- %guildName% - название гильдии
- %guildLevel% - уровень гильдии
- %guild_leaders% - список лидеров (через \n)
- %guild_officers% - список офицеров (через \n)
- %guild_members% - список участников (через \n)
- %dateUpdate% - дата обновления (в формате: `dd.MM.yyyy HH:mm:ss`)

Скриншоты:
1. ![Скриншот 1](/screen_1.jpg)
