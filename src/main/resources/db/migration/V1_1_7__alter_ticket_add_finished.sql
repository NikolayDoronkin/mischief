alter table ticket
    add column started date,
    add column finished date,
    add column difficulty int,
    add column duration int default 0;