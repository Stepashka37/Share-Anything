package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
public class UserDto {

    UserDto(long id, @NotBlank(groups = {New.class}) String name, @NotBlank(groups = {New.class}) @Email(groups = {New.class, Update.class}) String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public interface New {
    }

    public interface Exist {
    }

    public interface Update extends Exist {
    }


    private long id;

    @NotBlank(groups = {New.class})
    @Size(max = 50)
    private String name;

    @NotBlank(groups = {New.class})
    @Email(groups = {New.class, Update.class})
    @Size(max = 50)
    private String email;

}
