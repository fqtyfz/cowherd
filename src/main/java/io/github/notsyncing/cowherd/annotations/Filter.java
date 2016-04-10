package io.github.notsyncing.cowherd.annotations;

import io.github.notsyncing.cowherd.server.ServiceActionFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter
{
    Class<? extends ServiceActionFilter> value();
}
