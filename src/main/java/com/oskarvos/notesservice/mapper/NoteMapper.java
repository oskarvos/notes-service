package com.oskarvos.notesservice.mapper;

import com.oskarvos.notesservice.dto.NoteRequest;
import com.oskarvos.notesservice.dto.NoteResponse;
import com.oskarvos.notesservice.dto.TagDto;
import com.oskarvos.notesservice.model.Note;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            note.getTags().addAll(extractTags(request.getTags()));
        }
    }

    private Set<String> extractTags(List<TagDto> tagDtos) {
        return tagDtos.stream()
                .map(TagDto::value)
                .collect(Collectors.toSet());
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