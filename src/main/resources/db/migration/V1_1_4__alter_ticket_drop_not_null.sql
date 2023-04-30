alter table ticket
    alter column fk_assignee drop not null,
    alter column fk_parent drop not null,
    add column updated timestamp,
    add column priority_name varchar(30);

update ticket
set priority_name = 'THE_LOWEST' where priority = 100;

alter table ticket alter column priority_name set not null;