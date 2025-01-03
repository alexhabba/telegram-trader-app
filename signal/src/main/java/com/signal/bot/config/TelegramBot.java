package com.signal.bot.config;

import com.dao.bot.service.UserService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final String ERROR_TEXT = "Error: ";

    private Map<String, Consumer<Message>> MAP_COMMAND_ACTION;

    private final BotConfig config;
    private final UserService userService;

    @PostConstruct
    public void init() {
        MAP_COMMAND_ACTION =
                Map.of("/start", this::registerUser);

    }


    public TelegramBot(BotConfig config, UserService userService) {
        this.config = config;
        this.userService = userService;
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

        if (update.hasChannelPost() && update.getChannelPost().hasText()) {
            long chatId = update.getChannelPost().getChatId();
            System.out.println("chatId:  " + chatId);
            // chatId = -1002294735226
//            registerUser(update.getChannelPost());
            prepareAndSendMessage(chatId, "С этого аккаунта запрещено добавлять учеников и создавать QR");
            return;
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            Consumer<Message> messageConsumer = MAP_COMMAND_ACTION.get(update.getMessage().getText());
            if (nonNull(messageConsumer)) {
                messageConsumer.accept(update.getMessage());
            }
        }
    }

    public void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void registerUser(Message message) {
        if (!userService.isUserExist(message)) {
            userService.registerUser(message);
        }
        sendMessageWhenStart(message);
    }

    private void sendMessageWhenStart(Message message) {
        String answer = EmojiParser.parseToUnicode("Привет, " + message.getChat().getFirstName() + " :blush:");
        answer += "\nСигналы будут публиковаться в этом канале https://t.me/+m8lKeczx_-45ODYy";
        prepareAndSendMessage(message.getChatId(), answer);
    }

}
