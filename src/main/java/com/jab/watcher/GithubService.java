package com.jab.watcher;

import io.vavr.Tuple2;

public interface GithubService {

    StarredProjects getStarredProjects(String baseAddress);
    Tuple2<String, Long> getIssuesByProject(String address);

}
