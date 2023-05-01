alter table comment add column "value" varchar(1000);
update comment set value = 'default_comment';
alter table comment alter column "value" set not null;