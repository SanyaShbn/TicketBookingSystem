--Trigger functions

CREATE OR REPLACE FUNCTION check_capacity()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.capacity < NEW.general_seats_numb THEN
        RAISE EXCEPTION 'ERROR_CHECK_CAPACITY';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION create_seats()
    RETURNS TRIGGER AS $$
DECLARE
i INT;
BEGIN
FOR i IN 1..NEW.seats_numb LOOP
            INSERT INTO seat (row_id, seat_number) VALUES (NEW.id, i);
END LOOP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION update_seats()
    RETURNS TRIGGER AS $$
BEGIN
    -- Удаляем старые записи
DELETE FROM seat WHERE row_id = NEW.id;

-- Создаем новые записи
FOR i IN 1..NEW.seats_numb LOOP
            INSERT INTO seat (row_id, seat_number) VALUES (NEW.id, i);
END LOOP;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION check_row_number_unique()
    RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM row
        WHERE sector_id = NEW.sector_id
          AND row_number = NEW.row_number
          AND id <> NEW.id) THEN
        RAISE EXCEPTION 'ERROR_CHECK_ROW_NUMBER';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION check_rows_numb_func()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.available_rows_numb > NEW.max_rows_numb THEN
        RAISE EXCEPTION 'ERROR_CHECK_ROWS';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION check_seats_numb_func()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.available_seats_numb > NEW.max_seats_numb THEN
        RAISE EXCEPTION 'ERROR_CHECK_SEATS';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION check_sector_name_unique()
    RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM sector
        WHERE arena_id = NEW.arena_id
          AND sector_name = NEW.sector_name
          AND id <> NEW.id) THEN
        RAISE EXCEPTION 'ERROR_CHECK_SECTOR_NAME';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


create function check_ticket_references() returns trigger
    language plpgsql
as
$$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM ticket
        WHERE event_id = NEW.id
    ) THEN
        RAISE EXCEPTION 'EVENT_REFERENCE_EXCEPTION: Cannot update or delete sport_event with existing ticket references';
END IF;

RETURN NEW;
END;
$$;


CREATE OR REPLACE FUNCTION check_event_time()
    RETURNS TRIGGER AS $$
BEGIN
    -- Проверяем на наличие пересечения событий с одинаковым arena_id, исключая текущую запись
    IF EXISTS (
        SELECT 1
        FROM sport_event
        WHERE
                arena_id = NEW.arena_id
          AND id != NEW.id
          AND (
                    event_date_time = NEW.event_date_time
                OR ABS(EXTRACT(EPOCH FROM (NEW.event_date_time - event_date_time))) < 10800
            )
    ) THEN
        RAISE EXCEPTION 'EVENT_TIME_EXCEPTION: Event already exists at the same time or within 3 hours interval for the same arena';
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--SQL for db init

create table arena
(
    id                 bigint generated always as identity
        primary key,
    name               varchar(255) not null
        constraint unique_name
            unique,
    city               varchar(255) not null,
    capacity           integer      not null,
    general_seats_numb integer default 0
);

alter table arena
    owner to postgres;

create trigger check_capacity_trigger
    before insert or update
                         on arena
                         for each row
                         execute procedure check_capacity();

create table sector
(
    id                   bigint generated always as identity
        primary key,
    sector_name          varchar(25),
    arena_id             bigint
        references arena
            on delete cascade,
    max_rows_numb        integer,
    available_rows_numb  integer default 0,
    max_seats_numb       integer,
    available_seats_numb integer default 0
);

alter table sector
    owner to postgres;

create trigger check_rows_numb
    before update
    on sector
    for each row
    execute procedure check_rows_numb_func();

create trigger check_seats_numb
    before update
    on sector
    for each row
    execute procedure check_seats_numb_func();

create trigger check_sector_name_unique_trigger
    before insert or update
                         on sector
                         for each row
                         execute procedure check_sector_name_unique();

create table row
(
    id         bigint generated always as identity
        primary key,
    sector_id  bigint
        references sector
            on update cascade on delete cascade,
    row_number integer,
    seats_numb integer
);

alter table row
    owner to postgres;

create trigger check_row_number_unique_trigger
    before insert or update
                         on row
                         for each row
                         execute procedure check_row_number_unique();

create trigger after_insert_row
    after insert
    on row
    for each row
    execute procedure create_seats();

create trigger after_update_row
    after update
    on row
    for each row
    when (old.seats_numb IS DISTINCT FROM new.seats_numb)
execute procedure update_seats();

create table seat
(
    id          bigint generated always as identity
        primary key,
    row_id      bigint
        references row
            on update cascade on delete cascade,
    seat_number integer
);

alter table seat
    owner to postgres;

create table sport_event
(
    id              bigint generated always as identity
        primary key,
    event_name      varchar(50),
    event_date_time timestamp,
    arena_id        bigint
        references arena
);

alter table sport_event
    owner to postgres;

create trigger event_time_trigger
    before insert or update
                         on sport_event
                         for each row
                         execute procedure check_event_time();

create trigger check_update_delete_sport_event
    before update
    on sport_event
    for each row
    execute procedure check_ticket_references();

create table ticket
(
    id       bigint generated always as identity
        primary key,
    event_id bigint
        references sport_event,
    seat_id  bigint
        references seat,
    status   varchar(25),
    price    numeric(10, 2)
);

alter table ticket
    owner to postgres;

create table users
(
    id       bigint generated always as identity
        primary key,
    email    varchar(50)  not null
        unique,
    password varchar(100) not null,
    role     varchar(32)
);

alter table users
    owner to postgres;

create table role
(
    id        bigint generated always as identity
        primary key,
    role_name varchar(50) not null
        unique
);

alter table role
    owner to postgres;

create table user_roles
(
    user_id bigint not null
        references users,
    role_id bigint not null
        references role,
    primary key (user_id, role_id)
);

alter table user_roles
    owner to postgres;

create table user_cart
(
    user_id   bigint not null
        references users,
    ticket_id bigint not null
        references ticket,
    PRIMARY KEY (user_id, ticket_id)
);

alter table user_cart
    owner to postgres;

create table purchased_tickets
(
    id            bigint generated always as identity
        primary key,
    user_id       bigint    not null
        references users,
    ticket_id     bigint    not null
        constraint unique_ticket_id
            unique
        references ticket,
    purchase_date timestamp not null
);

alter table purchased_tickets
    owner to postgres;

insert into role (role_name) values ('ADMIN'), ('USER');

insert into users (email, password, role)
values ('admin@gmail.com', '$2a$10$q/a9kx3bFYPe013IbWnJDeyYc6PjXWmNTpdB1198yUYqNjJQtKkC.', 'ADMIN');

insert into user_roles (user_id, role_id)
values (1, 1);