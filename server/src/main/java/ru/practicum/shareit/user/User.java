package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;


@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Size(max = 50)
    private String name;

    @Column
    @Size(max = 50)
    private String email;

    public User() {
    }


    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }


}
