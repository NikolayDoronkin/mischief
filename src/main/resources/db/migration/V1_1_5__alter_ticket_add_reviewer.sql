alter table ticket add column fk_reviewer uuid references "user"(id)