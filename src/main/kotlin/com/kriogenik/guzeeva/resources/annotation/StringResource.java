package com.kriogenik.guzeeva.resources.annotation;

import com.kriogenik.guzeeva.resources.StringResources;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE})
public @interface StringResource {

    StringResources value();

}
