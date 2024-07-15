package com.trade.bot.websocket;

import com.bybit.api.client.exception.BybitApiException;
import com.bybit.api.client.websocket.callback.WebSocketClosedCallback;
import com.bybit.api.client.websocket.callback.WebSocketClosingCallback;
import com.bybit.api.client.websocket.callback.WebSocketFailureCallback;
import com.bybit.api.client.websocket.callback.WebSocketMessageCallback;
import com.bybit.api.client.websocket.callback.WebSocketOpenCallback;
import lombok.Getter;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketConnection extends WebSocketListener {
    private static final AtomicInteger connectionCounter = new AtomicInteger(0);
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConnection.class);
    private static OkHttpClient client;
    private static boolean sessionStatus;

    @Getter
    private final int connectionId;
    private final Object mutex;
    private final Request request;
    private final String streamName;

    private final WebSocketOpenCallback onOpenCallback;
    private final WebSocketMessageCallback onMessageCallback;
    private final WebSocketClosingCallback onClosingCallback;
    private final WebSocketClosedCallback onClosedCallback;
    private final WebSocketFailureCallback onFailureCallback;

    private WebSocket webSocket;

    public WebSocketConnection(
            WebSocketOpenCallback onOpenCallback,
            WebSocketMessageCallback onMessageCallback,
            WebSocketClosingCallback onClosingCallback,
            WebSocketClosedCallback onClosedCallback,
            WebSocketFailureCallback onFailureCallback,
            String serverConnect,
            OkHttpClient client
    ) {
        this.onOpenCallback = onOpenCallback;
        this.onMessageCallback = onMessageCallback;
        this.onClosingCallback = onClosingCallback;
        this.onClosedCallback = onClosedCallback;
        this.onFailureCallback = onFailureCallback;
        this.connectionId = WebSocketConnection.connectionCounter.incrementAndGet();
        this.request = new Request.Builder().url(serverConnect).build();;
        this.streamName = request.url().host() + request.url().encodedPath();
        this.webSocket = client.newWebSocket(request, this);
        this.mutex = new Object();
        WebSocketConnection.client = client;
    }

    public boolean getSessionStatus() {
        return sessionStatus;
    }

    public void send(String message) {
        if (null == webSocket) {
            throw new BybitApiException("No WebSocket connection. Please connect first!");
        }
        webSocket.send(message);
    }

    public void close() {
        if (null != webSocket) {
            logger.info("[Connection {}] Closing connection to {}", connectionId, streamName);
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
        }
    }

    @Override
    public void onOpen(@NotNull WebSocket ws, @NotNull Response response) {
        logger.info("[Connection {}] Connected to Server", connectionId);
        onOpenCallback.onOpen(response);
    }

    @Override
    public void onClosing(@NotNull WebSocket ws, int code, @NotNull String reason) {
        super.onClosing(ws, code, reason);
        onClosingCallback.onClosing(code, reason);
    }

    @Override
    public void onClosed(@NotNull WebSocket ws, int code, @NotNull String reason) {
        super.onClosed(ws, code, reason);
        onClosedCallback.onClosed(code, reason);
    }

    @SneakyThrows
    @Override
    public void onMessage(@NotNull WebSocket ws, String text) {

        // session status
        if (text.contains("authorizedSince")) {
            JSONObject result =  new JSONObject(text).getJSONObject("result");
            WebSocketConnection.sessionStatus = !result.isNull("authorizedSince");
        }
        onMessageCallback.onMessage(text);
    }

    @Override
    public void onFailure(@NotNull WebSocket ws, @NotNull Throwable t, Response response) {
        logger.error("[Connection {}] Failure", connectionId, t);
        onFailureCallback.onFailure(t, response);
    }
}
