create table if not exists `permission`
(
    `id` int auto_increment primary key,
    `identifier` varchar(512) not null,
    `description` varchar(1024) not null,
    constraint `permission_identifier_uindex` unique (`identifier`)
);

create table if not exists `group`
(
    `id` int auto_increment primary key,
    `parent_id` int,
    `identifier` varchar(512) not null,
    `weight` int default 0,
    `description` varchar(1024) not null,
    constraint `group_group__fk` foreign key (`parent_id`) references `group`(`id`),
    constraint `group_identifier_uindex` unique (`identifier`)
);

create table if not exists `group_permission`
(
    `id` int auto_increment primary key,
    `perm_value` bool not null,
    `group_id` int not null,
    `permission_id` int not null,
    constraint `group_permission_group__fk` foreign key (`group_id`) references `group`(`id`),
    constraint `group_permission_permission__fk` foreign key (`permission_id`) references `permission` (`id`)
);

create table if not exists `player`
(
    `id` int auto_increment primary key,
    `uuid`  varchar(255) not null,
    `name`  varchar(255) not null,
    constraint `player_uuid_uindex` unique (`uuid`)
);

create table if not exists `player_group`
(
    `id` int auto_increment primary key,
    `player_id` int not null,
    `group_id` int not null,
    constraint `player_group_player__fk` foreign key (`player_id`) references `player`(`id`),
    constraint `player_group_group__fk` foreign key (`group_id`) references `group`(`id`)
);

create table if not exists `player_permission`
(
    `id` int auto_increment primary key,
    `perm_value` bool not null,
    `player_id` int not null,
    `permission_id` int not null,
    constraint `player_permission_player__fk` foreign key (`player_id`) references `player`(`id`),
    constraint `player_permission_permission__fk` foreign key (`permission_id`) references `permission`(`id`)
);