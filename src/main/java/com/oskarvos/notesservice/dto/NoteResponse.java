package com.oskarvos.notesservice.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record NoteResponse(UUID id, String title, String content, Instant createdAt, Set<String> tags) {
}