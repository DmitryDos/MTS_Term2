package app.service.response;

public record MessageResponse(Long bookId, Boolean success, String message) {}