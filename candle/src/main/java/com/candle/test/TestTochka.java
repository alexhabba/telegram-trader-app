package com.candle.test;

import com.candle.test.tochka.CreateStatement;
import com.candle.test.tochka.RequestCreateStatementDto;
import com.candle.test.tochka.ResponseStatementDto;
import lombok.SneakyThrows;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDate;

public class TestTochka {

    @SneakyThrows
    public static void main(String[] args) throws IOException {
        String accountId =  "40802810020000640637/044525104";
        String token =  "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvbjJhdzBwOWE3RnlUbFBXaGtMbmJIelJZcTJ5dURobSJ9.ojsvdwVeqpRuR2wmIDP_myS7gbkq6xi1jK8t3NgXUMZo35tJtli-Hpn142QVLwJEbzKyDpMcMDaXsSbdwr690pLmHhn7TH5TvQizhFFTha6XQmpoivOZxUWzepbsc-3ggLy2UkiRWNSG_UgiWNdItv0A8oCh8hIjPDWo4_qNEFl8WR5is1mJ-rdACdIH7TERW_-udud3oBoOSTsVXiRbvqDzi7WJloo0CZMZ_9i-foSHtkf2EAbyrFB-liDCuFb56UgzFEuxfwvc2DFCTgp6QKIL_d-hatM8P8r79TWiZV5zEMgsz6NqShJNqwe6sZjegErw7aKd4_VxmtwNkHSlhmlp3jtS60TFQwSwDdIeFuVY7uOAxVZ45IijFMIdMyGsyad-LozcZBbrijON7Lz7MsLESpRBMA4ZtOG_DlP4-sdLh_rCvzAX8Y2uwUTwIdHlWna6u-rBVXSswFlfbbqs1Rs8n1ja4izYiD_xx-pArJRxqLcJbreZG6UnEhB2o6Ga";
        CreateStatement createStatement = new CreateStatement();
        String statementId = createStatement.createStatementAndGetStatementId(LocalDate.parse("2025-06-02").toString(), LocalDate.parse("2025-06-02").toString(), accountId, token);
//        create();
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("text/plain");
//        Request request = new Request.Builder()
//                .url("https://enter.tochka.com/uapi/open-banking/v1.0/statements")
//                .addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvbjJhdzBwOWE3RnlUbFBXaGtMbmJIelJZcTJ5dURobSJ9.ojsvdwVeqpRuR2wmIDP_myS7gbkq6xi1jK8t3NgXUMZo35tJtli-Hpn142QVLwJEbzKyDpMcMDaXsSbdwr690pLmHhn7TH5TvQizhFFTha6XQmpoivOZxUWzepbsc-3ggLy2UkiRWNSG_UgiWNdItv0A8oCh8hIjPDWo4_qNEFl8WR5is1mJ-rdACdIH7TERW_-udud3oBoOSTsVXiRbvqDzi7WJloo0CZMZ_9i-foSHtkf2EAbyrFB-liDCuFb56UgzFEuxfwvc2DFCTgp6QKIL_d-hatM8P8r79TWiZV5zEMgsz6NqShJNqwe6sZjegErw7aKd4_VxmtwNkHSlhmlp3jtS60TFQwSwDdIeFuVY7uOAxVZ45IijFMIdMyGsyad-LozcZBbrijON7Lz7MsLESpRBMA4ZtOG_DlP4-sdLh_rCvzAX8Y2uwUTwIdHlWna6u-rBVXSswFlfbbqs1Rs8n1ja4izYiD_xx-pArJRxqLcJbreZG6UnEhB2o6Ga")
//                .build();
//        Response response = client.newCall(request).execute();
//        System.out.println(response.body().string());

        Thread.sleep(10000);
        ResponseStatementDto statement = createStatement.getStatement(accountId, token, statementId);

        System.out.println();


//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"Data\": {\r\n        \"Statement\": {\r\n            \"accountId\": \"40802810020000640637/044525104\",\r\n            \"startDateTime\": \"2025-05-10\",\r\n            \"endDateTime\": \"2025-05-20\"\r\n        }\r\n    }\r\n}");
//        Request request = new Request.Builder()
//                .url("https://enter.tochka.com/uapi/open-banking/v1.0/statements")
//                .method("POST", body)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvbjJhdzBwOWE3RnlUbFBXaGtMbmJIelJZcTJ5dURobSJ9.ojsvdwVeqpRuR2wmIDP_myS7gbkq6xi1jK8t3NgXUMZo35tJtli-Hpn142QVLwJEbzKyDpMcMDaXsSbdwr690pLmHhn7TH5TvQizhFFTha6XQmpoivOZxUWzepbsc-3ggLy2UkiRWNSG_UgiWNdItv0A8oCh8hIjPDWo4_qNEFl8WR5is1mJ-rdACdIH7TERW_-udud3oBoOSTsVXiRbvqDzi7WJloo0CZMZ_9i-foSHtkf2EAbyrFB-liDCuFb56UgzFEuxfwvc2DFCTgp6QKIL_d-hatM8P8r79TWiZV5zEMgsz6NqShJNqwe6sZjegErw7aKd4_VxmtwNkHSlhmlp3jtS60TFQwSwDdIeFuVY7uOAxVZ45IijFMIdMyGsyad-LozcZBbrijON7Lz7MsLESpRBMA4ZtOG_DlP4-sdLh_rCvzAX8Y2uwUTwIdHlWna6u-rBVXSswFlfbbqs1Rs8n1ja4izYiD_xx-pArJRxqLcJbreZG6UnEhB2o6Ga")
//                .build();
//        Response response = client.newCall(request).execute();
//        System.out.println(response.body().string());
    }

    public static void create() throws IOException {
                OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"Data\": {\r\n        \"Statement\": {\r\n            \"accountId\": \"40802810020000640637/044525104\",\r\n            \"startDateTime\": \"2025-05-21\",\r\n            \"endDateTime\": \"2025-05-21\"\r\n        }\r\n    }\r\n}");
        Request request = new Request.Builder()
                .url("https://enter.tochka.com/uapi/open-banking/v1.0/statements")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvbjJhdzBwOWE3RnlUbFBXaGtMbmJIelJZcTJ5dURobSJ9.ojsvdwVeqpRuR2wmIDP_myS7gbkq6xi1jK8t3NgXUMZo35tJtli-Hpn142QVLwJEbzKyDpMcMDaXsSbdwr690pLmHhn7TH5TvQizhFFTha6XQmpoivOZxUWzepbsc-3ggLy2UkiRWNSG_UgiWNdItv0A8oCh8hIjPDWo4_qNEFl8WR5is1mJ-rdACdIH7TERW_-udud3oBoOSTsVXiRbvqDzi7WJloo0CZMZ_9i-foSHtkf2EAbyrFB-liDCuFb56UgzFEuxfwvc2DFCTgp6QKIL_d-hatM8P8r79TWiZV5zEMgsz6NqShJNqwe6sZjegErw7aKd4_VxmtwNkHSlhmlp3jtS60TFQwSwDdIeFuVY7uOAxVZ45IijFMIdMyGsyad-LozcZBbrijON7Lz7MsLESpRBMA4ZtOG_DlP4-sdLh_rCvzAX8Y2uwUTwIdHlWna6u-rBVXSswFlfbbqs1Rs8n1ja4izYiD_xx-pArJRxqLcJbreZG6UnEhB2o6Ga")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

//    public static void getStatement() throws IOException {
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("text/plain");
//        Request request = new Request.Builder()
//                .url("https://enter.tochka.com/uapi/open-banking/v1.0/accounts/40802810020000640637/044525104/statements/e1437392-2207-4ebc-afaf-0cbdbdbc89b1")
//                .addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvbjJhdzBwOWE3RnlUbFBXaGtMbmJIelJZcTJ5dURobSJ9.ojsvdwVeqpRuR2wmIDP_myS7gbkq6xi1jK8t3NgXUMZo35tJtli-Hpn142QVLwJEbzKyDpMcMDaXsSbdwr690pLmHhn7TH5TvQizhFFTha6XQmpoivOZxUWzepbsc-3ggLy2UkiRWNSG_UgiWNdItv0A8oCh8hIjPDWo4_qNEFl8WR5is1mJ-rdACdIH7TERW_-udud3oBoOSTsVXiRbvqDzi7WJloo0CZMZ_9i-foSHtkf2EAbyrFB-liDCuFb56UgzFEuxfwvc2DFCTgp6QKIL_d-hatM8P8r79TWiZV5zEMgsz6NqShJNqwe6sZjegErw7aKd4_VxmtwNkHSlhmlp3jtS60TFQwSwDdIeFuVY7uOAxVZ45IijFMIdMyGsyad-LozcZBbrijON7Lz7MsLESpRBMA4ZtOG_DlP4-sdLh_rCvzAX8Y2uwUTwIdHlWna6u-rBVXSswFlfbbqs1Rs8n1ja4izYiD_xx-pArJRxqLcJbreZG6UnEhB2o6Ga")
//                .build();
//        Response response = client.newCall(request).execute();
//        System.out.println(response.body().string());
//    }

//    cначала нужно сформировать выписку а уже потом ее получить
}
