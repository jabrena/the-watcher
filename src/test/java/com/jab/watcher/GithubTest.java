package com.jab.watcher;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.jab.watcher.model.GithubRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
class GithubTest {

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

        wireMockServer.stubFor(get(urlEqualTo("/users/jabrena/starred"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("github/jabrena-starred-page1-ok.json")));

        wireMockServer.stubFor(get(urlEqualTo("/users/jabrena/starred?page=1"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("github/jabrena-starred-page1-ok.json")));

        wireMockServer.stubFor(get(urlEqualTo("/users/jabrena/starred?page=2"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("github/jabrena-starred-page2-ok.json")));
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

}
