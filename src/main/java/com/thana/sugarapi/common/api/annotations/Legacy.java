package com.thana.sugarapi.common.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Legacy {

    boolean old() default false;
}
