package com.github.githubsearcher.controller;

import com.github.githubsearcher.dto.RepositoryResponseDTO;
import com.github.githubsearcher.dto.SearchRequestDTO;
import com.github.githubsearcher.service.GitHubRepositoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GitHubRepositoryController.class)
class GitHubRepositoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubRepositoryService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSearchRepositories_Success() throws Exception {
        SearchRequestDTO request = new SearchRequestDTO();
        request.setQuery("spring boot");
        request.setLanguage("Java");
        request.setSort("stars");

        RepositoryResponseDTO response = new RepositoryResponseDTO();
        response.setId(1L);
        response.setName("spring-boot-example");

        Mockito.when(service.searchAndSaveRepositories(any()))
                .thenReturn(List.of(response));

        mockMvc.perform(post("/api/github/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("spring-boot-example"));
    }
}
