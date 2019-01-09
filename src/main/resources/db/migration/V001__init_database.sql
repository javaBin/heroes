create table HEROES (
    id uniqueidentifier primary key,
    email varchar(100) not null,
    name varchar(100) not null,
    twitter varchar(100),
    achievement varchar(100),
    consent_id bigint,
    consented_at datetime,
    consent_client_ip varchar(100)
);


CREATE TABLE achievement_conference_speaker (
    id uniqueidentifier primary key,
    hero_id uniqueidentifier not null REFERENCES heroes(id),
    title varchar(50) not null,
    year int not null
);

CREATE TABLE achievement_usergroup_speaker (
    id uniqueidentifier primary key,
    hero_id uniqueidentifier not null REFERENCES heroes(id),
    title varchar(50) not null,
    talk_date date not null
);

CREATE TABLE achievement_board_member (
    id uniqueidentifier primary key,
    hero_id uniqueidentifier not null REFERENCES heroes(id),
    role varchar(50) not null,
    year int not null
);

