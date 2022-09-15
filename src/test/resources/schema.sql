create schema if not exists s_share;
set schema s_share;

create table if not exists account
(
    account_id         bigint unsigned       primary key  auto_increment,
    user_id            varchar(15)           unique       not null,
    user_name          varchar(30)           unique       not null,
    password           char(60)                           not null,
    profile            text                  default '',
    profile_photo_url  varchar(300)          default null,
    updated_at         timestamp(3)          default current_timestamp(3) not null,
    lock_version       int unsigned          default 0    not null,
    status             smallint unsigned     default 0    not null,
    shussha_count      int                   default 0    not null
);

create table if not exists friend
(
    account_id_from    bigint unsigned                    not null,
    account_id_to      bigint unsigned                    not null,
    status             smallint unsigned    default 0     not null,
    lock_version       int unsigned         default 0     not null,
    updated_at         timestamp(3)         default current_timestamp(3) not null,
    primary key(account_id_from, account_id_to),
    foreign key (account_id_from) references account (account_id),
    foreign key (account_id_to) references account (account_id)
);

create table if not exists shussha
(
    shussha_id     bigint unsigned       primary key          auto_increment,
    account_id     bigint unsigned                            not null,
    date           date                  default current_date,
    status         smallint unsigned     default 0            not null,
    lock_version   int unsigned          default 0            not null,
    updated_at     timestamp(3)          default current_timestamp(3) not null,
    foreign key (account_id) references account (account_id),
    unique key (account_id, date)
);

create table if not exists recruitment
(
    recruitment_id     bigint unsigned       primary key          auto_increment,
    created_by         bigint unsigned                            not null,
    shussha_id         bigint unsigned                            not null,
    title              varchar(30)                                not null,
    genre              smallint unsigned     default 0            not null,
    deadline           date                  default null,
    capacity           int unsigned          default null,
    participant_count  int unsigned          default 0            not null,
    status             smallint unsigned     default 0            not null,
    lock_version       int unsigned          default 0            not null,
    updated_at         timestamp(3)          default current_timestamp(3) not null,
    foreign key (created_by) references account (account_id),
    foreign key (shussha_id) references shussha (shussha_id)
);

create table if not exists lunch_participants
(
    recruitment_id     bigint unsigned                            not null,
    account_id         bigint unsigned                            not null,
    primary key (recruitment_id, account_id),
    foreign key (recruitment_id) references recruitment (recruitment_id),
    foreign key (account_id) references account (account_id)
);
