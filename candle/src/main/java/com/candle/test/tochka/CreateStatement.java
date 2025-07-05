package com.candle.test.tochka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

import static com.candle.test.Utils.getResponse;
import static com.candle.test.Utils.postResponse;

@Service
@RequiredArgsConstructor
public class CreateStatement {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public String createStatementAndGetStatementId(String startDate, String endDate, String accountId, String token) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestCreateStatementDto requestCreateStatementDto = createRequest(startDate, endDate, accountId);

        RequestBody body = RequestBody.create(mediaType, objectMapper.writeValueAsString(requestCreateStatementDto));
//        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"Data\": {\r\n        \"Statement\": {\r\n            \"accountId\": \"40802810020000640637/044525104\",\r\n            \"startDateTime\": \"2025-05-21\",\r\n            \"endDateTime\": \"2025-05-21\"\r\n        }\r\n    }\r\n}");
        Request request = new Request.Builder()
                .url("https://enter.tochka.com/uapi/open-banking/v1.0/statements")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Mozilla/5.0")  // Обязательный заголовок!
                .addHeader("Authorization", token)
                .build();

        String response = postResponse(request, 3);

        return objectMapper.readValue(response, RequestCreateStatementDto.class).getData().getStatement().getStatementId();
    }

    public ResponseStatementDto getStatement(String accountId, String token, String statementId) throws IOException {
        Request request = new Request.Builder()
                .url("https://enter.tochka.com/uapi/open-banking/v1.0/accounts/" + accountId + "/statements/" + statementId)
                .addHeader("Authorization", token)
                .build();
        String response = getResponse(request, 3);
        System.out.println(response);
        return objectMapper.readValue(response, ResponseStatementDto.class);
    }

    private RequestCreateStatementDto createRequest(String startDate, String endDate, String accountId) {
        return RequestCreateStatementDto.builder().data(RequestCreateStatementDto.Data.builder().statement(RequestCreateStatementDto.Statement.builder()
                .startDateTime(startDate).endDateTime(endDate).accountId(accountId).build()).build()).build();
    }
}
