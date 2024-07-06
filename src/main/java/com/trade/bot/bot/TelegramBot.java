package com.trade.bot.bot;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.websocket_message.public_channel.PublicOrderBookData;
import com.bybit.api.client.domain.websocket_message.public_channel.WebsocketOrderbookMessage;
import com.bybit.api.client.restApi.BybitApiCallback;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.config.BotConfig;
import com.trade.bot.entity.TelegramUser;
import com.trade.bot.enums.Role;
import com.trade.bot.repository.UserRepository;
import com.trade.bot.service.OrderService;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.trade.bot.enums.Role.ADMIN;
import static com.trade.bot.enums.Role.ADMIN_DUBNA;
import static com.trade.bot.enums.Role.ADMIN_MOSKOW;
import static com.trade.bot.enums.Role.ADMIN_RAMENSKOE;
import static com.trade.bot.enums.Role.ADMIN_TEST;
import static com.trade.bot.enums.Role.ADMIN_VOSKRESENSK;
import static com.trade.bot.enums.Role.SUPER_ADMIN;
import static com.trade.bot.enums.Role.TEACHER_DUBNA;
import static com.trade.bot.enums.Role.TEACHER_MOSKOW;
import static com.trade.bot.enums.Role.TEACHER_RAMENSKOE;
import static com.trade.bot.enums.Role.TEACHER_TEST;
import static com.trade.bot.enums.Role.TEACHER_VOSKRESENSK;
import static java.util.Collections.EMPTY_SET;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final String L_BUY = "L_BUY";
    private static final String L_SELL = "L_SELL";
    private static final String START = "/start";
    private static final String CANSEL = "Отменить";
    private static final String CHOSE_SYMBOL = "Выбери инструмент";

    @Value("${list.symbol}")
    private Set<String> symbols;

    private final UserRepository userRepository;
    private final BotConfig config;
    private final OrderService orderService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final List<Role> ADMIN_STATISTIC_DAYS = List.of(
            ADMIN_DUBNA,
            ADMIN_MOSKOW,
            ADMIN_VOSKRESENSK,
            ADMIN_RAMENSKOE,
            ADMIN_TEST
    );

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";

    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config,
                       UserRepository userRepository, OrderService orderService,  Set<String> symbols) {
        this.config = config;
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.symbols = symbols;

        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Добро пожаловать!"));
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);
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
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            Optional<TelegramUser> byId = userRepository.findById(chatId);
            if (symbols.contains(messageText)) {
                sendButtonStartWork(chatId, "введите цену лимитной заявки", Set.of(CANSEL));
                prepareAndSendMessage(chatId, "или можете отменить ее, нажав кномку ниже");

                return;
            }
//            try {
//                orderService.openOrder("NOT", BigDecimal.valueOf(Double.parseDouble(messageText)), x -> prepareAndSendMessage(chatId, x.toString()));
//                return;
//            } catch (Exception ex) {
//                log.error("не число");
//            }
            switch (messageText) {
                case START:
                    registerUser(update.getMessage());
                    break;
                case L_BUY:
                    prepareAndSendMessage(chatId, "пока не реализована логика " + L_BUY);
                    sendButtonStartWork(chatId, CHOSE_SYMBOL, symbols);
                    break;
                case L_SELL:
                    prepareAndSendMessage(chatId, "пока не реализована логика " + L_SELL);
                    sendButtonStartWork(chatId, CHOSE_SYMBOL, symbols);
                    break;
            }
        }
    }

    private void register(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Do you really want to register?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();

        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
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
}
