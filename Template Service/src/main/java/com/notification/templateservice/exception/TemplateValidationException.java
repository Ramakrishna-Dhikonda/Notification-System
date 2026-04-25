package com.notification.templateservice.exception;

import com.notification.templateservice.validation.ValidationResult;

public class TemplateValidationException extends RuntimeException {

    private final ValidationResult validationResult;
    private final String templateId;

    public TemplateValidationException(String templateId, ValidationResult result) {
        super(buildMessage(templateId, result));
        this.templateId       = templateId;
        this.validationResult = result;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public String getTemplateId() {
        return templateId;
    }

    private static String buildMessage(String templateId, ValidationResult result) {
        return "Template '" + templateId + "' failed validation with " +
                result.errorCount() + " error(s) and " +
                result.warningCount() + " warning(s)";
    }
}