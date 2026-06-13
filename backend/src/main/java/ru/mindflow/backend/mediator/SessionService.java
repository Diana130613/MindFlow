package ru.mindflow.backend.mediator;

import ru.mindflow.backend.dto.ProgressDto;
import ru.mindflow.backend.dto.SessionDto;
import ru.mindflow.backend.dto.SessionRequest;
import java.util.List;

public interface SessionService {
    SessionDto save(Long userId, SessionRequest request);
    List<SessionDto> getUserSessions(Long userId);
    ProgressDto getUserProgress(Long userId);
}
