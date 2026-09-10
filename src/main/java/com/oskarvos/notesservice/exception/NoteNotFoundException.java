package com.oskarvos.notesservice.exception;

import java.util.UUID;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(UUID id) {
        super("Заметка с id " + id + " не найдена");
    }
}