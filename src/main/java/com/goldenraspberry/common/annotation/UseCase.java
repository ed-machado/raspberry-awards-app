package com.goldenraspberry.common.annotation;

import java.lang.annotation.*;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/** Annotation para marcar classes de casos de uso. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface UseCase {

  @AliasFor(annotation = Component.class)
  String value() default "";
}
