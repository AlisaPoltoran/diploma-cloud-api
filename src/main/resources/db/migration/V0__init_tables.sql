create table if not exists cloud.users
(
    id       SERIAL primary key,
    login    varchar(255) not null,
    password varchar(255) not null
);

create table if not exists cloud.files
(
    id                 SERIAL primary key,
    name               varchar(255) not null,
    creation_date_time TIMESTAMPTZ DEFAULT Now(),
    user_id            bigint       not null,
    size_in_bytes      int          not null,
    content            oid          not null,
    content_type       varchar(255) not null,
    FOREIGN KEY (user_id) references cloud.users (id)
);

drop type if exists cloud.role cascade;
create type role as enum ('USER', 'ADMIN');

alter table cloud.users
    add column if not exists role role;

alter table cloud.users
    alter role set default 'USER';

create table if not exists tokens
(
    id         SERIAL primary key,
    token      varchar(255) not null,
    token_type varchar(50),
    expired    boolean,
    revoked    boolean,
    user_id    int,
    FOREIGN KEY (user_id) references cloud.users (id)
);