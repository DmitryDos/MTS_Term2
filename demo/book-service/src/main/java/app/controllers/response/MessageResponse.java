package app.controllers.response;

public record MessageResponse(Long bookId, Boolean success, String message) {}