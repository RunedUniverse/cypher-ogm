DROP DATABASE IF EXISTS `rogm-test-database`;
CREATE DATABASE `rogm-test-database`;
USE `rogm-test-database`;

create table Person
(
    id        bigint auto_increment,
    firstName text    null,
    lastName  text    null,
    fictional boolean null,
    constraint Person_pk
        primary key (id)
);
create table Artist
(
    id        bigint auto_increment,
    person_id bigint null,
    constraint Artist_pk
        primary key (id),
    constraint Artist_Person_id_fk
        foreign key (person_id) references `rogm-test-database`.Person (id)
);

