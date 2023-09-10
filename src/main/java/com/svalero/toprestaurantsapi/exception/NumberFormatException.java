package com.svalero.toprestaurantsapi.exception;

public class NumberFormatException extends Exception {

    public NumberFormatException(String message) {
        super(message);
    }

    public NumberFormatException() {
        super("You can only enter numbers");
    }
}