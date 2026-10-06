package com.officina.officina_be.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.officina.officina_be.core.templates.BaseEntity;
import com.officina.officina_be.core.utils.Constants.BookingStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseEntity {
    @Id
    @SequenceGenerator(sequenceName = "booking_id_seq", allocationSize = 1, name = "booking_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_id_seq")
    @Column(name = "booking_id", nullable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "notes", nullable = true)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "booking_services",
        joinColumns = @JoinColumn(
            name = "booking_id", 
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (booking_id) REFERENCES bookings ON DELETE CASCADE")
        ),
        inverseJoinColumns = @JoinColumn(
            name = "service_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (service_id) REFERENCES jobs ON DELETE RESTRICT")
        )
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Jobs> jobs = new HashSet<>();
}
