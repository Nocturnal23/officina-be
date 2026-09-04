package com.officina.officina_be.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.officina.officina_be.entity.Jobs;

@Repository
public interface JobsRepository extends JpaRepository<Jobs, Long> {
    Optional<Jobs> findByIdAndDeletedFalse(Long jobId);

    Optional<Jobs> findByTypeAndDeletedFalse(String type);

    List<Jobs> findByIdInAndDeletedFalse(Collection<Long> ids);

    List<Jobs> findAllByDeletedFalse();

    boolean existsByTypeIgnoreCaseAndDeletedFalse(String type);
}
