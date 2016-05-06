DROP TABLE if exists MultiKeysPerson;
create table if not exists PERSON (
	ID int identity primary key,
	NAME varchar,
	AGE  smallint,
)