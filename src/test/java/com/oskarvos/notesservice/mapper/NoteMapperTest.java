package com.oskarvos.notesservice.mapper;

import com.oskarvos.notesservice.dto.NoteRequest;
import com.oskarvos.notesservice.dto.NoteResponse;
import com.oskarvos.notesservice.model.Note;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NoteMapperTest {

    private final NoteMapper mapper = new NoteMapper();

    @Test
    @DisplayName("toEntity() переносит title, content и tags")
    void toEntity_shouldCopyFields() {
        NoteRequest request = new NoteRequest();
        request.setTitle("T");
        request.setContent("C");
        request.setTags(Set.of("a", "b"));

        Note note = mapper.toEntity(request);

        assertThat(note.getTitle()).isEqualTo("T");
        assertThat(note.getContent()).isEqualTo("C");
        assertThat(note.getTags()).containsExactlyInAnyOrder("a", "b");
    }

    @Test
    @DisplayName("toEntity() с tags == null даёт пустой Set")
    void toEntity_shouldHandleNullTags() {
        NoteRequest request = new NoteRequest();
        request.setTitle("T");
        request.setContent("C");
        request.setTags(null);

        Note note = mapper.toEntity(request);

        assertThat(note.getTags()).isEmpty();
    }

    @Test
    @DisplayName("apply() заменяет существующие теги, а не добавляет")
    void apply_shouldReplaceTags() {
        Note note = new Note();
        note.setTitle("old");
        note.getTags().add("old-tag");

        NoteRequest request = new NoteRequest();
        request.setTitle("new");
        request.setContent("c");
        request.setTags(Set.of("new-tag"));

        mapper.apply(request, note);

        assertThat(note.getTitle()).isEqualTo("new");
        assertThat(note.getTags()).containsExactly("new-tag");
    }

    @Test
    @DisplayName("apply() очищает теги, если tags == null")
    void apply_shouldClearTagsWhenNull() {
        Note note = new Note();
        note.getTags().add("old-tag");

        NoteRequest request = new NoteRequest();
        request.setTitle("t");
        request.setContent("c");
        request.setTags(null);

        mapper.apply(request, note);

        assertThat(note.getTags()).isEmpty();
    }

    @Test
    @DisplayName("toResponse() копирует поля и не отдаёт внутренний Set")
    void toResponse_shouldDefensivelyCopyTags() {
        Note note = new Note();
        note.setTitle("Title");
        note.setContent("Content");
        note.getTags().add("work");

        NoteResponse response = mapper.toResponse(note);

        assertThat(response.id()).isEqualTo(note.getId());
        assertThat(response.title()).isEqualTo("Title");
        assertThat(response.content()).isEqualTo("Content");
        assertThat(response.tags()).containsExactly("work");

        note.getTags().add("hacked");
        assertThat(response.tags()).containsExactly("work");
    }

    @Test
    @DisplayName("toResponse() для заметки без тегов возвращает пустой Set")
    void toResponse_shouldReturnEmptyTags() {
        Note note = new Note();
        note.setTitle("Title");
        note.setContent("Content");

        NoteResponse response = mapper.toResponse(note);

        assertThat(response.tags()).isEmpty();
    }
}