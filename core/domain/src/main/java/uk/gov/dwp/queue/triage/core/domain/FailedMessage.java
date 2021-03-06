package uk.gov.dwp.queue.triage.core.domain;

import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class FailedMessage {

    private final FailedMessageId failedMessageId;
    private final String jmsMessageId;
    private final Destination destination;
    private final Instant sentAt;
    private final Instant failedAt;
    private final String content;
    private final Map<String, Object> properties;
    private final StatusHistoryEvent statusHistoryEvent;
    private final Set<String> labels;

    FailedMessage(FailedMessageId failedMessageId,
                  String jmsMessageId, Destination destination,
                  Instant sentAt,
                  Instant failedAt,
                  String content,
                  Map<String, Object> properties,
                  StatusHistoryEvent statusHistoryEvent,
                  Set<String> labels) {
        this.failedMessageId = failedMessageId;
        this.jmsMessageId = jmsMessageId;
        this.destination = destination;
        this.sentAt = sentAt;
        this.failedAt = failedAt;
        this.content = content;
        this.properties = properties;
        this.statusHistoryEvent = statusHistoryEvent;
        this.labels = labels;
    }

    public FailedMessageId getFailedMessageId() {
        return failedMessageId;
    }

    public String getJmsMessageId() {
        return jmsMessageId;
    }

    public Destination getDestination() {
        return destination;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public String getContent() {
        return content;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public <T> T getProperty(String name) {
        return (T)properties.get(name);
    }

    public Set<String> getLabels() {
        return new HashSet<>(labels);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    public StatusHistoryEvent getStatusHistoryEvent() {
        return statusHistoryEvent;
    }

    public StatusHistoryEvent.Status getStatus() {
        return statusHistoryEvent.getStatus();
    }
}
