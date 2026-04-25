package com.notification.templateservice.validation.validators;

import com.notification.templateservice.domain.NotificationTemplate;
import com.notification.templateservice.validation.TemplateValidator;
import com.notification.templateservice.validation.ValidationError;
import com.notification.templateservice.validation.ValidationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PlaceholderSyntaxValidator implements TemplateValidator {

    /** Matches the correct form: {{word_characters}} */
    private static final Pattern VALID_PLACEHOLDER = Pattern.compile("\\{\\{(\\w+)\\}\\}");
    /**
     * Matches anything that looks like a placeholder attempt but is malformed.
     * E.g.: {{ }}, {{}}, { name }, {name}, {{123 abc}}
     */
    private static final Pattern MALFORMED_PLACEHOLDER = Pattern.compile("\\{\\{\\s*\\}\\}|\\{\\{\\s+\\w*\\s*\\}\\}|\\{\\w+\\}");

    @Override
    public ValidationResult validate(NotificationTemplate template) {
        List<ValidationError> errors = new ArrayList<>();

        String body    = template.getBody();
        String subject = template.getSubject(); // may be null for non-email channels

        // 1. Check for malformed placeholder patterns in body
        checkMalformedPlaceholders(body, "body", errors);

        // 2. Check for malformed placeholder patterns in subject
        if (subject != null && !subject.isBlank()) {
            checkMalformedPlaceholders(subject, "subject", errors);
        }

        // 3. Extract all valid placeholders used in body (and subject)
        Set<String> usedInBody    = extractPlaceholders(body);
        Set<String> usedInSubject = subject != null ? extractPlaceholders(subject) : Set.of();
        Set<String> allUsed       = new HashSet<>(usedInBody);
        allUsed.addAll(usedInSubject);

        // 4. Warn if declared required placeholders are never referenced
        Set<String> declared = template.getRequiredPlaceholders();
        for (String required : declared) {
            if (!allUsed.contains(required)) {
                errors.add(ValidationError.warning("PLACEHOLDER_NOT_USED", "body")
                        .message("Declared required placeholder '{{" + required + "}}' is not referenced in body or subject")
                        .build());
            }
        }

        // 5. Warn if body uses placeholders not declared in requiredPlaceholders
        if (!declared.isEmpty()) {
            for (String used : allUsed) {
                if (!declared.contains(used)) {
                    errors.add(ValidationError.warning("UNDECLARED_PLACEHOLDER", "body")
                            .message("Placeholder '{{" + used + "}}' is used in the template but not declared in requiredPlaceholders")
                            .build());
                }
            }
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.of(errors);
    }

    private void checkMalformedPlaceholders(String content, String field, List<ValidationError> errors) {
        if (content == null) return;
        Matcher m = MALFORMED_PLACEHOLDER.matcher(content);
        while (m.find()) {
            errors.add(ValidationError.error("MALFORMED_PLACEHOLDER", field)
                    .message("Malformed placeholder found: '" + m.group() + "'. Use {{identifier}} format")
                    .build());
        }
    }

    /** Returns the set of placeholder names (without braces) found in the content. */
    public static Set<String> extractPlaceholders(String content) {
        if (content == null || content.isBlank()) return Set.of();
        Set<String> placeholders = new HashSet<>();
        Matcher m = VALID_PLACEHOLDER.matcher(content);
        while (m.find()) {
            placeholders.add(m.group(1));
        }
        return placeholders;
    }
}