package com.cmp_huerto.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SanitizedHtmlValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SanitizedHtml {
    String message() default "El texto contiene contenido HTML no permitido o malicioso";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class SanitizedHtmlValidator implements ConstraintValidator<SanitizedHtml, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Los nulos se manejan con @NotNull o @NotBlank por separado
        }

        // Safelist.none() remueve todo HTML: ningún dato de la parcela debe llevar etiquetas.
        String sanitizedValue = Jsoup.clean(value, Safelist.none());

        // Si el texto original era diferente al sanitizado, traía HTML y se rechaza.
        return value.equals(sanitizedValue);
    }
}
