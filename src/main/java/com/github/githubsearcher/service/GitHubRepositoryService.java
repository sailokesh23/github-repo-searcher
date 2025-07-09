package com.github.githubsearcher.service;

import com.github.githubsearcher.dto.SearchRequestDTO;
import com.github.githubsearcher.dto.RepositoryResponseDTO;
import com.github.githubsearcher.entity.RepositoryEntity;
import com.github.githubsearcher.repository.RepositoryEntityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class GitHubRepositoryService {

    private final RestTemplate restTemplate;
    private final RepositoryEntityRepository  repositoryRepository;

    @Value("${github.api.url:https://api.github.com/search/repositories}")
    private String githubApiUrl;

    @Value("${github.api.token}")
    private String githubApiToken;


    public GitHubRepositoryService(RestTemplate restTemplate,
                                   RepositoryEntityRepository repositoryRepository) {
        this.restTemplate = restTemplate;
        this.repositoryRepository = repositoryRepository;
    }

    /**
     * Calls GitHub API, saves/updates repos, returns saved ones.
    */
    public List<RepositoryResponseDTO> searchAndSaveRepositories(SearchRequestDTO request) {
        String url = githubApiUrl + "?q=" + request.getQuery();
        if (request.getLanguage() != null && !request.getLanguage().isBlank()) {
            url += "+language:" + request.getLanguage();
        }
        if (request.getSort() != null && !request.getSort().isBlank()) {
            url += "&sort=" + request.getSort();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github.v3+json");
        headers.set("Authorization", "Bearer " + githubApiToken); // ✅ Add this line!

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        List<RepositoryEntity> entities = new ArrayList<>();

        for (Map<String, Object> item : items) {
            Long repoId = ((Number) item.get("id")).longValue();
            String name = (String) item.get("name");
            String description = (String) item.get("description");
            Map<String, Object> owner = (Map<String, Object>) item.get("owner");
            String ownerName = (String) owner.get("login");
            String language = (String) item.get("language");
            Integer stars = ((Number) item.get("stargazers_count")).intValue();
            Integer forks = ((Number) item.get("forks_count")).intValue();
            String lastUpdated = (String) item.get("updated_at");

            RepositoryEntity entityDB = repositoryRepository.findById(repoId)
                    .orElse(new RepositoryEntity());
            entityDB.setId(repoId);
            entityDB.setName(name);
            entityDB.setDescription(description);
            entityDB.setOwnerName(ownerName);
            entityDB.setLanguage(language);
            entityDB.setStars(stars);
            entityDB.setForks(forks);
            entityDB.setLastUpdated(ZonedDateTime.parse(lastUpdated, DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            entities.add(entityDB);
        }

        List<RepositoryEntity> savedEntities = repositoryRepository.saveAll(entities);

        return savedEntities.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves stored repositories with optional filters & sorting.
     */
    public List<RepositoryResponseDTO> getStoredRepositories(String language, Integer minStars, String sort) {
        List<RepositoryEntity> entities;

        if (language == null && minStars == null) {
            entities = repositoryRepository.findAll();
        } else if (language != null && minStars != null) {
            entities = repositoryRepository.findByLanguageIgnoreCaseAndStarsGreaterThanEqual(language, minStars);
        } else if (language != null) {
            entities = repositoryRepository.findByLanguageIgnoreCase(language);
        } else {
            entities = repositoryRepository.findByStarsGreaterThanEqual(minStars);
        }

        if (sort != null) {
            if (sort.equalsIgnoreCase("forks")) {
                entities.sort(Comparator.comparingInt(RepositoryEntity::getForks).reversed());
            } else if (sort.equalsIgnoreCase("updated")) {
                entities.sort(Comparator.comparing(RepositoryEntity::getLastUpdated).reversed());
            } else {
                // Default: sort by stars
                entities.sort(Comparator.comparingInt(RepositoryEntity::getStars).reversed());
            }
        }

        return entities.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private RepositoryResponseDTO mapToResponseDTO(RepositoryEntity entity) {
        RepositoryResponseDTO dto = new RepositoryResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setOwner(entity.getOwnerName());
        dto.setLanguage(entity.getLanguage());
        dto.setStars(entity.getStars());
        dto.setForks(entity.getForks());
        dto.setLastUpdated(entity.getLastUpdated().toString());
        return dto;
    }
}
