package com.example.labjava.service;

import org.json.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRate {

    private final RestTemplate restTemplate;

    public ExchangeRate() {
        this.restTemplate = new RestTemplate();
    }

    public String getExchangeRates() {
        String url = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
        String jsonResponse = restTemplate.getForObject(url, String.class);

        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return "Не вдалося отримати курси валют.";
        }

        return formatExchangeRates(jsonResponse);
    }

    private String formatExchangeRates(String jsonString) {
        JSONArray currencyArray = new JSONArray(jsonString);
        StringBuilder formattedRates = new StringBuilder("💰 **Курси валют:**\n");

        for (int i = 0; i < currencyArray.length(); i++) {
            JSONObject currency = currencyArray.getJSONObject(i);
            String ccy = currency.getString("ccy");
            double buy = currency.getDouble("buy");
            double sale = currency.getDouble("sale");

            formattedRates.append(String.format("\n%s: Купівля **%.2f₴** | Продаж **%.2f₴**", ccy, buy, sale));
        }

        return formattedRates.toString();
    }
}