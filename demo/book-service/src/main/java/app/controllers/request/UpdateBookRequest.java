package app.controllers.request;

import app.entities.Tag;

import java.util.Set;

public record UpdateBookRequest (Long authorId, String title, Set<Tag> tags){}
