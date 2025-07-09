package com.github.githubsearcher.service;

import com.github.githubsearcher.dto.RepositoryResponseDTO;
import com.github.githubsearcher.dto.SearchRequestDTO;
import com.github.githubsearcher.entity.RepositoryEntity;
import com.github.githubsearcher.repository.RepositoryEntityRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import org.junit.jupiter.api.extension.ExtendWith;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GitHubRepositoryServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RepositoryEntityRepository repositoryRepository;

    @InjectMocks
    private GitHubRepositoryService service;

    @Test
    void testSearchAndSaveRepositories() {
        SearchRequestDTO request = new SearchRequestDTO();
        request.setQuery("spring boot");
        request.setLanguage("Java");
        request.setSort("stars");

        Map<String, Object> item = new HashMap<>();
        item.put("id", 1L);
        item.put("name", "spring-boot-example");
        item.put("description", "A test project");
        item.put("owner", Map.of("login", "octocat"));
        item.put("language", "Java");
        item.put("stargazers_count", 123);
        item.put("forks_count", 10);
        item.put("updated_at", "2024-01-01T12:00:00Z");

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("items", List.of(item));

        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                Mockito.eq(Map.class)
        )).thenReturn(ResponseEntity.ok(responseMap));

        when(repositoryRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // ✅ Fix: Return a mock entity with lastUpdated set!
        RepositoryEntity mockEntity = new RepositoryEntity();
        mockEntity.setId(1L);
        mockEntity.setName("spring-boot-example");
        mockEntity.setLastUpdated(ZonedDateTime.now());

        when(repositoryRepository.saveAll(any())).thenReturn(List.of(mockEntity));

        List<RepositoryResponseDTO> result = service.searchAndSaveRepositories(request);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("spring-boot-example");
        assertThat(result.get(0).getLastUpdated()).isNotNull();
    }
}
