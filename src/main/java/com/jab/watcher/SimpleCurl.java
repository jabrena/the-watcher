package com.jab.watcher;

import io.vavr.control.Option;
import io.vavr.control.Try;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleCurl {

    /**
     * Function to fetch information from an URL.
     *
     */
    //TODO Review Webclient
    static Function<URL, Optional<String>> fetch = url -> Try.of(() -> {

        LOGGER.debug("Thread: {}", Thread.currentThread().getName());
        LOGGER.debug("Requested URL: {}", url);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url.toURI())
                .build();

        return Option.some(client
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body()).toJavaOptional();
    })
    .onFailure(ex -> LOGGER.error("SimpleCURL Error: {}", ex.getLocalizedMessage(), ex))
    .recover(ex -> Optional.empty())
    .get();
}
