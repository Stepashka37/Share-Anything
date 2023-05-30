package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;


@Data
@Builder
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    @Column
    private  String name;

    @Column
    private  String description;

    @Column(name = "is_available")
    private  Boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "owner_id",
    referencedColumnName = "id")
    private User owner;

    @OneToOne
    @JoinColumn(name = "request_id",
    referencedColumnName = "id")
    private  ItemRequest request;


    public Item() {
    }

    public Item(long id, String name, String description, Boolean isAvailable, User owner, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.owner = owner;
        this.request = request;
    }
}
