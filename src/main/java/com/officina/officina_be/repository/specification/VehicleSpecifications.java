package com.officina.officina_be.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.officina.officina_be.entity.Booking;
import com.officina.officina_be.entity.Vehicle;

import jakarta.persistence.criteria.Predicate;

public final class VehicleSpecifications {
    private VehicleSpecifications() {
    }

    public static Specification<Vehicle> searchForUserHistory(Long userId, List<String> terms) {
        return (root, query, criteriaBuilder) -> {
            var historyBooking = query.subquery(Long.class);
            var bookingRoot = historyBooking.from(Booking.class);
            historyBooking.select(bookingRoot.get("id"));
            historyBooking.where(
                    criteriaBuilder.equal(bookingRoot.get("vehicle"), root),
                    criteriaBuilder.equal(bookingRoot.get("user").get("id"), userId),
                    criteriaBuilder.isFalse(bookingRoot.get("deleted")));

            Predicate currentlyOwned = criteriaBuilder.and(
                    criteriaBuilder.isFalse(root.get("deleted")),
                    criteriaBuilder.equal(root.get("user").get("id"), userId));
            Predicate presentInHistory = criteriaBuilder.exists(historyBooking);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.or(currentlyOwned, presentInHistory));

            for (String term : terms) {
                String pattern = "%" + term.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("plate")), pattern)));
            }

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
