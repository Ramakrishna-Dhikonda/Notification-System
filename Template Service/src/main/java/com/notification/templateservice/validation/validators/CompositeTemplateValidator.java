package com.notification.templateservice.validation.validators;

import com.notification.templateservice.domain.NotificationTemplate;
import com.notification.templateservice.validation.TemplateValidator;
import com.notification.templateservice.validation.ValidationResult;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public final class CompositeTemplateValidator implements TemplateValidator {

    private final String name;
    private final List<TemplateValidator> validators;

    private CompositeTemplateValidator(String name, List<TemplateValidator> validators) {
        this.name = Objects.requireNonNull(name, "Composite name must not be null");
        this.validators = Collections.unmodifiableList(new ArrayList<>(validators));
    }

    @Override
    public ValidationResult validate(NotificationTemplate template) {
        return validators.stream()
                .map(v -> v.validate(template))
                .reduce(ValidationResult.valid(), ValidationResult::merge);
    }

    public static Builder named(String name) {
        return new Builder(name);
    }

    public static final class Builder {
        private final String name;
        private final List<TemplateValidator> validators = new ArrayList<>();

        private Builder(String name) {
            this.name = name;
        }

        public Builder add(TemplateValidator validator) {
            validators.add(Objects.requireNonNull(validator, "Validator must not be null"));
            return this;
        }

        public Builder addAll(List<TemplateValidator> list) {
            list.forEach(this::add);
            return this;
        }

        public CompositeTemplateValidator build() {
            if (validators.isEmpty()) {
                throw new IllegalStateException("Composite '" + name + "' must have at least one validator");
            }
            return new CompositeTemplateValidator(name, validators);
        }
    }
}
