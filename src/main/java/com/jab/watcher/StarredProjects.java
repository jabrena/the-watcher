package com.jab.watcher;

import com.jab.watcher.model.GithubRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StarredProjects {

    private final List<GithubRepository> list;
}
