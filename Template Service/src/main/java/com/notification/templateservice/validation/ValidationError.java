package com.notification.templateservice.validation;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@Builder(toBuilder = true)
public final class ValidationError {
    private final String errorCode;
    private final String field;
    private final String message;
    private final ValidationSeverity severity;

    public ValidationError(
            String errorCode,
            String field,
            String message,
            ValidationSeverity severity
    ) {
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
        this.field = Objects.requireNonNull(field, "field must not be null");
        this.message = Objects.requireNonNull(message, "message must not be null");
        this.severity = Objects.requireNonNull(severity, "severity must not be null");
    }

    public boolean isError() {
        return severity == ValidationSeverity.ERROR;
    }

    public boolean isWarning() {
        return severity == ValidationSeverity.WARNING;
    }

    public static ValidationErrorBuilder error(String errorCode, String field) {
        return ValidationError.builder()
                .errorCode(errorCode)
                .field(field)
                .severity(ValidationSeverity.ERROR);
    }

    public static ValidationErrorBuilder warning(String errorCode, String field) {
        return ValidationError.builder()
                .errorCode(errorCode)
                .field(field)
                .severity(ValidationSeverity.WARNING);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationError that)) return false;
        return Objects.equals(errorCode, that.errorCode)
                && Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorCode, field);
    }
}
