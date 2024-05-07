package app.controllers.response;

import app.entities.PaymentStatus;

public record BookStatusResponse(Long bookId, PaymentStatus status, String messageId) { }
