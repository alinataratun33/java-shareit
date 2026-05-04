package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleConflictException() {
        ConflictException exception = new ConflictException("Email уже существует");
        ErrorResponse response = errorHandler.handleConflictException(exception);

        assertEquals("Конфликт", response.getError());
        assertEquals("Email уже существует", response.getDescription());
    }

    @Test
    void handleValidationException() {
        ValidationException exception = new ValidationException("Имя не может быть пустым");
        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertEquals("Ошибка валидации", response.getError());
        assertEquals("Имя не может быть пустым", response.getDescription());
    }

    @Test
    void handleNotFoundException() {
        NotFoundException exception = new NotFoundException("Пользователь не найден");
        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertEquals("Объект не найден", response.getError());
        assertEquals("Пользователь не найден", response.getDescription());
    }

    @Test
    void handleForbiddenException() {
        ForbiddenException exception = new ForbiddenException("Только владелец может редактировать вещь");
        ErrorResponse response = errorHandler.handleForbiddenException(exception);

        assertEquals("Доступ запрещён", response.getError());
        assertEquals("Только владелец может редактировать вещь", response.getDescription());
    }

    @Test
    void handleThrowable() {
        Throwable exception = new RuntimeException("Непредвиденная ошибка");
        ErrorResponse response = errorHandler.handleThrowable(exception);

        assertEquals("Внутренняя ошибка сервера", response.getError());
        assertEquals("Непредвиденная ошибка", response.getDescription());
    }

    @Test
    void errorResponse() {
        ErrorResponse response = new ErrorResponse("Ошибка", "Подробное описание");

        assertEquals("Ошибка", response.getError());
        assertEquals("Подробное описание", response.getDescription());
    }
}

