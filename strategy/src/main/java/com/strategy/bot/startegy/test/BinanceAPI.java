package com.strategy.bot.startegy.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.json.JSONArray;
import org.json.JSONObject;

public class BinanceAPI {

    private static final String API_KEY = "YOUR_API_KEY"; // Замените на свой ключ
    private static final String API_SECRET = "YOUR_API_SECRET"; // Замените на свой секрет

    public static void main(String[] args) throws IOException {
        String symbol = "WLDUSDT";
        String interval = "1m"; // 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1M
        long startTime = LocalDateTime.parse("2025-01-06T20:00:00").toInstant(ZoneOffset.UTC).toEpochMilli();
        long endTime = LocalDateTime.parse("2025-01-06T20:00:00").toInstant(ZoneOffset.UTC).toEpochMilli();
        int limit = 5;


        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&startTime=%d&endTime=%d&limit=%d",
//        String url = String.format("https://api.binance.com/api/v3/uiKlines?symbol=%s&interval=%s&limit=%d",
                URLEncoder.encode(symbol, StandardCharsets.UTF_8),
                interval, startTime, endTime, limit);

        String response = getResponse(url);

        if (response != null){
            try{
                JSONArray jsonArray = new JSONArray(response);

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONArray candle = jsonArray.getJSONArray(i);
                    System.out.println(candle);
                }

            }catch (Exception e){
                System.out.println("Error parsing response: "+e.getMessage());
            }
        } else {
            System.out.println("No response from server");
        }
    }


    private static String getResponse(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            System.err.println("GET request failed with code " + responseCode);
            return null;
        }
    }
}

