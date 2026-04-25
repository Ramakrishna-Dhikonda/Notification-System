package com.notification.templateservice.validation.validators;

import com.notification.templateservice.domain.TemplateChannel;
import com.notification.templateservice.validation.validators.channel.*;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class TemplateValidatorFactory {
    private Map<TemplateChannel, CompositeTemplateValidator> cache;

    public CompositeTemplateValidator getValidator(TemplateChannel channel) {
        CompositeTemplateValidator validator = cache.get(channel);
        if (validator == null)
            throw new IllegalArgumentException("No validator registered for channel: " + channel + ". Register a composite in TemplateValidatorFactory.");
        return validator;
    }

    private Map<TemplateChannel, CompositeTemplateValidator> buildCache(
            NotEmptyValidator notEmpty,
            PlaceholderSyntaxValidator placeholderSyntax,
            EmailTemplateValidator email,
            SmsTemplateValidator sms
    ) {
        Map<TemplateChannel, CompositeTemplateValidator> cache = new EnumMap<>(TemplateChannel.class);
        cache.put(TemplateChannel.EMAIL,
                CompositeTemplateValidator.named("EmailCompositeValidator")
                        .add(notEmpty)
                        .add(placeholderSyntax)
                        .add(new BodyLengthValidator(10, 102_400, "EmailBodyLengthValidator"))
                        .add(email)
                        .build()
        );
        cache.put(TemplateChannel.SMS,
                CompositeTemplateValidator.named("SmsCompositeValidator")
                        .add(notEmpty)
                        .add(placeholderSyntax)
                        .add(new BodyLengthValidator(1, 1600, "SmsBodyLengthValidator"))
                        .add(sms)
                        .build()
        );
        return cache;
    }
}
