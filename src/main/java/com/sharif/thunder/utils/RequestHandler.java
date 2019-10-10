package com.sharif.thunder.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private String content;
    private static final String USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64; rv:63.0; ThunderBot) Gecko/20100101 Firefox/63.0";
    private static OkHttpClient client = new OkHttpClient();
    private static final int MAX_LENGTH = 64 * 1024;

    private static final String BAD_REQUEST_JSON = "{\"code\": 400, \"message\": \"Bad Request\"}";
    private static final String UNAUTHORIZED_JSON =
            "{\"code\": 401, \"message\": \"Unauthorized\"}";
    private static final String NOT_FOUND_JSON = "{\"code\": 404, \"message\": \"Not Found\"}";
    private static final String METHOD_NOT_ALLOWED_JSON =
            "{\"code\": 405, \"message\": \"Method Not Allowed\"}";
    private static final String PAYLOAD_TOO_LARGE_JSON =
            "{\"code\": 413, \"message\": \"PayloadTooLarge\"}";
    private static final String SERVER_ERROR_JSON =
            "{\"code\": 500, \"message\": \"Internal Server Error\"}";

    public RequestHandler(String url) {
        Request request = new Request.Builder().header("User-Agent", USER_AGENT).url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                this.content = response.body().string();
            }
        } catch (Exception ex) {
            log.error(
                    "An error occurred while running the {} class, message: {}",
                    this,
                    ex.getMessage(),
                    ex);
        }
    }

    public static byte[] download(String url, String authorization) throws IOException {
        Request request =
                new Request.Builder()
                        .header("User-Agent", USER_AGENT)
                        .header("Authorization", authorization)
                        .url(url)
                        .build();
        Response response = client.newCall(request).execute();
        return response.body() == null ? new byte[0] : response.body().bytes();
    }

    @NotNull
    public static CompletableFuture<JsonElement> getJsonOrFail(
            String method, final HttpServerExchange exchange) {
        if (!exchange.getRequestMethod().equalToString(method)) {
            exchange.setStatusCode(405).getResponseSender().send(METHOD_NOT_ALLOWED_JSON);
            return CompletableFuture.completedFuture(null);
        }

        if (exchange.getRequestContentLength() > MAX_LENGTH) {
            exchange.setStatusCode(413).getResponseSender().send(PAYLOAD_TOO_LARGE_JSON);
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<JsonElement> future = new CompletableFuture<>();

        exchange.getRequestReceiver()
                .receiveFullString(
                        (sex, string) -> future.complete(new JsonParser().parse(string)),
                        (sex, err) -> future.completeExceptionally(err));

        return future;
    }

    public static void serverError(final HttpServerExchange exchange, Throwable err) {
        exchange.setStatusCode(500).getResponseSender().send(SERVER_ERROR_JSON);

        if (err != null) {
            log.error("A server error occurred", err);
        }
    }

    public static void invalidRequest(final HttpServerExchange exchange) {
        exchange.setStatusCode(400).getResponseSender().send(BAD_REQUEST_JSON);
    }

    public String getString() {
        return content;
    }

    /**
     * Retrieves the content as a JsonObject which can be handled and manipulated with the Google
     * Gson package.
     *
     * @return {@link JsonObject}
     * @throws IllegalStateException IllegalStateException
     */
    public JsonObject getJsonObject() throws IllegalStateException {
        try {
            return (content == null) ? null : new JsonParser().parse(content).getAsJsonObject();
        } catch (Exception ex) {
            log.error(
                    "An error occurred while running the {} class, message: {}",
                    this,
                    ex.getMessage(),
                    ex);
            return null;
        }
    }

    /**
     * Retrieves the content as a JsonArray which can be handled and manipulated with the Google
     * Gson package.
     *
     * @return {@link JsonArray}
     * @throws IllegalStateException IllegalStateException
     */
    public JsonArray getJsonArray() throws IllegalStateException {
        try {
            return (content == null) ? null : new JsonParser().parse(content).getAsJsonArray();
        } catch (Exception ex) {
            log.error(
                    "An error occurred while running the {} class, message: {}",
                    this,
                    ex.getMessage(),
                    ex);
            return null;
        }
    }

    /**
     * Retrieves the content as a Jsoup Document.
     *
     * @return {@link Document}
     */
    public Document getDocument() {
        try {
            return (content == null) ? null : Jsoup.parse(content);
        } catch (Exception ex) {
            log.error(
                    "An error occurred while running the {} class, message: {}",
                    this,
                    ex.getMessage(),
                    ex);
            return null;
        }
    }
}
