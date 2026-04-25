package com.notification.templateservice.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ValidationResult {

    private final List<ValidationError> errors;

    private ValidationResult(List<ValidationError> errors) {
        this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
    }

    public static ValidationResult valid() {
        return new ValidationResult(Collections.emptyList());
    }

    public static ValidationResult of(List<ValidationError> errors) {
        return new ValidationResult(errors);
    }

    public ValidationResult merge(ValidationResult other) {
        List<ValidationError> combined = new ArrayList<>(this.errors);
        combined.addAll(other.errors);
        return new ValidationResult(combined);
    }

    public boolean isValid() {
        return errors.stream().noneMatch(ValidationError::isError);
    }

    public boolean hasWarnings() {
        return errors.stream().anyMatch(ValidationError::isWarning);
    }

    public List<ValidationError> getAllErrors() {
        return errors;
    }

    public List<ValidationError> getErrors() {
        return errors.stream()
                .filter(ValidationError::isError)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<ValidationError> getWarnings() {
        return errors.stream()
                .filter(ValidationError::isWarning)
                .collect(Collectors.toUnmodifiableList());
    }

    public int errorCount()   { return (int) errors.stream().filter(ValidationError::isError).count(); }
    public int warningCount() { return (int) errors.stream().filter(ValidationError::isWarning).count(); }

    @Override
    public String toString() {
        return "ValidationResult{valid=" + isValid() +
                ", errors=" + errorCount() +
                ", warnings=" + warningCount() + "}";
    }
}