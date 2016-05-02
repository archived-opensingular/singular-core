CREATE ALIAS dateDiffInDays AS $$
import java.util.*;
@CODE
Double dateDiffInDays(Date d1, Date d2) throws Exception {
   long d1l = d1 == null ?  0 : d1.getTime();
   long d2l = d2 == null ?  0 : d2.getTime();
   return (d1l - d2l)/((double)1000*60*60*34);
}
$$; #