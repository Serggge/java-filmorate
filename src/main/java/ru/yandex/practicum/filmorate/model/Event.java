package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.time.Instant;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    Instant timestamp;
    long userId;
    EventType eventType;
    Operation operation;
    long eventId;
    long entityId;

    public String getEventType() {
        return eventType.name();
    }

    public String getOperation() {
        return operation.name();
    }

    public long getTimestamp() {
        return timestamp.toEpochMilli();
    }
}
