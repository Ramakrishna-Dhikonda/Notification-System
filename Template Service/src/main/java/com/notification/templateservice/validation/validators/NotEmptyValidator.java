package com.notification.templateservice.validation.validators;

import com.notification.templateservice.domain.NotificationTemplate;
import com.notification.templateservice.validation.ValidationError;
import com.notification.templateservice.validation.ValidationResult;
import com.notification.templateservice.validation.TemplateValidator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotEmptyValidator implements TemplateValidator {

    @Override
    public ValidationResult validate(NotificationTemplate template) {
        List<ValidationError> errors = new ArrayList<>();

        if (isBlank(template.getId())) {
            errors.add(ValidationError.error("TEMPLATE_ID_BLANK", "id")
                    .message("Template id must not be blank").build());
        }

        if (isBlank(template.getName())) {
            errors.add(ValidationError.error("TEMPLATE_NAME_BLANK", "name")
                    .message("Template name must not be blank").build());
        }

        if (template.getChannel() == null) {
            errors.add(ValidationError.error("TEMPLATE_CHANNEL_NULL", "channel")
                    .message("Template channel must not be null").build());
        }

        if (isBlank(template.getBody())) {
            errors.add(ValidationError.error("TEMPLATE_BODY_BLANK", "body")
                    .message("Template body must not be blank").build());
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.of(errors);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}