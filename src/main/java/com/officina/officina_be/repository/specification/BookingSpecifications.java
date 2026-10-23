package com.officina.officina_be.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.officina.officina_be.core.utils.Constants.BookingStatus;
import com.officina.officina_be.dto.request.HistoryFilterRequest;
import com.officina.officina_be.entity.Booking;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public final class BookingSpecifications {
    private BookingSpecifications() {
    }

    public static Specification<Booking> history(
            Long userId,
            List<BookingStatus> statuses,
            HistoryFilterRequest filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }

            if (filters != null) {
                switch (filters.getDateMode() == null ? "" : filters.getDateMode()) {
                    case "range" -> {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                                root.get("appointmentDate"), filters.getDateFrom()));
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(
                                root.get("appointmentDate"), filters.getDateTo()));
                    }
                    case "on" -> predicates.add(criteriaBuilder.equal(
                            root.get("appointmentDate"), filters.getDate()));
                    case "before" -> predicates.add(criteriaBuilder.lessThan(
                            root.get("appointmentDate"), filters.getDate()));
                    case "after" -> predicates.add(criteriaBuilder.greaterThan(
                            root.get("appointmentDate"), filters.getDate()));
                    default -> {
                    }
                }

                if (filters.getVehicleBrand() != null && !filters.getVehicleBrand().isBlank()) {
                    predicates.add(criteriaBuilder.equal(
                            root.join("vehicle", JoinType.INNER).get("brand"),
                            filters.getVehicleBrand()));
                }
                if (filters.getVehicleModel() != null && !filters.getVehicleModel().isBlank()) {
                    predicates.add(criteriaBuilder.equal(
                            root.join("vehicle", JoinType.INNER).get("model"),
                            filters.getVehicleModel()));
                }
                if (filters.getVehiclePlate() != null && !filters.getVehiclePlate().isBlank()) {
                    predicates.add(criteriaBuilder.equal(
                            criteriaBuilder.upper(root.join("vehicle", JoinType.INNER).get("plate")),
                            filters.getVehiclePlate().trim().toUpperCase()));
                }
                if (filters.getVehicleId() != null) {
                    predicates.add(criteriaBuilder.equal(
                            root.join("vehicle", JoinType.INNER).get("id"),
                            filters.getVehicleId()));
                }
                if (filters.getJobIds() != null && !filters.getJobIds().isEmpty()) {
                    predicates.add(root.join("jobs", JoinType.INNER)
                            .get("id").in(filters.getJobIds()));
                    query.distinct(true);
                }
                if (userId == null && filters.getUserId() != null) {
                    predicates.add(criteriaBuilder.equal(
                            root.get("user").get("id"),
                            filters.getUserId()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
