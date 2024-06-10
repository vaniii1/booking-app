package vanii.bookingapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import vanii.bookingapp.client.MyTelegramBot;

@Configuration
public class TelegramBotConfig {
    @Bean
    public BotSession botSession(TelegramBotsApi telegramBotsApi, MyTelegramBot myTelegramBot)
            throws TelegramApiException {
        return telegramBotsApi.registerBot(myTelegramBot);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @Bean
    public MyTelegramBot myTelegramBot() {
        return new MyTelegramBot();
    }
}
