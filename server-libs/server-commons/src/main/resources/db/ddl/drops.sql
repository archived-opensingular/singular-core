-- Cria um back na mesma pasta do banco atual
BACKUP TO concat(replace(lower(DATABASE_PATH()), lower(database())), 'bkp-singular-', to_char(sysdate, 'YYYYMMDD-hh24MI'),'.zip');
drop all objects;