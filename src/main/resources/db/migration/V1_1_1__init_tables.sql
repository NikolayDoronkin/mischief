create table if not exists "user"
(
    id                              uuid                    primary key,
    first_name                      varchar(100)            not null,
    last_name                       varchar(100)            not null,
    login                           varchar(50)             not null,
    password                        varchar(100)            not null,
    created                         timestamp               not null        default now(),
    deleted                         timestamp,
    user_role                       varchar(30)             not null
);

create table if not exists project
(
    id                               uuid                    primary key,
    name                             varchar(100)            not null,
    short_name                       varchar(100)            not null,
    description                      varchar(50)             not null,
    created                          timestamp               not null        default now(),
    deleted                          timestamp
);

create table if not exists user_m2m_project
(
    fk_user                          uuid                                   references "user"(id),
    fk_project                       uuid                                   references project(id)
);

create table if not exists ticket
(
    id                                uuid                    primary key,
    number                            integer                 not null,
    title                             varchar(100)            not null,
    description                       varchar(50)             not null,
    fk_assignee                       uuid                    not null        references "user"(id),
    fk_reporter                       uuid                    not null        references "user"(id),
    created                           timestamp               not null        default now(),
    deleted                           timestamp,
    relatable_finished_date           timestamp,
    priority                          integer                 not null        default 100,
    status                            varchar(50)             not null        default 'OPEN',
    type                              varchar(50)             not null        default 'SPIKE',
    fk_parent                         uuid                    not null        references ticket(id)
);

create table if not exists user_m2m_ticket_access
(
    fk_user                          uuid                                   references "user"(id),
    fk_ticket                        uuid                                   references ticket(id)
);

create table if not exists user_m2m_ticket_listener
(
    fk_user                          uuid                                   references "user"(id),
    fk_ticket                        uuid                                   references ticket(id)
);

create table if not exists notification
(
    id                                uuid                    primary key,
    template                          varchar(20)             not null,
    fk_author                         uuid                                    references "user"(id),
    fk_related_ticket                 uuid                    not null        references ticket(id),
    created                           timestamp               not null        default now()
);

create table if not exists user_m2m_notification
(
    fk_notification                   uuid                                   references notification(id),
    fk_user                           uuid                                   references "user"(id)
);

create table if not exists comment
(
    id                                uuid                    primary key,
    fk_author                         uuid                                    references "user"(id),
    created                           timestamp               not null        default now(),
    updated                           timestamp,
    fk_ticket                         uuid                    not null        references ticket(id)
);

create table if not exists user_m2m_comment
(
    fk_user                          uuid                                   references "user"(id),
    fk_comment                       uuid                                   references comment(id)
);

insert into "user" values ('699da8f6-dc01-4a1d-94ac-2633e20e261a', 'SYSTEM', 'SYSTEM', 'admin',
                           '$2a$12$VRY9q057K.DwhnDx6chsTOKuLyl11qnxNWzJ4c7nSsD1wukGzdvMa',
                           now(), null, 'SYSTEM');