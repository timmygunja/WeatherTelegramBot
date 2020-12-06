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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Bot extends TelegramLongPollingBot {
    String city = "Лондон";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }


    public synchronized void setButtons(SendMessage sendMessage) {

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
        keyboardSecondRow.add(new KeyboardButton("Help"));

        // Добавляем все строки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }


    /**
     * Метод для приема сообщений.
     * @param update Содержит сообщение от пользователя.
     */

    @Override
    public void onUpdateReceived(Update update) {
        String chadId = update.getMessage().getChatId().toString();
        String message = update.getMessage().getText();
//        sendMsg(chadId, message);
        try {
            switch (message) {
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
                default:
                    city = message;
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
//        sendMessage.setReplyMarkup();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
//            log.log(Level.SEVERE, "Exception: ", e.toString());
            System.out.println("Exception: " + e.toString());
        }
    }


    /**
     * Метод возвращает имя бота, указанное при регистрации.
     * @return имя бота
     */

    @Override
    public String getBotUsername() {
        return "WeatherTelegramFinBot";
    }


    /**
     * Метод возвращает token бота для связи с сервером Telegram
     * @return token для бота
     */

    @Override
    public String getBotToken() {
        return "1425042061:AAGaKa5JvK3pHhh3x4PTtlpiAPyIe0kUSf8";
    }
}