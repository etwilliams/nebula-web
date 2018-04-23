package com.dakuupa.nebula;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Eric
 */
@Repeatable(HTTPMethods.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface HTTPMethod {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    String action() default "";
    String value() default GET;
}
