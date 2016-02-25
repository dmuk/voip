package voip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class DateUtil {
  // NIST, Boulder, Colorado  (time-a.timefreq.bldrdoc.gov)
  // public static final String ATOMICTIME_SERVER="132.163.4.101";
  // NIST, Gaithersburg, Maryland (time-a.nist.gov)
  // public static final String ATOMICTIME_SERVER="129.6.15.28";
  // NIST, Gaithersburg, Maryland  (time-c.nist.gov)
  public static final String ATOMICTIME_SERVER="129.6.15.30";
//  public static final int ATOMICTIME_PORT = 13;


  public final static GregorianCalendar getAtomicTime() throws IOException{
    BufferedReader in = null;
    Socket conn = null;

    try {
       conn = new Socket(ATOMICTIME_SERVER, 13);

       in = new BufferedReader
         (new InputStreamReader(conn.getInputStream()));

       String atomicTime;
       while (true) {
          if ( (atomicTime = in.readLine()).contains("*")) {
             break;
          }
       }
//       System.out.println("DEBUG 1 : " + atomicTime);
       String[] fields = atomicTime.split(" ");
       GregorianCalendar calendar = new GregorianCalendar();

       String[] date = fields[1].split("-");
       calendar.set(Calendar.YEAR, 2000 +  Integer.parseInt(date[0]));
       calendar.set(Calendar.MONTH, Integer.parseInt(date[1])-1);
       calendar.set(Calendar.DATE, Integer.parseInt(date[2]));

       // deals with the timezone and the daylight-saving-time (you may need to adjust this)
       // here i'm using "EST" for Eastern Standart Time (to support Daylight Saving Time)
       TimeZone tz = TimeZone.getTimeZone("EST"); // or .getDefault()
       int gmt = (tz.getRawOffset() + tz.getDSTSavings()) / 3600000;
//       System.out.println("DEBUG 2 : " + gmt);

       String[] time = fields[2].split(":");
       calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]) + gmt);
       calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
       calendar.set(Calendar.SECOND, Integer.parseInt(time[2]));
       return calendar;
    }
    catch (IOException e){
       throw e;
    }
    finally {
       if (in != null) { in.close();   }
       if (conn != null) { conn.close();   }
    }
  }

  public static void main(String args[]) throws IOException {
    SimpleDateFormat sdf =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    System.out.println("Atomic time : " +
//        sdf.format(DateUtil.getAtomicTime().getTime()));
    while(true){
    byte[] dd = sdf.format(DateUtil.getAtomicTime().getTime()).getBytes();
      System.out.println(dd.length);
    }
  }
}
