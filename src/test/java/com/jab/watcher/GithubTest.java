package com.jab.watcher;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.jab.watcher.model.rate.GithubRateLimit;
import com.jab.watcher.model.stars.GithubRepository;
import io.vavr.Tuple2;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
class GithubTest {

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    WireMockServer wireMockServer;

    @BeforeEach
    public void setup () {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();
    }

    @AfterEach
    public void teardown () {
        wireMockServer.stop();
    }

    private void loadStubs() {

        //Github Starred Projects

        wireMockServer.stubFor(get(urlEqualTo("/users/jabrena/starred?page=1"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("github/jabrena-starred-page1-ok.json")));

        wireMockServer.stubFor(get(urlEqualTo("/users/jabrena/starred?page=2"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("github/jabrena-starred-page2-ok.json")));

        //Github Repository Issues

        wireMockServer.stubFor(get(urlEqualTo("/repos/arrow-kt/arrow-core/issues"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("github/arrow-kt-arrow-core-page1-ok.json")));

        wireMockServer.stubFor(get(urlEqualTo("/repos/arrow-kt/arrow-core/issues?page=1"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("github/arrow-kt-arrow-core-page1-ok.json")));

        wireMockServer.stubFor(get(urlEqualTo("/repos/arrow-kt/arrow-core/issues?page=2"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("github/arrow-kt-arrow-core-page2-ok.json")));

        //Github Rate Limit

        wireMockServer.stubFor(get(urlEqualTo("/rate_limit"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("github/jabrena-rate_limit.json")));
    }

    @Test
    public void given_githubService_when_callStarredProjects_then_ok() {

        loadStubs();

        GithubService service = new GithubServiceImpl();
        //var address = "https://api.github.com/users/jabrena/starred";
        var address = "http://localhost:8090/users/jabrena/starred";
        var starredProjects = service.getStarredProjects(address);

        //TODO Remove
        starredProjects.getList().stream()
            .map(GithubRepository::getFullName)
            .forEach(LOGGER::info);

        then(starredProjects).isNotNull();
    }

    @Test
    public void given_githubService_when_callIssues_then_ok() {

        loadStubs();

        GithubService service = new GithubServiceImpl();

        var repository = "arrow-kt/arrow-core";
        var list = List.of(repository);

        //TODO Add pagination support and date parameter
        //?since=2020-01-01T00:00:00Z&page=4
        //var baseAddress = "https://api.github.com/repos/";
        var baseAddress  = "http://localhost:8090/repos/";
        var resources = "/issues";
        List<Tuple2<String, Long>> issues = list.stream()
            .map(x -> baseAddress + x + resources)
            .map(service::getIssuesByProject)
            .peek(x -> LOGGER.info(x.toString()))
            .collect(Collectors.toUnmodifiableList());

        then(issues).isNotNull();
    }

    @Test
    public void given_githubService_when_callRateLimitMetrics_then_ok() {

        loadStubs();

        String token = "MY_GITHUB_OAUTH_TOKEN";
        //String address = "https://api.github.com/rate_limit";
        String address  = "http://localhost:8090/rate_limit";

        GithubService service = new GithubServiceImpl();
        GithubRateLimit githubRateLimit = service.getRateLimitMetrics(token, address);

        then(githubRateLimit.getRate().getLimit()).isEqualTo(5000);
        then(githubRateLimit.getRate().getRemaining()).isGreaterThan(0);
    }

}
