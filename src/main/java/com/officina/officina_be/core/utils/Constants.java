package com.officina.officina_be.core.utils;

public class Constants {
    public enum Role {
        USER,
        ADMIN
    };

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    };

    //TODO Usare questo enum per i lavori offerti dall'officina.
    public enum Services {
        ALTRO,
        TAGLIANDO,
    }
}
