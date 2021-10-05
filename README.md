# AlterSQL
MySQL DBMS custom realization with JAVA


Usage

Create Command

create table <table_name>
create table <table_name> (col1:type1,col2:type2.....colN:typeN)

drop table <table_name>

alter table <table_name> ADD (col1:type1....colN:typeN)
alter table <table_name> MODIFY (col1:type1....colN:typeN)

FYI: 
DB Files are saves in src/main/recources/database/
Commands are not case sensitive
Table is stored as CSV File
Meta info about Table Columns in stored in same directory <table_name>_META.txt
