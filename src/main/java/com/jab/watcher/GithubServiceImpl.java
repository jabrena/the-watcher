package com.jab.watcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jab.watcher.model.issues.GithubIssues;
import com.jab.watcher.model.rate.GithubRateLimit;
import com.jab.watcher.model.stars.GithubRepository;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GithubServiceImpl implements GithubService {

    Function<String, URL> toURL = address -> Try.of(() ->
        new URL(address)).getOrElseThrow(ex -> {
        LOGGER.error(ex.getLocalizedMessage(), ex);
        throw new RuntimeException("Bad address", ex);
    });

    Function<Optional<String>, List<GithubRepository>> unserializeStarredProjects = param -> Try.of(() -> {
        ObjectMapper objectMapper = new ObjectMapper();

        //TODO Refactor
        if(param.isPresent()) {
            List<GithubRepository> deserializedData = objectMapper
                .readValue(param.get(), new TypeReference<List<GithubRepository>>() {});

            return deserializedData;
        } else {
            LOGGER.error("Bad Serialization process");
            throw new RuntimeException();
        }
    }).getOrElseThrow(ex -> {
        LOGGER.error("Bad Serialization process", ex);
        throw new RuntimeException(ex);
    });

    @Override
    public StarredProjects getStarredProjects(String baseAddress) {

        List<GithubRepository> projects = new ArrayList<>();
        AtomicInteger page = new AtomicInteger(0);

        //TODO Make functional the break
        while(true) {

            var address = baseAddress + "?page=" + page.incrementAndGet();
            var currentPage = toURL
                .andThen(SimpleCurl.fetch)
                .andThen(unserializeStarredProjects)
                .apply(address);

            LOGGER.info("CurrentPage:" + currentPage.size());
            projects.addAll(currentPage);
            if(currentPage.size() == 0) {
                LOGGER.info("Exit condition: True");
                break;
            }
        }

        return new StarredProjects(projects);
    }

    Function<Optional<String>, List<GithubIssues>> unserializeIssues = param -> Try.of(() -> {
        ObjectMapper objectMapper = new ObjectMapper();

        //TODO Refactor
        if(param.isPresent()) {
            List<GithubIssues> deserializedData = objectMapper
                .readValue(param.get(), new TypeReference<List<GithubIssues>>() {});

            return deserializedData;
        } else {
            LOGGER.error("Bad Serialization process");
            throw new RuntimeException();
        }
    }).getOrElseThrow(ex -> {
        LOGGER.error("Bad Serialization process", ex);
        throw new RuntimeException(ex);
    });

    @Override
    public Tuple2<String, Long> getIssuesByProject(String address) {

        var currentPage = toURL
            .andThen(SimpleCurl.fetch)
            .andThen(unserializeIssues)
            .apply(address);


        return new Tuple2(address, currentPage.size());
    }

    Function<Optional<String>, GithubRateLimit> unserializeRateLimitMetrics = param -> Try.of(() -> {
        ObjectMapper objectMapper = new ObjectMapper();

        //TODO Refactor
        if(param.isPresent()) {
            GithubRateLimit deserializedData = objectMapper
                .readValue(param.get(), new TypeReference<GithubRateLimit>() {});

            return deserializedData;
        } else {
            LOGGER.error("Bad Serialization process");
            throw new RuntimeException();
        }
    }).getOrElseThrow(ex -> {
        LOGGER.error("Bad Serialization process", ex);
        throw new RuntimeException(ex);
    });

    @Override
    public GithubRateLimit getRateLimitMetrics(String token, String address) {

        var metrics = toURL
            .andThen(url -> SimpleCurl.fetchWithToken.apply(url, token))
            .andThen(unserializeRateLimitMetrics)
            .apply(address);

        return metrics;
    }
}
