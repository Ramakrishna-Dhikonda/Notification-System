package com.notification.templateservice.service;

import com.notification.templateservice.domain.NotificationTemplate;
import com.notification.templateservice.domain.TemplateChannel;
import com.notification.templateservice.validation.ValidationResult;

import java.util.List;

public interface TemplateValidationService {

    ValidationResult validate(NotificationTemplate template);

    List<ValidationResult> validateBatch(List<NotificationTemplate> templates);

    ValidationResult validateForChannel(NotificationTemplate template, TemplateChannel channel);
}
