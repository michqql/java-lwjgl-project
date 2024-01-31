package me.michqql.engine.entity.components;

import me.michqql.engine.entity.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Conflicts {
    Class<? extends Component>[] conflictingComponents();
}
