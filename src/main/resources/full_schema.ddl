create table english_entry (id  bigserial not null, done_practicing boolean not null, random_order int8, word varchar(32), user_id int8, primary key (id));
create table english_meaning (id  bigserial not null, value varchar(255), entry_id int8, primary key (id));
create table english_usage (id  bigserial not null, value varchar(255), entry_id int8, primary key (id))
create table german_entry (id  bigserial not null, done_practicing boolean not null, random_order int8, word varchar(32), artikel varchar(3), user_id int8, primary key (id));
create table german_meaning (id  bigserial not null, value varchar(255), entry_id int8, primary key (id));
create table german_usage (id  bigserial not null, value varchar(255), entry_id int8, primary key (id));
create table practice (id  bigserial not null, entry_type varchar(255), last_practiced_index int8, primary key (id));
create table users (id  bigserial not null, email varchar(100) not null, encrypted_password varchar(255) not null, first_name varchar(50) not null, last_name varchar(50) not null, user_uuid varchar(255) not null, primary key (id));
create table invitation (id  bigserial not null, code varchar(255), user_id int8, primary key (id));

CREATE SEQUENCE random_order_seq_english START 1;
CREATE SEQUENCE random_order_seq_german START 1;


alter table users add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);
alter table users add constraint UK_4mcg6l0va97nbd8o9tqpeg104 unique (user_uuid);
alter table english_entry add constraint FKkcmvc538ac4xcquunlorw7299 foreign key (user_id) references users;
alter table english_meaning add constraint FKj8kfjbive5pqc0oyhqcw6b850 foreign key (entry_id) references english_entry;
alter table english_usage add constraint FKr9hiplp03ogtoamth9ngrjm5x foreign key (entry_id) references english_entry;
alter table german_entry add constraint FK57rvhhy82ihqelx9whpa5m5ch foreign key (user_id) references users;
alter table german_meaning add constraint FKbwl46eip43ub0ktd9en5uuyyd foreign key (entry_id) references german_entry;
alter table german_usage add constraint FK97olr14w28uaucttv12jswf0j foreign key (entry_id) references german_entry;
alter table invitation add constraint FKs81mf5o97gfb55r5vbov80r5m foreign key (user_id) references users;
