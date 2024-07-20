package com.trade.bot.bot;

import com.bybit.api.client.domain.websocket_message.public_channel.PublicOrderBookData;
import com.bybit.api.client.domain.websocket_message.public_channel.WebsocketOrderbookMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.config.BotConfig;
import com.trade.bot.entity.TelegramUser;
import com.trade.bot.repository.UserRepository;
import com.trade.bot.service.BybitTradeFetcher;
import com.trade.bot.service.bybit.BybitOrderService;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final String L_BUY = "L_BUY";
    private static final String L_SELL = "L_SELL";
    private static final String START = "/start";
    private static final String CANSEL = "Отменить";
    private static final String CHOSE_SYMBOL = "Выбери инструмент";
    private static final Set<String> OPERATION = Set.of(L_BUY, L_SELL);
    private static final String ERROR_TEXT = "";
    private static final String appUrl = "http://localhost:3000";

    @Value("${list.symbol}")
    private Set<String> symbols;

    private final UserRepository userRepository;
    private final BotConfig config;
    private final BybitOrderService bybitOrderService;
    private final ObjectMapper objectMapper;
    private final BybitTradeFetcher bybitTradeFetcher;

    public TelegramBot(BotConfig config, ObjectMapper objectMapper, BybitTradeFetcher bybitTradeFetcher,
                       UserRepository userRepository, BybitOrderService bybitOrderService, Set<String> symbols) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.bybitTradeFetcher = bybitTradeFetcher;
        this.userRepository = userRepository;
        this.bybitOrderService = bybitOrderService;
        this.symbols = symbols;

        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Добро пожаловать!"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

//            https://api.bybit.com/spot/v3/public/quote/trades?symbol=BTCUSDT&limit=500

            long chatId = update.getMessage().getChatId();
            String s = bybitTradeFetcher.fetchTrades();
            if (nonNull(s)) {
                prepareAndSendMessage(chatId, s);
                return;
            }

            String messageText = update.getMessage().getText();
            sendStartMessage(chatId);

            switch (messageText) {
                case START:
                    registerUser(update.getMessage());
                    break;
            }
        }
    }

    private void registerUser(Message msg) {

        var chatId = msg.getChatId();
        var chat = msg.getChat();
        Optional<TelegramUser> byId = userRepository.findById(msg.getChatId());
        if (byId.isEmpty()) {


            TelegramUser telegramUser = new TelegramUser();

            telegramUser.setChatId(chatId);
            telegramUser.setFirstName(chat.getFirstName());
            telegramUser.setLastName(chat.getLastName());
            telegramUser.setUserName(chat.getUserName());
            telegramUser.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(telegramUser);
        }
        startCommandReceived(chatId, chat.getFirstName());
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :blush:");
        log.info("Replied to user " + name);

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add(L_BUY);
        row.add(L_SELL);

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void sendButtonStartWork(long chatId, String textToSend, Set<String> buttons) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        buttons.forEach(row::add);
//        if (END_LESSON.equals(button)) {
//            row.add(button);
//        } else {
//            row.add(QR_GENERATE);
//            row.add(ADD_NEW_STUDENT);

//            row.add(button);
//        }

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    private ReplyKeyboardMarkup createButtonList(List<String> listButton) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(listButton.get(0));
        row.add(listButton.get(1));
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add(listButton.get(2));
        row.add(listButton.get(3));
        keyboardRows.add(row);
        if (listButton.size() > 4) {
            row = new KeyboardRow();
            row.add(listButton.get(4));
            keyboardRows.add(row);
        }
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    @SneakyThrows
    public void handler(String s) {
        WebsocketOrderbookMessage bookMessage = objectMapper.readValue(s, WebsocketOrderbookMessage.class);

        if (nonNull(bookMessage) && nonNull(bookMessage.getData())) {

            PublicOrderBookData data = bookMessage.getData();
            if (data.getS().equals("WLDUSDT")) {
                LinkedList<Pair<Double, Double>> bid = extracted(data.getB());
                LinkedList<Pair<Double, Double>> ask = extracted(data.getA());


                if (!bid.isEmpty() && bid.getLast().getRight() > 7000) {
                    System.out.println("bid " + bid.getLast());
                    this.prepareAndSendMessage(1466178855, "price   " + bid.getLast().getLeft() + "   bid  " + bid.getLast().getRight());
//                    this.prepareAndSendMessage(1466178855, s);
                }
                if (!ask.isEmpty() && ask.getLast().getRight() > 7000) {
                    System.out.println("ask " + ask.getLast());
                    this.prepareAndSendMessage(1466178855, "price   " + ask.getLast().getLeft() + "   ask " + ask.getLast().getRight());
//                    this.prepareAndSendMessage(1466178855, s);
                }
            }
        }
    }

    private LinkedList<Pair<Double, Double>> extracted(List<List<String>> data) {
        LinkedList<Pair<Double, Double>> ll = new LinkedList<>();
        data.forEach(el -> {
            double price = Double.parseDouble(el.get(0));
            double count = Double.parseDouble(el.get(1));
            if (ll.isEmpty()) {
                ll.addFirst(Pair.of(price, count));
                return;
            }

            if (ll.getLast().getRight() > count) {
                ll.addFirst(Pair.of(price, count));
            } else {
                ll.addLast(Pair.of(price, count));
            }
        });
        return ll;
    }

    private void sendStartMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        // Создаем InlineKeyboardMarkup с одной кнопкой
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Перейти на сайт");
        button.setUrl("t.me/GoodTraderAppBot/GoodTraderAppBot"); // URL вашего сайта
        row.add(button);
        rows.add(row);
        markup.setKeyboard(rows);

        // Устанавливаем клавиатуру для сообщения
        sendMessage.setReplyMarkup(markup);
        sendMessage.setText("Привет! Вот ссылка на мое веб-приложение:");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
