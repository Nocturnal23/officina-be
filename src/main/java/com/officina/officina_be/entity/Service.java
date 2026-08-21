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
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service extends BaseEntity {
    @Id
    @SequenceGenerator(sequenceName = "service_id_seq", allocationSize = 1, name = "service_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_id_seq")
    @Column(name = "service_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type; // Da rivedere il tipo

    @Column(name = "description", nullable = true)
    private String description;

    @ManyToMany(mappedBy = "services")
    private Set<Booking> bookings = new HashSet<>();

    @ManyToMany(mappedBy = "services")
    private Set<InterventionHistory> interventionHistories = new HashSet<>();
    
}
