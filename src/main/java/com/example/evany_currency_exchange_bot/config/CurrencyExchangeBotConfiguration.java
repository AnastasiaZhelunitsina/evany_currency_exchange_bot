package com.example.evany_currency_exchange_bot.config;

import com.example.evany_currency_exchange_bot.bot.BotMenu;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Configuration
public class CurrencyExchangeBotConfiguration extends BotConfig{
    @Bean
    public TelegramBotsApi telegramBotsApi(BotMenu botMenu) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(botMenu);
        return api;
    }
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }
}

