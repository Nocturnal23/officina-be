package com.officina.officina_be.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.officina.officina_be.entity.Jobs;

@Repository
public interface JobsRepository extends JpaRepository<Jobs, Long> {
    Optional<Jobs> findById(Long jobId);

    Optional<Jobs> findByTypeAndDeletedFalse(String type);

    List<Jobs> findByIdInAndDeletedFalse(Collection<Long> ids);

    List<Jobs> findAllByDeletedFalse();

    List<Jobs> findAll();

    boolean existsByTypeIgnoreCaseAndDeletedFalse(String type);
}
