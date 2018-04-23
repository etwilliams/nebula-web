package com.dakuupa.nebula;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author etwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HTTPMethods {
    HTTPMethod[] value();
}
