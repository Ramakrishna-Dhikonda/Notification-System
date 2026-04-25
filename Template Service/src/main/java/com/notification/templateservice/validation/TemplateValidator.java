package com.notification.templateservice.validation;

import com.notification.templateservice.domain.NotificationTemplate;

@FunctionalInterface
public interface TemplateValidator {
    ValidationResult validate(NotificationTemplate template);
    default String getName() {
        return this.getClass().getSimpleName();
    }
}
