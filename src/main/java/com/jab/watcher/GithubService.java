package com.jab.watcher;

import com.jab.watcher.model.rate.GithubRateLimit;
import io.vavr.Tuple2;

public interface GithubService {

    StarredProjects getStarredProjects(String baseAddress);
    Tuple2<String, Long> getIssuesByProject(String address);
    GithubRateLimit getRateLimitMetrics(String token, String address);

}
