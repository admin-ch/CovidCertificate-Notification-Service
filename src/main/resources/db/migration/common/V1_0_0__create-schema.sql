create table notifications
(
    id                 uuid      not null primary key,
    content            varchar   not null,
    creation_date_time timestamp not null default now()
);