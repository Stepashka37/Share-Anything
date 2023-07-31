package ru.practicum.shareit.request.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class ItemRequestDtoToCreate {

    @NotBlank
    private String description;

    public ItemRequestDtoToCreate(String description) {
        this.description = description;
    }

    public ItemRequestDtoToCreate() {
    }
}
