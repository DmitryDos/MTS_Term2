package app.controllers.response;

import app.entities.Author;
import app.entities.Tag;

import java.util.Set;

public record FindBookResponse(Long id, String title, Author author, Set<Tag> tags) {}