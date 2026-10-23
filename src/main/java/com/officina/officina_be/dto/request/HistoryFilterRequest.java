package com.officina.officina_be.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryFilterRequest {
    @Pattern(regexp = "range|on|before|after", message = "Modalità data non valida.")
    private String dateMode;

    private LocalDate date;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    private String vehicleBrand;
    private String vehicleModel;
    private String vehiclePlate;

    @Positive
    private Long vehicleId;

    @Positive
    private Long userId;
    
    private List<@Positive Long> jobIds;

    @AssertTrue(message = "I parametri della data non corrispondono alla modalità selezionata.")
    public boolean isDateFilterValid() {
        boolean hasDate = date != null;
        boolean hasRangeBound = dateFrom != null || dateTo != null;

        if (dateMode == null) {
            return !hasDate && !hasRangeBound;
        }
        if ("range".equals(dateMode)) {
            return !hasDate && dateFrom != null && dateTo != null && !dateFrom.isAfter(dateTo);
        }
        return hasDate && !hasRangeBound;
    }
}
