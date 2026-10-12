package com.qaautomation.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/** Checks links with plain HTTP requests - much faster than opening each one in the browser. */
public final class LinkChecker {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private LinkChecker() {
    }

    /** Returns the HTTP status code, or -1 if the server could not be reached. */
    public static int getStatusCode(String url) {
        int status = send(url, "HEAD");
        // Some servers don't support HEAD requests; try a normal GET before calling it broken.
        if (status == 405 || status == 403) {
            status = send(url, "GET");
        }
        return status;
    }

    /** Returns only the links that failed, mapped to their status code. */
    public static Map<String, Integer> findBrokenLinks(Collection<String> urls) {
        Map<String, Integer> broken = new LinkedHashMap<>();
        for (String url : urls) {
            int status = getStatusCode(url);
            if (status < 200 || status >= 400) {
                broken.put(url, status);
            }
        }
        return broken;
    }

    private static int send(String url, String method) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .header("User-Agent", "Mozilla/5.0 (QA link checker)")
                .timeout(Duration.ofSeconds(15))
                .build();
        try {
            return CLIENT.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
        } catch (IOException e) {
            return -1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }
    }
}
