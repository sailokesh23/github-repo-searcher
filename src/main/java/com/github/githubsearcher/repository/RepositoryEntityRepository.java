package com.github.githubsearcher.repository;

import com.github.githubsearcher.entity.RepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositoryEntityRepository extends JpaRepository<RepositoryEntity, Long> {

    List<RepositoryEntity> findByLanguageIgnoreCase(String language);

    List<RepositoryEntity> findByStarsGreaterThanEqual(Integer stars);

    List<RepositoryEntity> findByLanguageIgnoreCaseAndStarsGreaterThanEqual(String language, Integer stars);

}
