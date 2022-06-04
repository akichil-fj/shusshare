create schema if not exists s_share;
set schema s_share;

create table if not exists account
(
    account_id         bigint unsigned       primary key  auto_increment,
    user_id            varchar(15)           unique       not null,
    user_name          varchar(30)           unique       not null,
    password           char(60)                           not null,
    profile_photo_url  varchar(300)          default null,
    updated_at         timestamp(3)          default current_timestamp(3) not null,
    lock_version       int unsigned          default 0    not null,
    status             smallint unsigned     default 0    not null
);

create table if not exists friend
(
    friend_id          bigint unsigned      primary key   auto_increment,
    account_id_from    bigint unsigned                    not null,
    account_id_to      bigint unsigned                    not null,
    status             smallint unsigned    default 0     not null,
    updated_at         timestamp(3)         default current_timestamp(3) not null,
    foreign key (user_id_1) references account (id),
    foreign key (user_id_2) references account (id)
);

create table if not exists shussha
(
    shussha_id   bigint unsigned       primary key          auto_increment,
    user_id      bigint unsigned                            not null,
    date         date            default current_date,
    updated_at   timestamp(3)    default current_timestamp(3) not null,
    foreign key (user_id) references account (id)
);

