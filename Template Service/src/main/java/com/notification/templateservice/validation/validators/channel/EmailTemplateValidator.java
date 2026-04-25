package com.notification.templateservice.validation.validators.channel;

import com.notification.templateservice.domain.NotificationTemplate;
import com.notification.templateservice.validation.TemplateValidator;
import com.notification.templateservice.validation.ValidationError;
import com.notification.templateservice.validation.ValidationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class EmailTemplateValidator implements TemplateValidator {

    private static final int SUBJECT_MIN_LENGTH = 3;
    private static final int SUBJECT_MAX_LENGTH = 150;

    private static final Pattern NEWLINE_IN_SUBJECT = Pattern.compile("[\\r\\n]");
    private static final Pattern HTML_TAG = Pattern.compile("<[a-zA-Z][^>]*>");
    private static final Pattern ALL_CAPS_WORD = Pattern.compile("\\b[A-Z]{3,}\\b");
    private static final Pattern EXCESSIVE_PUNCTUATION = Pattern.compile("[!?]{2,}");

    @Override
    public ValidationResult validate(NotificationTemplate template) {
        List<ValidationError> errors = new ArrayList<>();
        String subject = template.getSubject();
        String body    = template.getBody();

        if (subject == null || subject.isBlank()) {
            errors.add(ValidationError.error("EMAIL_SUBJECT_REQUIRED", "subject")
                    .message("Email templates must include a non-blank subject line").build());
        } else {
            int len = subject.trim().length();

            if (len < SUBJECT_MIN_LENGTH) {
                errors.add(ValidationError.error("EMAIL_SUBJECT_TOO_SHORT", "subject")
                        .message("Email subject must be at least " + SUBJECT_MIN_LENGTH + " characters; found " + len).build());
            }

            if (len > SUBJECT_MAX_LENGTH) {
                errors.add(ValidationError.error("EMAIL_SUBJECT_TOO_LONG", "subject")
                        .message("Email subject must not exceed " + SUBJECT_MAX_LENGTH + " characters; found " + len).build());
            }

            if (NEWLINE_IN_SUBJECT.matcher(subject).find()) {
                errors.add(ValidationError.error("EMAIL_SUBJECT_HEADER_INJECTION", "subject")
                        .message("Email subject contains newline characters — potential header injection").build());
            }

            if (ALL_CAPS_WORD.matcher(subject).find()) {
                errors.add(ValidationError.warning("EMAIL_SUBJECT_ALL_CAPS", "subject")
                        .message("Email subject contains ALL-CAPS words which may trigger spam filters").build());
            }

            if (EXCESSIVE_PUNCTUATION.matcher(subject).find()) {
                errors.add(ValidationError.warning("EMAIL_SUBJECT_EXCESSIVE_PUNCTUATION", "subject")
                        .message("Email subject contains repeated punctuation (!! or ??) which may trigger spam filters").build());
            }
        }

        if (body != null && HTML_TAG.matcher(body).find()) {
            // HTML template: warn if no unsubscribe hint
            String bodyLower = body.toLowerCase();
            boolean hasUnsubscribeHint = bodyLower.contains("unsubscribe")
                    || bodyLower.contains("opt-out")
                    || bodyLower.contains("opt out");

            if (!hasUnsubscribeHint) {
                errors.add(ValidationError.warning("EMAIL_MISSING_UNSUBSCRIBE", "body")
                        .message("HTML email body should include an unsubscribe link or opt-out instructions (CAN-SPAM / GDPR)").build());
            }
            // Warn if there's no plain-text fallback hint
            boolean hasAltText = bodyLower.contains("<alt") || bodyLower.contains("alt=");
            if (!hasAltText) {
                errors.add(ValidationError.warning("EMAIL_MISSING_IMG_ALT", "body")
                        .message("HTML email body should include alt attributes on images for accessibility").build());
            }
        }
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.of(errors);
    }
}
