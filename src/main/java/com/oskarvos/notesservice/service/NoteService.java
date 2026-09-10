package com.oskarvos.notesservice.service;

import com.oskarvos.notesservice.dto.NoteRequest;
import com.oskarvos.notesservice.dto.NoteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NoteService {

    NoteResponse createNote(NoteRequest request);

    NoteResponse getNote(UUID id);

    Page<NoteResponse> listNotes(String tag, Pageable pageable);

    NoteResponse updateNote(UUID id, NoteRequest request);

    void deleteNote(UUID id);
}