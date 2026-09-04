package com.officina.officina_be.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.officina.officina_be.core.templates.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "intervention_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterventionHistory extends BaseEntity {
    @Id
    @SequenceGenerator(sequenceName = "intervention_history_id_seq", allocationSize = 1, name = "intervention_history_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "intervention_history_id_seq")
    @Column(name = "intervention_history_id", nullable = false, unique = true)
    private Long id;
    
    @Column(name = "kilometers", nullable = false)
    private Long kilometers;

    @Column(name = "cost", nullable = false)
    private double cost;

    @Column(name = "notes", nullable = true)
    private String notes;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    //Associazione intervento/veicolo.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    //Associazione per intervento/prenotazione
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = true)
    private Booking booking;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "loco_services",
        joinColumns = @JoinColumn(name = "loco_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Jobs> jobs = new HashSet<>();
}
