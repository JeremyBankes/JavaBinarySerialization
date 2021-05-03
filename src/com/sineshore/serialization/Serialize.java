package com.sineshore.serialization;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Serialize {
}
