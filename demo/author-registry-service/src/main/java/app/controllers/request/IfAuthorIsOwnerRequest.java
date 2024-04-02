package app.controllers.request;

public record IfAuthorIsOwnerRequest (String firstName, String lastName, String bookName) {};
