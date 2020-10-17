package com.jab.watcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jab.watcher.model.GithubRepository;
import com.jab.watcher.model.Owner;
import io.vavr.control.Try;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GithubServiceImpl implements GithubService {

    Function<String, URL> toURL = address -> Try.of(() ->
        new URL(address)).getOrElseThrow(ex -> {
        LOGGER.error(ex.getLocalizedMessage(), ex);
        throw new RuntimeException("Bad address", ex);
    });

    Function<Optional<String>, StarredProjects> unserialize = param -> Try.of(() -> {
        ObjectMapper objectMapper = new ObjectMapper();

        //TODO Refactor
        if(param.isPresent()) {
            List<GithubRepository> deserializedData = objectMapper
                .readValue(param.get(), new TypeReference<List<GithubRepository>>() {});

            var list = deserializedData.stream()
                .map(GithubRepository::getIssuesUrl)
                .peek(LOGGER::info)
                .collect(Collectors.toUnmodifiableList());
            LOGGER.info("" + list.size());

            return new StarredProjects(deserializedData);
        } else {
            LOGGER.error("Bad Serialization process");
            throw new RuntimeException();
        }
    }).getOrElseThrow(ex -> {
        LOGGER.error("Bad Serialization process", ex);
        throw new RuntimeException(ex);
    });

    @Override
    public StarredProjects getStarredProjects() {
        return toURL
            .andThen(SimpleCurl.fetch)
            .andThen(unserialize)
            .apply("http://localhost:8090/users/jabrena/starred");
    }
}
