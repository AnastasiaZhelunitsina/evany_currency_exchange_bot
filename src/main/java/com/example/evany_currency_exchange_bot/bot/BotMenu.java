package com.example.evany_currency_exchange_bot.bot;

import com.example.evany_currency_exchange_bot.exception.ServiceException;
import com.example.evany_currency_exchange_bot.service.CurrencyExchangeBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class BotMenu extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(BotMenu.class);

    private static final String START = "/start";
    private static final String EXCHANGE_RATES = "/rates";
    private static final String CURRENCY_CONVERTER = "/convert";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String KZT = "/kzt";
    private static final String USD_TO_RUB = "/usd_to_rub";

    private static final String EUR_TO_RUB = "/eur_to_rub";

    private static final String KZT_TO_RUB = "/kzt_to_rub";

    private static final String HELP = "/help";

    @Autowired
    private CurrencyExchangeBotService currencyExchangeBotService;

    public BotMenu(@Value("${bot.token}") String token) {
        super(token);
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String[] messageText = update.getMessage().getText().trim().split(" ");
            long chatId = update.getMessage().getChatId();
            String command = messageText[0];
            String value = "";
            if (messageText.length == 2) {
                value = messageText[1];
            }

            switch (command) {
                case START -> {
                    String userName = update.getMessage().getChat().getUserName();
                    startCommand(chatId, userName);
                }
                case EXCHANGE_RATES -> {
                    exchangeCommand(chatId);
                }
                case CURRENCY_CONVERTER -> {
                    converterCommand(chatId);
                }

                case USD -> usdCommand(chatId);
                case EUR -> eurCommand(chatId);
                case KZT -> kztCommand(chatId);
                case HELP -> helpCommand(chatId);

                case USD_TO_RUB -> fromUsdToRubCommand(chatId, value);
                case EUR_TO_RUB -> fromEurToRubCommand(chatId, value);
                case KZT_TO_RUB -> fromKztToRubCommand(chatId, value);

                default -> unknownCommand(chatId);
            }

        }
    }

    public String getBotUsername() {
        return "evany_currency_exchange_bot";
    }

    private void startCommand(long chatId, String userName) {
        String answer = """
                Приветствую в боте, %s! 
                                
                Узнать официальные курсы валют: /rates 
                Конвертер валют: /convert
                                
                Дополнительная информация - /help
                """;
        String formatted = String.format(answer, userName);
        sendMessage(chatId, formatted);
    }

    private void exchangeCommand(long chatId) {
        String answer = """
                Узнать официальные курсы валют, установленные Центральным банком России по состоянию на текущую дату:
                                
                Доллар США: /usd
                Евро: /eur
                Казахстанский тенге: /kzt
                                
                Дополнительная информация: /help
                """;
        String formatted = String.format(answer);
        sendMessage(chatId, formatted);
    }

    private void converterCommand(long chatId) {
        String answer = """
                Конвертация:
                *введите команду из списка и через пробел необходмую сумму для конвертации*
                например: /usd_to_rub 2
                
                Доллары США в российские рубли: /usd_to_rub
                                
                Евро в российские рубли: /eur_to_rub
                                
                Казахстанские тенге в российские рубли: /kzt_to_rub
                                          
                Дополнительная информация: /help
                """;
        String formatted = String.format(answer);
        sendMessage(chatId, formatted);
    }

    private String date(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return dtf.format(LocalDate.now());
    }

    private void usdCommand(long chatID) {
        StringBuilder formatted = new StringBuilder();
        try {
            var usd = currencyExchangeBotService.getUSDExchange();

            var text = "Курс доллара США по данным ЦБ РФ на %s составляет: %s₽";
            formatted.append(String.format(text, date(), usd));
        } catch (ServiceException e) {
            LOG.error("Проблема с получением курса доллара", e);
        }
        sendMessage(chatID, String.valueOf(formatted));
    }

    private void eurCommand(long chatID) {
        StringBuilder formatted = new StringBuilder();
        try {
            var eur = currencyExchangeBotService.getEURExchange();

            var text = "Курс евро по данным ЦБ РФ на %s составляет: %s₽";
            formatted.append(String.format(text, date(), eur));
        } catch (ServiceException e) {
            LOG.error("Проблема с получением курса евро", e);
        }
        sendMessage(chatID, String.valueOf(formatted));
    }

    private void kztCommand(long chatID) {
        StringBuilder formatted = new StringBuilder();
        try {
            var kzt = currencyExchangeBotService.getKZTExchange();

            var text = "Курс казахстанского тенге по данным ЦБ РФ на %s составляет: %s₽";
            formatted.append(String.format(text, date(), kzt));
        } catch (ServiceException e) {
            LOG.error("Проблема с получением курса казахстанского тенге", e);
        }
        sendMessage(chatID, String.valueOf(formatted));
    }

    private BigDecimal convert(String currency, String amount) {
        BigDecimal current = new BigDecimal(currency);
        BigDecimal input = new BigDecimal(amount);
        return current.multiply(input).setScale(2, RoundingMode.CEILING);
    }

    private void fromUsdToRubCommand(long chatID, String value) {

        StringBuilder formatted = new StringBuilder();
        try {
            var usd = currencyExchangeBotService.getUSDExchange().replace(',', '.');
            BigDecimal answer = convert(usd, value);
            var text = "%s$ по данным ЦБ РФ на %s составляет: %s₽";
            formatted.append(String.format(text, value, date(), answer));
        } catch (ServiceException e) {
            LOG.error("Проблема с получением курса доллара", e);
        }
        sendMessage(chatID, String.valueOf(formatted));
    }

    private void fromEurToRubCommand(long chatID, String value) {

        StringBuilder formatted = new StringBuilder();
        try {
            var eur = currencyExchangeBotService.getEURExchange().replace(',', '.');
            BigDecimal answer = convert(eur, value);
            var text = "%s€ по данным ЦБ РФ на %s составляет: %s₽";
            formatted.append(String.format(text, value, date(), answer));
        } catch (ServiceException e) {
            LOG.error("Проблема с получением курса евро", e);
        }
        sendMessage(chatID, String.valueOf(formatted));
    }

    private void fromKztToRubCommand(long chatID, String value) {

        StringBuilder formatted = new StringBuilder();
        try {
            var kzt = currencyExchangeBotService.getKZTExchange().replace(',', '.');
            BigDecimal answer = convert(kzt, value);
            var text = "%s₸ по данным ЦБ РФ на %s составляет: %s₽";
            formatted.append(String.format(text, value, date(), answer));
        } catch (ServiceException e) {
            LOG.error("Проблема с получением курса казахстанского тенге", e);
        }
        sendMessage(chatID, String.valueOf(formatted));
    }

    private void helpCommand(Long chatId) {
        var text = """
                Справочная информация по боту
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        var text = "Не удалось распознать команду";
        sendMessage(chatId, text);
    }

    private void sendMessage(long chatID, String textToSend) {
        var chatIdToStr = String.valueOf(chatID);
        var sendMessage = new SendMessage(chatIdToStr, textToSend);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Проблема с отправкой сообщения", e);
        }
    }
}
