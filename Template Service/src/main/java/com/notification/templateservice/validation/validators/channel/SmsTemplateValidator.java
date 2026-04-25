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
public class SmsTemplateValidator implements TemplateValidator {

    private static final int SMS_HARD_MAX          = 1600;

    private static final Pattern HTML_TAG = Pattern.compile("<[a-zA-Z][^>]*>");
    private static final Pattern NON_GSM7 = Pattern.compile(
            "[^\\x00-\\x7F@£$¥èéùìòÇ\\nØø\\rÅåΔ_ΦΓΛΩΠΨΣΘΞ" +
                    "ÆæßÉ !\"#¤%&'()*+,\\-./0-9:;<=>?¡A-ZÄÖÑÜ§¿a-zäöñüà]"
    );

    @Override
    public ValidationResult validate(NotificationTemplate template) {
        List<ValidationError> errors = new ArrayList<>();
        String body    = template.getBody();
        String subject = template.getSubject();


        if (subject != null && !subject.isBlank()) {
            errors.add(ValidationError.warning("SMS_SUBJECT_IGNORED", "subject")
                    .message("SMS templates do not use a subject field; it will be ignored during dispatch").build());
        }
        if (body.length() > SMS_HARD_MAX) {
            errors.add(ValidationError.error("SMS_BODY_TOO_LONG", "body")
                    .message("SMS body length " + body.length() + " exceeds hard limit of " + SMS_HARD_MAX + " characters").build());
        }
        if (NON_GSM7.matcher(body).find()) {
            int ucs2MaxSingle = 70;
            errors.add(ValidationError.warning("SMS_NON_GSM7_CHARSET", "body")
                    .message("Body contains non-GSM-7 characters; message will use UCS-2 encoding " +
                            "(max " + ucs2MaxSingle + " chars per segment instead of 160)").build());
        }
        if (HTML_TAG.matcher(body).find()) {
            errors.add(ValidationError.error("SMS_HTML_NOT_ALLOWED", "body")
                    .message("SMS body must not contain HTML markup; SMS is plain text only").build());
        }
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.of(errors);
    }
}
