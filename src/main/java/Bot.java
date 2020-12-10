import org.json.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

public class Bot extends TelegramLongPollingBot {
    String dir = System.getProperty("user.dir");
    String fileName = "subInfo.txt";
    String path = dir + File.separator + fileName;
    String city = "Москва";
    Map<String, String> subInfo;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

        Thread subscriptionThread = new subscriptionThread();
        subscriptionThread.start();
    }


    public static synchronized void setButtons(SendMessage sendMessage) {

        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();


        // Добавляем строки клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        // Добавляем кнопки клавиатуры
        keyboardFirstRow.add(new KeyboardButton("Указать город"));
        keyboardFirstRow.add(new KeyboardButton("Погода сейчас"));
        keyboardSecondRow.add(new KeyboardButton("Прогноз сегодня"));
        keyboardSecondRow.add(new KeyboardButton("Прогноз на завтра"));
//        keyboardSecondRow.add(new KeyboardButton("Помощь"));

        // Добавляем все строки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }


    @Override
    public void onUpdateReceived(Update update) {
        String chadId = update.getMessage().getChatId().toString();
        String message = update.getMessage().getText();

        try {
            switch (message) {
                case "/start":
                    sendMsg(chadId, "Добро пожаловать!\nПолучите прогноз, отправив название города и выбрав опцию кнопкой.");
                    break;
                case "Указать город":
                    sendMsg(chadId, "Отправьте имя города и затем выберите опцию прогноза");
                    break;
                case "Погода сейчас": {
                    JSONObject data = Parser.getCurrentData(city);
                    String answer = Parser.retrieveCurrentData(data);
                    sendMsg(chadId, answer);
                    break;
                }
                case "Прогноз сегодня": {
                    JSONObject data = Parser.getTodayData(city);
                    String answer = Parser.retrieveTodayData(data);
                    sendMsg(chadId, answer);
                    break;
                }
                case "Прогноз на завтра": {
                    JSONObject data = Parser.getTomorrowData(city);
                    String answer = Parser.retrieveTomorrowData(data);
                    sendMsg(chadId, answer);
                    break;
                }
                case "/subscribe": {
                    try {
                        write(chadId, city);
                        sendMsg(chadId, "Вы успешно подписались на рассылку по городу " + city +
                                "\nПрогноз на сутки будет приходить вам в 8:00 по московскому времени");
                    }
                    catch (Exception e) {
                        System.out.println("Exception: " + e.toString());
                    }
                    break;
                }
                case "/unsubscribe": {
                    try {
                        remove(chadId);
                        sendMsg(chadId, "Подписка прекращена, уведомления приходить не будут.");
                    }
                    catch (Exception e) {
                        System.out.println("Exception: " + e.toString());
                    }
                    break;
                }
                default:
                    this.city = message;
                    sendMsg(chadId, "Записан город " + city);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для настройки сообщения и его отправки.
     * @param chatId id чата
     * @param s Строка, которую необходимо отправить в качестве сообщения.
     */

    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        setButtons(sendMessage);
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
//            log.log(Level.SEVERE, "Exception: ", e.toString());
            System.out.println("Exception: " + e.toString());
        }
    }


    @Override
    public String getBotUsername() {
        return "WeatherTelegramFinBot";
    }


    @Override
    public String getBotToken() {
        return "1425042061:AAGaKa5JvK3pHhh3x4PTtlpiAPyIe0kUSf8";
    }

    public void write(String chatId, String city) throws IOException {
        FileWriter fw = new FileWriter(path, true);
        String content = chatId + " " + city + "\n";
        fw.write(content);
        fw.close();
    }


    public Map<String, String> read() throws IOException {
        Map<String, String> subInfo = new HashMap<>();
        FileReader fr = new FileReader(path);
        BufferedReader reader = new BufferedReader(fr);
        String line = reader.readLine();
        while (line != null) {
            String key = line.split(" ")[0];
            String value = line.split(" ")[1];
            subInfo.put(key, value);
            line = reader.readLine();
        }
        fr.close();
        return subInfo;
    }


    public void remove(String chatId) throws IOException {
        Map<String, String> subInfo = read();
        subInfo.remove(chatId);
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write("");
        fileWriter.close();
        FileWriter fw = new FileWriter(path, true);
        for (Map.Entry<String, String> user : subInfo.entrySet()) {
            String content = user.getKey() + " " + user.getValue() + "\n";
            fw.append(content);
        }
        fw.close();
    }

}


class subscriptionThread extends Thread {
    public void run() {
        Bot bot = new Bot();

        try {
            while (true)
            {
                if (LocalTime.now().getHour() > 5 && LocalTime.now().getHour() < 9) {
                    if (LocalTime.now().getHour() == 8) {
                        Map<String, String> subInfo = bot.read();

                        for (Map.Entry<String, String> user : subInfo.entrySet()) {
                            JSONObject data = Parser.getTodayData(user.getValue());
                            String answer = Parser.retrieveTodayData(data);
                            bot.sendMsg(user.getKey(), answer);
                        }
                        sleep(60_000 * 60);  // sleep for hour

                    } else sleep(15_000);
                }
                else sleep(60_000 * 60 * 3);
            }
        }
        catch (Exception e) {
                System.out.println("Exception: " + e.toString());
            }
    }
}