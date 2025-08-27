package com.goldenraspberry.common.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Annotation para marcar classes de domínio.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface DomainService {

    @AliasFor(annotation = Component.class)
    String value() default "";
}
