package me.michqql.engine.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public final class EventManager {

    // Static start
    private static EventManager instance;

    public static EventManager getInstance() {
        if(instance == null)
            instance = new EventManager();

        return instance;
    }
    // Static end

    private final HashMap<Class<? extends Event>, PriorityQueue<EventMethod>> LISTENER_MAP = new HashMap<>();

    // Ensure this class cannot be instantiated
    private EventManager() {}

    public void registerListener(final Listener listener) {
        // Find all the methods in the listener class annotated with @EventHandler
        Class<? extends Listener> type = listener.getClass();
        Method[] methods = type.getMethods();
        for(Method method : methods) {
            EventHandler handler = method.getAnnotation(EventHandler.class);
            if(handler == null || method.getParameterCount() != 1 ||
                    !Event.class.isAssignableFrom(method.getParameterTypes()[0]))
                continue;

            @SuppressWarnings("unchecked") // Cast is checked above, and is subclass of Event.class
            Class<? extends Event> eventType = (Class<? extends Event>) method.getParameterTypes()[0];
            // Check if event type that is being listened to is abstract, cannot listen to abstract events
            if(Modifier.isAbstract(eventType.getModifiers())) {
                throw new ListenerRegistrationException("Event Handler is listening to abstract event type. " +
                        "listener=" + listener.getClass().getSimpleName() + " eventHandler=" + method.getName() +
                        " eventType=" + eventType.getSimpleName());
            }

            LISTENER_MAP.compute(eventType, (clazz, queue) -> {
                if(queue == null) {
                    queue = new PriorityQueue<>(Comparator.comparingInt(m -> m.handler.priority().getPriority()));
                }

                queue.add(new EventMethod(listener, method, handler));
                return queue;
            });
        }
    }

    public void callEvent(Event event) {
        Class<? extends Event> eventType = event.getClass();
        if(Modifier.isAbstract(eventType.getModifiers())) {
            throw new RuntimeException("Cannot call event for abstract event class. eventType=" +
                    eventType.getSimpleName());
        }

        PriorityQueue<EventMethod> queue = LISTENER_MAP.get(eventType);
        if(queue == null) return; // No listeners are listening to this event type!

        for(EventMethod method : queue) {
            // Skip event handlers that don't want to handle a cancelled event, if this event is cancelled
            if(event instanceof Cancellable cancellable && cancellable.isCancelled() &&
                    !method.handler.handleCancelled())
                continue;
            try {
                method.method.invoke(method.invokerObject, event);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Wraps the listener and the specific method in the listener for use for the event
     */
    private final class EventMethod {
        final Listener invokerObject;
        final Method method;
        final EventHandler handler;

        public EventMethod(Listener invokerObject, Method method, EventHandler handler) {
            this.invokerObject = invokerObject;
            this.method = method;
            this.handler = handler;
        }
    }
}
