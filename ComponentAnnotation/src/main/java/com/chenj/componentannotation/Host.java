package com.chenj.componentannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chenjun
 * create at 2019-05-29
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Host {
    String value();
}
