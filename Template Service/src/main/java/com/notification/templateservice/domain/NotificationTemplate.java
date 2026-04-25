package com.notification.templateservice.domain;


import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Builder
@Getter
public class NotificationTemplate {
    private final String id;
    private final String name;
    private final TemplateChannel channel;
    private final String subject;       // Optional: only for EMAIL
    private final String body;
    private final Set<String> requiredPlaceholders;  // e.g. {{name}}, {{otp}}
    private final Map<String, String> metadata;
    private final String locale;
    private final int version;

}
