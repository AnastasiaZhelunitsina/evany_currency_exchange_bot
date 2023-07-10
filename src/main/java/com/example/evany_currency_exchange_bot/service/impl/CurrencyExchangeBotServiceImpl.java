package com.example.evany_currency_exchange_bot.service.impl;

import com.example.evany_currency_exchange_bot.client.CbrClient;
import com.example.evany_currency_exchange_bot.exception.ServiceException;
import com.example.evany_currency_exchange_bot.service.CurrencyExchangeBotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

@Service
public class CurrencyExchangeBotServiceImpl implements CurrencyExchangeBotService {

    private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";
    private static final String KZT_XPATH = "/ValCurs//Valute[@ID='R01335']/Value";

    @Autowired
    private CbrClient client;


    @Override
    public String getUSDExchange() throws ServiceException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyXML(xml, USD_XPATH);
    }

    @Override
    public String getEURExchange() throws ServiceException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyXML(xml, EUR_XPATH);
    }

    @Override
    public String getKZTExchange() throws ServiceException {
        var xml = client.getCurrencyRatesXML();
        return extractCurrencyXML(xml, KZT_XPATH);
    }
    private static String extractCurrencyXML(String xml, String xPathExpression) throws ServiceException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);

            return xpath.evaluate(xPathExpression, document);
        } catch (XPathExpressionException e) {
            throw new ServiceException("Проблема парсинга XML", e);
        }
    }
}
