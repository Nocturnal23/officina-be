package com.officina.officina_be.entity;

import java.util.HashSet;
import java.util.Set;

import com.officina.officina_be.core.templates.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Jobs extends BaseEntity {
    @Id
    @SequenceGenerator(sequenceName = "jobs_id_seq", allocationSize = 1, name = "jobs_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jobs_id_seq")
    @Column(name = "jobs_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToMany(mappedBy = "jobs")
    private Set<Booking> bookings = new HashSet<>();

    @ManyToMany(mappedBy = "jobs")
    private Set<InterventionHistory> interventionHistories = new HashSet<>();
    
}
