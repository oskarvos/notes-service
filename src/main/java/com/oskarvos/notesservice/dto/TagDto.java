package com.oskarvos.notesservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagDto(
        @NotBlank(message = "Тег не может быть пустым")
        @Size(max = 50, message = "Тег не должен превышать 50 символов")
        String value
) {}
