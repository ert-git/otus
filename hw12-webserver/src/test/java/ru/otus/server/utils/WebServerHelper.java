package ru.otus.server.utils;

import static ru.otus.server.utils.HttpUrlConnectionHelper.sendRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jetty.http.HttpMethod;

public final class WebServerHelper {

    private static final String COOKIE_NAME_JSESSIONID = "JSESSIONID";
    public static final String COOKIE_HEADER = "Cookie";


    private WebServerHelper() {
    }

    public static HttpCookie login(String url, String login, String password) throws IOException {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        HttpURLConnection connection = sendRequest(url, HttpMethod.POST);
        try {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(String.format("login=%s&password=%s", login, password).getBytes());
                os.flush();
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
                return cookies.stream().filter(c -> c.getName().equalsIgnoreCase(COOKIE_NAME_JSESSIONID)).findFirst().orElse(null);
            }
        } finally {
            connection.disconnect();
        }
        return null;
    }

    public static String readInput(InputStream in) {
        return new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

    }
}
