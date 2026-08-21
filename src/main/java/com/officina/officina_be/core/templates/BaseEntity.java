package com.officina.officina_be.core.templates;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity implements IdentifiableEntity  {
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    protected LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "modified_date")
    protected LocalDateTime modifiedDate;

    @Column(name = "deleted", nullable = false)
    protected Long deleted;
}
