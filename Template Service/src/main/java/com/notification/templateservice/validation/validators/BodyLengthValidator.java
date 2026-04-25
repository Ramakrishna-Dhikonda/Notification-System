package com.notification.templateservice.validation.validators;

import com.notification.templateservice.domain.NotificationTemplate;
import com.notification.templateservice.validation.ValidationError;
import com.notification.templateservice.validation.ValidationResult;
import com.notification.templateservice.validation.TemplateValidator;

import java.util.ArrayList;
import java.util.List;

public class BodyLengthValidator implements TemplateValidator {

    private final int minLength;
    private final int maxLength;
    private final String validatorLabel;

    public BodyLengthValidator(int minLength, int maxLength, String validatorLabel) {
        if (minLength < 0)          throw new IllegalArgumentException("minLength must be >= 0");
        if (maxLength <= minLength)  throw new IllegalArgumentException("maxLength must be > minLength");
        this.minLength      = minLength;
        this.maxLength      = maxLength;
        this.validatorLabel = validatorLabel;
    }

    @Override
    public ValidationResult validate(NotificationTemplate template) {
        String body = template.getBody();
        if (body == null || body.isBlank()) {
            // NotEmptyValidator handles blank body; skip to avoid duplicate errors.
            return ValidationResult.valid();
        }

        List<ValidationError> errors = new ArrayList<>();
        int length = body.trim().length();

        if (length < minLength) {
            errors.add(ValidationError.error("BODY_TOO_SHORT", "body")
                    .message("Body length " + length + " is below the minimum of " + minLength + " characters")
                    .build());
        }

        if (length > maxLength) {
            errors.add(ValidationError.error("BODY_TOO_LONG", "body")
                    .message("Body length " + length + " exceeds the maximum of " + maxLength + " characters")
                    .build());
        }

        // Warn if approaching the limit (within 10%)
        if (errors.isEmpty() && length > maxLength * 0.9) {
            errors.add(ValidationError.warning("BODY_NEAR_MAX_LENGTH", "body")
                    .message("Body length " + length + " is approaching the maximum of " + maxLength + " characters")
                    .build());
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.of(errors);
    }
}
