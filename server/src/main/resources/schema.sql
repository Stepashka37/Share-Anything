drop table if exists items, bookings, users,  requests, comments cascade ;
drop type if exists booking_status cascade;

create type  booking_status as enum ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');


create table if not exists users
(
    id    serial
        constraint users_pk
            primary key,
    name  varchar(50) not null,
    email varchar(50) not null
        unique
);



create unique index if not exists users_email_uindex
    on users (email);

create unique index if not exists users_id_uindex
    on users (id);

create table if not exists requests
(
    id           serial
        constraint requests_pk
            primary key,
    description  varchar(500) not null,
    requestor_id integer      not null
        constraint requests_users_id_fk
            references users,
    created      timestamp
);



create unique index if not exists requests_id_uindex
    on requests (id);

create table if not exists items
(
    id           serial
        constraint items_pk
            primary key,
    name         varchar(50) not null,
    description  varchar(500),
    is_available boolean     not null,
    owner_id     integer     not null
        constraint items_users_id_fk
            references users
            on delete cascade,
    request_id   integer
        constraint items_requests_id_fk
            references requests
            on delete cascade
);



create unique index if not exists items_id_uindex
    on items (id);

create table if not exists bookings
(
    id         serial
        constraint bookings_pk
            primary key,
    start_date timestamp with time zone not null,
    end_date   timestamp with time zone not null,
    item_id    integer                  not null
        constraint bookings_items_id_fk
            references items
            on delete cascade,
    booker_id  integer                  not null
        constraint bookings_users_id_fk
            references users
            on delete cascade,
    status     varchar(50)              not null
);



create unique index if not exists bookings_id_uindex
    on bookings (id);

create table if not exists comments
(
    id        serial
        constraint comments_pk
            primary key,
    text      varchar(500) not null,
    item_id   integer      not null
        constraint comments_items_id_fk
            references items
            on delete cascade,
    author_id integer      not null
        constraint comments_users_id_fk
            references users
            on delete cascade,
    created   timestamp    not null
);


create unique index if not exists comments_id_uindex
    on comments (id);



/*create table if not exists users(
    id  bigint generated always as identity primary key,
    name  varchar(50) not null,
    email varchar(50) not null unique
);


create table if not exists  requests (
    id bigint generated always as identity primary key,
    description  varchar(500) not null,
    requestor_id bigint not null,
    created      timestamp,
        constraint fk_requests_to_users foreign key(requestor_id)
        references users(id)

);


create table if not exists  items(
    id bigint generated always as identity primary key,
    name         varchar(50) not null,
    description  varchar(500),
    is_available boolean     not null,
    owner_id     integer     not null,
    request_id   integer,
        constraint fk_items_users foreign key(owner_id)
        references users(id) on delete cascade,
        constraint fk_items_requests foreign key(request_id)
        references requests(id) on delete cascade
);


create type  booking_status as enum ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');


create table if not exists  bookings(
    id bigint generated always as identity primary key,
    start_date timestamp with time zone not null,
    end_date   timestamp with time zone not null,
    item_id    integer   not null,
    booker_id  integer   not null,
    status     varchar(50)   not null,
    constraint fk_bookings_items foreign key(item_id)
        references items(id) on delete cascade,
    constraint fk_bookings_users foreign key(booker_id)
        references users(id) on delete cascade
);


create table if not exists  comments (
    id bigint generated always as identity primary key,
    text      varchar(1000) not null,
    item_id   integer not null,
    author_id integer not null,
    created timestamp not null,
    constraint fk_comments_items foreign key(item_id)
        references items(id) on delete cascade,
    constraint fk_comments_users foreign key(author_id)
        references users(id) on delete cascade
);*/
