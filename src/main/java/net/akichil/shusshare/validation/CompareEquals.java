package net.akichil.shusshare.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {CompareEqualsValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompareEquals {

    String message() default "一致しません";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // チェックする値
    String value1();

    String value2();

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        CompareEquals[] values();
    }

}
