package stg.onyou.annotation;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.lang.annotation.*;

@NotBlank
@Size(min = 2)
@Constraint(validatedBy = NoWhitespaceValidator.class)
@ReportAsSingleViolation
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NoWhitespace {
    String message() default "Keyword must not contain whitespace";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
