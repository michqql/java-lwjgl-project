package me.michqql.engine.event;

public class ListenerRegistrationException extends RuntimeException {

    public ListenerRegistrationException(String reason) {
        super(reason);
    }
}
