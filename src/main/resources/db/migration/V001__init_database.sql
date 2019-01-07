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


CREATE TABLE person (
    id VARCHAR(50),
    data varchar(1000)
);

CREATE TABLE achievement (
    id VARCHAR(50),
    data varchar(1000)
);

CREATE TABLE hero_achievements (
    id uniqueidentifier primary key,
    hero_id uniqueidentifier not null REFERENCES heroes(id),
    achievement_type varchar(50) not null,
    data text
);
