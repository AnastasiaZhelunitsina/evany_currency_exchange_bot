package com.example.evany_currency_exchange_bot.service;

import com.example.evany_currency_exchange_bot.exception.ServiceException;

public interface CurrencyExchangeBotService {



    String getUSDExchange() throws ServiceException;

    String getEURExchange() throws ServiceException;

    String getKZTExchange() throws ServiceException;
}
