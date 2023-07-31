package ru.practicum.shareit.item;

public class CommentMapper {

    public static Comment dtoToModel(CommentDtoToCreate dtoToCreate) {
        return Comment.builder()
                .text(dtoToCreate.getText())
                .build();
    }

    public static CommentDtoToReturn modelToDto(Comment comment) {
        return CommentDtoToReturn.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getUser().getName())
                .created(comment.getCreated())
                .build();
    }
}
