package com.officina.officina_be.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officina.officina_be.dto.request.JobRequest;
import com.officina.officina_be.dto.response.JobResponse;
import com.officina.officina_be.entity.Jobs;
import com.officina.officina_be.error.DuplicateResource;
import com.officina.officina_be.error.ResourceNotFound;
import com.officina.officina_be.mapper.JobMapper;
import com.officina.officina_be.repository.JobsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobMapper jobMapper;
    private final JobsRepository jobsRepo;

    @Transactional
    public JobResponse registerService(JobRequest request) {
        if (jobsRepo.existsByTypeIgnoreCaseAndDeletedFalse(request.type())) {
            throw new DuplicateResource("L'officina offre già questo servizio.");
        }

        Jobs job = jobMapper.toEntity(request);
        Jobs savedJob = jobsRepo.save(job);

        return jobMapper.toResponse(savedJob);
    }

    @Transactional
    public void deleteJob(Long jobId) {
        Jobs job = jobsRepo.findByIdAndDeletedFalse(jobId)
            .orElseThrow(() -> new ResourceNotFound("Servizio non trovato."));

        job.setDeleted(true);
        jobsRepo.save(job);
    }

    @Transactional 
    public JobResponse updateJob(Long jobId, Map<String, Object> updates){
        Jobs job = jobsRepo.findByIdAndDeletedFalse(jobId)
            .orElseThrow(() -> new ResourceNotFound("Lavoro non trovato."));

        if(updates.containsKey("type")) {
            job.setType((String) updates.get("type"));
        }
        if(updates.containsKey("description")) {
            job.setDescription((String) updates.get("description"));
        }

        Jobs savedJob = jobsRepo.save(job);
        return jobMapper.toResponse(savedJob);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs() {
        return jobsRepo.findAllByDeletedFalse()
            .stream()
            .map(jobMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long jobId) {
        Jobs job = jobsRepo.findByIdAndDeletedFalse(jobId)
            .orElseThrow(() -> new ResourceNotFound("Lavoro non trovato."));

        return jobMapper.toResponse(job);
    }
}
