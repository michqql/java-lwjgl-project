package me.michqql.engine.event;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
