create table execution_facts (
                                 id uuid not null,
                                 description varchar(500) not null,
                                 finish_time timestamp(6),
                                 start_time timestamp(6) not null,
                                 version bigint,
                                 executor_id uuid not null,
                                 primary key (id)
);

create table participants (
                              id uuid not null,
                              email varchar(320) not null,
                              full_name varchar(100) not null,
                              version bigint,
                              primary key (id)
);

alter table if exists participants
    drop constraint if exists participants_email_key;

alter table if exists participants
    add constraint participants_email_key unique (email);

alter table if exists execution_facts
    add constraint FK4liygverkrgerrp7e3fycqxpb
        foreign key (executor_id)
            references participants;
