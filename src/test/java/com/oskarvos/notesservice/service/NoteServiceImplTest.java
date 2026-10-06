package com.oskarvos.notesservice.service;

import com.oskarvos.notesservice.dto.NoteRequest;
import com.oskarvos.notesservice.dto.NoteResponse;
import com.oskarvos.notesservice.dto.TagDto;
import com.oskarvos.notesservice.exception.NoteNotFoundException;
import com.oskarvos.notesservice.mapper.NoteMapper;
import com.oskarvos.notesservice.model.Note;
import com.oskarvos.notesservice.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository repository;

    @Mock
    private NoteMapper mapper;

    @InjectMocks
    private NoteServiceImpl service;

    private Note sample;

    @BeforeEach
    void setUp() {
        sample = new Note();
        sample.setTitle("Title");
        sample.setContent("Content");
        sample.getTags().add("work");
    }

    @Test
    @DisplayName("create() сохраняет заметку через репозиторий")
    void create_shouldSaveThroughRepository() {
        NoteRequest request = new NoteRequest();
        request.setTitle("T");
        request.setContent("C");
        request.setTags(List.of(new TagDto("work")));

        when(mapper.toNewEntity(request)).thenReturn(sample);
        when(repository.save(sample)).thenReturn(sample);
        when(mapper.toResponse(sample)).thenReturn(
                new NoteResponse(sample.getId(), "T", "C", sample.getCreatedAt(), Set.of("work")));

        NoteResponse result = service.createNote(request);

        assertThat(result.title()).isEqualTo("T");
        verify(mapper).toNewEntity(request);
        verify(repository).save(sample);
        verify(mapper).toResponse(sample);
    }

    @Test
    @DisplayName("create() принимает null вместо tags")
    void create_shouldAcceptNullTags() {
        NoteRequest request = new NoteRequest();
        request.setTitle("T");
        request.setContent("C");
        request.setTags(null);

        when(mapper.toNewEntity(request)).thenReturn(sample);
        when(repository.save(sample)).thenReturn(sample);
        when(mapper.toResponse(sample)).thenReturn(
                new NoteResponse(sample.getId(), "T", "C", sample.getCreatedAt(), Set.of()));

        NoteResponse result = service.createNote(request);

        assertThat(result.tags()).isEmpty();
        verify(repository).save(sample);
    }

    @Test
    @DisplayName("getById() возвращает заметку, если она есть")
    void getById_shouldReturnNote() {
        when(repository.findById(sample.getId())).thenReturn(Optional.of(sample));
        when(mapper.toResponse(sample)).thenReturn(
                new NoteResponse(sample.getId(), "Title", "Content", sample.getCreatedAt(), Set.of("work")));

        NoteResponse result = service.getNote(sample.getId());

        assertThat(result.id()).isEqualTo(sample.getId());
        assertThat(result.title()).isEqualTo("Title");
    }

    @Test
    @DisplayName("getById() бросает NoteNotFoundException")
    void getById_shouldThrowWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getNote(id))
                .isInstanceOf(NoteNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    @DisplayName("getAll(tag) вызывает findByTag()")
    void getAll_withTag_shouldCallFindByTag() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findByTag("work", pageable)).thenReturn(new PageImpl<>(List.of(sample)));
        when(mapper.toResponse(sample)).thenReturn(
                new NoteResponse(sample.getId(), "Title", "Content", sample.getCreatedAt(), Set.of("work")));

        Page<NoteResponse> result = service.listNotes("work", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(repository).findByTag("work", pageable);
        verify(repository, never()).findAll(pageable);
    }

    @Test
    @DisplayName("getAll(null) вызывает findAll()")
    void getAll_withoutTag_shouldCallFindAll() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(sample)));
        when(mapper.toResponse(sample)).thenReturn(
                new NoteResponse(sample.getId(), "Title", "Content", sample.getCreatedAt(), Set.of("work")));

        Page<NoteResponse> result = service.listNotes(null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(repository).findAll(pageable);
        verify(repository, never()).findByTag(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("getAll(\"\") вызывает findAll()")
    void getAll_withEmptyTag_shouldCallFindAll() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(sample)));
        when(mapper.toResponse(sample)).thenReturn(
                new NoteResponse(sample.getId(), "Title", "Content", sample.getCreatedAt(), Set.of("work")));

        service.listNotes("", pageable);

        verify(repository).findAll(pageable);
        verify(repository, never()).findByTag(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("getAll(\"  \") вызывает findAll()")
    void getAll_withBlankTag_shouldCallFindAll() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(sample)));
        when(mapper.toResponse(sample)).thenReturn(
                new NoteResponse(sample.getId(), "Title", "Content", sample.getCreatedAt(), Set.of("work")));

        service.listNotes("   ", pageable);

        verify(repository).findAll(pageable);
        verify(repository, never()).findByTag(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("update() накатывает request на найденную заметку")
    void update_shouldApplyRequest() {
        UUID id = sample.getId();
        NoteRequest request = new NoteRequest();
        request.setTitle("New");
        request.setContent("New content");

        when(repository.findById(id)).thenReturn(Optional.of(sample));
        when(mapper.toResponse(sample)).thenReturn(
                new NoteResponse(id, "New", "New content", sample.getCreatedAt(), Set.of()));

        NoteResponse result = service.updateNote(id, request);

        assertThat(result.title()).isEqualTo("New");
        verify(mapper).copyRequestInto(request, sample);
        verify(mapper).toResponse(sample);
    }

    @Test
    @DisplayName("update() бросает NoteNotFoundException")
    void update_shouldThrowWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        NoteRequest request = new NoteRequest();
        request.setTitle("T");
        request.setContent("C");

        assertThatThrownBy(() -> service.updateNote(id, request))
                .isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    @DisplayName("delete() удаляет найденную заметку")
    void delete_shouldRemoveExistingNote() {
        UUID id = sample.getId();
        when(repository.findById(id)).thenReturn(Optional.of(sample));

        service.deleteNote(id);

        verify(repository).delete(sample);
    }

    @Test
    @DisplayName("delete() бросает NoteNotFoundException")
    void delete_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteNote(id))
                .isInstanceOf(NoteNotFoundException.class);
    }
}