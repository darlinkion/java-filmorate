package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.model.type.EventType;
import ru.yandex.practicum.filmorate.model.type.OperationType;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @NotNull
    private Long timestamp;
    @Positive
    private int userId;
    @NotNull
    private EventType eventType;
    @NotNull
    private OperationType operation;
    @Positive
    private int eventId;
    @Positive
    private int entityId;

    public Event(Long timestamp, int userId, EventType eventType, OperationType operation, int entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
