CREATE FUNCTION DBSINGULAR.dateDiffInDays (@DATE1 datetime, @DATE2 datetime)
RETURNS float
AS BEGIN
return (cast(@DATE1 as double precision) - (cast(@DATE2 as double precision)))
END