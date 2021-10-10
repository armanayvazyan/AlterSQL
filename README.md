# AlterSQL
MySQL DBMS custom realization with JAVA


Usage
-----------------------------------

create table <table_name>
create table <table_name> (col1:type1,col2:type2.....colN:typeN)

drop table <table_name>

alter table <table_name> ADD (col1:type1....colN:typeN)
alter table <table_name> MODIFY (col1:type1....colN:typeN)

truncate table <table_name>

insert into <table_name> (col1, col2, col3,.... col N) values (value1, value2, value3, .... valueN);
update <table_name> set (col1=value1,col2=val2,... colN=valN) where (colN = valN)


FYI: 
-----------------------------------
DB Files are saves in src/main/recources/database/
Commands are not case sensitive
Table is stored as CSV File
Meta info about Table Columns in stored in same directory <table_name>_META.txt
