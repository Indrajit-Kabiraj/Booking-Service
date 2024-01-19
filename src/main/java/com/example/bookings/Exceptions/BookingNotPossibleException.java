package com.example.bookings.Exceptions;

public class BookingNotPossibleException extends Throwable {
    public BookingNotPossibleException() {
    }

    public BookingNotPossibleException(String message) {
        super(message);
    }

    public BookingNotPossibleException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingNotPossibleException(Throwable cause) {
        super(cause);
    }

    public BookingNotPossibleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
