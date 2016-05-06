create table if not exists MultiKeysPerson (
	ID int not null,
	NAME varchar,
	AGE  smallint not null,
  PRIMARY KEY (ID, AGE)
)//; ALTER TABLE MultiKeysPerson add PRIMARY KEY (ID, AGE);