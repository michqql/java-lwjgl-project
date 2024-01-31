package me.michqql.engine.event;

/**
 * The events get called in the order of HIGHEST to LOWEST,
 * meaning the highest priority event handlers get called first,
 * and there is a chance the event gets cancelled before the lowest
 * priority event handlers get to process the event.
 */
public enum EventPriority {

    HIGHEST(4), HIGH(3), NORMAL(2), LOW(1), LOWEST(0);

    final int priority;

    EventPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
