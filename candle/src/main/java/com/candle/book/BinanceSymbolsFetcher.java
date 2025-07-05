package com.candle.book;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * Получение информации по всем символам USDT spot.
 */
public class BinanceSymbolsFetcher {

    public List<String> getAllSymbol(String quoteAsset) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
//                .url("https://api.binance.com/api/v3/exchangeInfo?symbol=SOPHUSDT")
                .url("https://api.binance.com/api/v3/exchangeInfo?permissions=SPOT")
                .header("User-Agent", "Mozilla/5.0")  // Обязательный заголовок!
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String json = response.body().string();
                JSONObject jsonObject = new JSONObject(json);
                JSONArray symbols = jsonObject.getJSONArray("symbols");

                List<String> currencies = new ArrayList<>();

                for (int i = 0; i < symbols.length(); i++) {
                    JSONObject symbol = symbols.getJSONObject(i);
                    if (symbol.getString("quoteAsset").equals(quoteAsset) && symbol.getString("status").equals("TRADING")) {
                        currencies.add(symbol.getString("symbol"));
                    }
                }

                return currencies;
            } else {
                System.err.println("Request failed: " + response.code() + " " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
}
