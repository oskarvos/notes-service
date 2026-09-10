package com.oskarvos.notesservice.service;

import com.oskarvos.notesservice.dto.NoteRequest;
import com.oskarvos.notesservice.dto.NoteResponse;
import com.oskarvos.notesservice.exception.NoteNotFoundException;
import com.oskarvos.notesservice.mapper.NoteMapper;
import com.oskarvos.notesservice.model.Note;
import com.oskarvos.notesservice.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoteServiceImpl implements NoteService {

    private final NoteRepository repository;
    private final NoteMapper mapper;

    @Override
    public NoteResponse createNote(NoteRequest request) {
        Note saved = repository.save(mapper.toNewEntity(request));
        log.info("Создана заметка id={}", saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public NoteResponse getNote(UUID id) {
        return mapper.toResponse(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoteResponse> listNotes(String tag, Pageable pageable) {
        Page<Note> notes = (tag == null || tag.isBlank())
                ? repository.findAll(pageable)
                : repository.findByTag(tag, pageable);
        return notes.map(mapper::toResponse);
    }

    @Override
    public NoteResponse updateNote(UUID id, NoteRequest request) {
        Note note = find(id);
        mapper.copyRequestInto(request, note);
        log.info("Обновлена заметка id={}", id);
        return mapper.toResponse(note);
    }

    @Override
    public void deleteNote(UUID id) {
        repository.delete(find(id));
        log.info("Удалена заметка id={}", id);
    }

    private Note find(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }
}