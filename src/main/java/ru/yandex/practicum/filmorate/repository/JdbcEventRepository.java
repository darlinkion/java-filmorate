package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.type.EventType;
import ru.yandex.practicum.filmorate.model.type.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcEventRepository {

    private final JdbcTemplate jdbc;

    public void addEvent(Event event) {
        String sql = "INSERT INTO EVENTS (TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";
        jdbc.update(sql, event.getTimestamp(), event.getUserId(), event.getEventType().toString(),
                event.getOperation().toString(), event.getEntityId());
        log.info("Добавлено новое событие --> {}", event);
    }

    public List<Event> getUserEvents(int userId) {
        String sql = "SELECT * FROM EVENTS WHERE USER_ID = ?";
        return jdbc.query(sql, this::mapRowToEvent, userId);
    }

    private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        return new Event(
                rs.getLong("TIMESTAMP"),
                rs.getInt("USER_ID"),
                EventType.valueOf(rs.getString("EVENT_TYPE")),
                OperationType.valueOf(rs.getString("OPERATION")),
                rs.getInt("EVENT_ID"),
                rs.getInt("ENTITY_ID")
        );
    }
}
