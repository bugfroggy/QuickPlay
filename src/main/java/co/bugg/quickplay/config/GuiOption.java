package co.bugg.quickplay.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuiOption {
    String name() default "Undefined";

    String description() default "Undefined";

    ConfigCategory category() default ConfigCategory.GENERAL;

    double priority() default 0.0;
}
