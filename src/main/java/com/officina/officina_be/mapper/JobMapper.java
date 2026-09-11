package com.officina.officina_be.mapper;

import org.springframework.stereotype.Component;

import com.officina.officina_be.dto.request.JobRequest;
import com.officina.officina_be.dto.response.JobResponse;
import com.officina.officina_be.entity.Jobs;

@Component
public class JobMapper {
    public Jobs toEntity(JobRequest request) {
        Jobs service = new Jobs();
        service.setType(request.type());
        service.setDescription(request.description());
        return service;
    }

    public JobResponse toResponse(Jobs entity) {
        return new JobResponse(
            entity.getId(),
            entity.getType(),
            entity.getDescription(),
            entity.isDeleted()
        );
    }
}
