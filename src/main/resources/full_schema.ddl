create table entry (id  bigserial not null, done_practicing boolean not null, word varchar(32), artikel varchar(3), user_id int8, primary key (id));
create table meaning (id  bigserial not null, value varchar(255), entry_id int8, primary key (id));
create table usage (id  bigserial not null, value varchar(255), entry_id int8, primary key (id));
create table users (id  bigserial not null, email varchar(100) not null, encrypted_password varchar(255) not null, first_name varchar(50) not null, last_name varchar(50) not null, user_uuid varchar(255) not null, primary key (id));
create table invitation (id  bigserial not null, code varchar(255), user_id int8, primary key (id));

alter table users add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);
alter table users add constraint UK_4mcg6l0va97nbd8o9tqpeg104 unique (user_uuid);
alter table entry add constraint FK57rvhhy82ihqelx9whpa5m5ch foreign key (user_id) references users;
alter table meaning add constraint FKbwl46eip43ub0ktd9en5uuyyd foreign key (entry_id) references german_entry;
alter table usage add constraint FK97olr14w28uaucttv12jswf0j foreign key (entry_id) references german_entry;
alter table invitation add constraint FKs81mf5o97gfb55r5vbov80r5m foreign key (user_id) references users;
