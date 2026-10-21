package com.officina.officina_be.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.officina.officina_be.core.utils.Constants.BookingStatus;
import com.officina.officina_be.dto.request.HistoryFilterRequest;
import com.officina.officina_be.entity.InterventionHistory;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public final class InterventionSpecifications {
    private InterventionSpecifications() {
    }

    public static Specification<InterventionHistory> history(
            Long userId,
            List<BookingStatus> statuses,
            HistoryFilterRequest filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            var vehicle = root.join("vehicle", JoinType.INNER);
            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(vehicle.get("user").get("id"), userId));
                predicates.add(criteriaBuilder.isFalse(vehicle.get("deleted")));
            }

            if (filters != null) {
                switch (filters.getDateMode() == null ? "" : filters.getDateMode()) {
                    case "range" -> {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                                root.get("completedAt"), filters.getDateFrom()));
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(
                                root.get("completedAt"), filters.getDateTo()));
                    }
                    case "on" -> predicates.add(criteriaBuilder.equal(
                            root.get("completedAt"), filters.getDate()));
                    case "before" -> predicates.add(criteriaBuilder.lessThan(
                            root.get("completedAt"), filters.getDate()));
                    case "after" -> predicates.add(criteriaBuilder.greaterThan(
                            root.get("completedAt"), filters.getDate()));
                    default -> {
                    }
                }

                if (filters.getVehicleBrand() != null && !filters.getVehicleBrand().isBlank()) {
                    predicates.add(criteriaBuilder.equal(
                            vehicle.get("brand"),
                            filters.getVehicleBrand()));
                }
                if (filters.getVehicleModel() != null && !filters.getVehicleModel().isBlank()) {
                    predicates.add(criteriaBuilder.equal(
                            vehicle.get("model"),
                            filters.getVehicleModel()));
                }
                if (filters.getVehiclePlate() != null && !filters.getVehiclePlate().isBlank()) {
                    predicates.add(criteriaBuilder.equal(
                            criteriaBuilder.upper(vehicle.get("plate")),
                            filters.getVehiclePlate().trim().toUpperCase()));
                }
                if (filters.getVehicleId() != null) {
                    predicates.add(criteriaBuilder.equal(
                            vehicle.get("id"),
                            filters.getVehicleId()));
                }
                if (filters.getJobIds() != null && !filters.getJobIds().isEmpty()) {
                    predicates.add(root.join("jobs", JoinType.INNER)
                            .get("id").in(filters.getJobIds()));
                    query.distinct(true);
                }
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
