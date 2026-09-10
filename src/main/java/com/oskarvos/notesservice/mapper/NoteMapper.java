package com.oskarvos.notesservice.mapper;

import com.oskarvos.notesservice.dto.NoteRequest;
import com.oskarvos.notesservice.dto.NoteResponse;
import com.oskarvos.notesservice.model.Note;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class NoteMapper {

    public Note toNewEntity(NoteRequest request) {
        Note note = new Note();
        copyRequestInto(request, note);
        return note;
    }

    public void copyRequestInto(NoteRequest request, Note note) {
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.getTags().clear();
        if (request.getTags() != null) {
            note.getTags().addAll(request.getTags());
        }
    }

    public NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt(),
                Set.copyOf(note.getTags())
        );
    }
}