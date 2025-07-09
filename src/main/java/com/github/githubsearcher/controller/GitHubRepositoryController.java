package com.github.githubsearcher.controller;

import com.github.githubsearcher.dto.SearchRequestDTO;
import com.github.githubsearcher.dto.RepositoryResponseDTO;
import com.github.githubsearcher.service.GitHubRepositoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubRepositoryController {

    private final GitHubRepositoryService gitHubRepositoryService;

    public GitHubRepositoryController(GitHubRepositoryService gitHubRepositoryService) {
        this.gitHubRepositoryService = gitHubRepositoryService;
    }

    /**
     * POST /api/github/search
     * Search GitHub repositories and save/update them in the database.
     */
    @PostMapping("/search")
    public ResponseEntity<List<RepositoryResponseDTO>> searchRepositories(
            @RequestBody SearchRequestDTO searchRequestDTO) {
        List<RepositoryResponseDTO> savedRepositories = gitHubRepositoryService.searchAndSaveRepositories(searchRequestDTO);
        return ResponseEntity.ok(savedRepositories);
    }

    /**
     * GET /api/github/repositories
     * Retrieve stored repositories with optional filters.
     */
    @GetMapping("/repositories")
    public ResponseEntity<List<RepositoryResponseDTO>> getRepositories(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(required = false, defaultValue = "stars") String sort) {

        List<RepositoryResponseDTO> repositories =
                gitHubRepositoryService.getStoredRepositories(language, minStars, sort);

        return ResponseEntity.ok(repositories);
    }
}

