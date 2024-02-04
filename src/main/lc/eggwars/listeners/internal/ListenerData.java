package lc.eggwars.listeners.internal;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ListenerData {
    public Class<? extends Event> event();
    public EventPriority priority() default EventPriority.NORMAL;
}