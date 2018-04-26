package com.icix.model;


import net.spy.memcached.MemcachedClient;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IdGeneratorImpl implements IdGenerator {

    private Logger logger = Logger.getLogger(IdGeneratorImpl.class);

    private final int STARTING_POINT_LATTER = 'A';
    private final int ALPHABET_LETTERS = 26;
    private final int SEQUENTIAL_LIMIT = 999999;
    private final int DAY_LIMIT = 999;
    private final int STARTING_YEAR = 2016;
    private final String template = "%03d-%c%c-%06d"; //00D-YH-00000N
    private Calendar calendar;
    private int sequential = 0;
    private int hourOverlay = 0;
    private int dayOverlay = 0;
    private int yearOverlay = 0;
    private int currentHour = 0;
    private int currentDay = 0;
    private int currentYear = 0;

    private MemcachedClient memcachedClient;

    public IdGeneratorImpl(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
        this.calendar = Calendar.getInstance();

        Object obj = memcachedClient.get(HOUR_OVERLAY);
        if(null != obj) hourOverlay = (int)obj;

        Object objDay = memcachedClient.get(DAY_OVERLAY);
        if(null != objDay) dayOverlay = (int)objDay;

        Object objYear = memcachedClient.get(YEAR_OVERLAY);
        if(null != objYear) yearOverlay = (int)objYear;

        obj =  memcachedClient.get(LAST_ID);
        if(null != obj) {
            String lastId =obj.toString();
            logger.info(String.format("[x]Last ID has from MC.[%s]",lastId));
            if (initDay(lastId))
                if (initHour(lastId))
                    setSequential(lastId);
        }
    }

    private boolean initDay(String lastId) {
        int day = Integer.parseInt(lastId.substring(0,3));
        if(day == getDay()) {
            currentDay = day;
            return true;
        }

        return false;
    }

    private boolean initHour(String lastId) {
        char h = lastId.charAt(5);
        if(h == getHour()){
            currentHour = h;
            return true;
        }

        return false;
    }

   /* private boolean initYear(String lastId) {
        char y = lastId.charAt(6);
        if(y == getYear()){
            currentYear = y;
            return true;
        }

        return false;
    }*/

    private void setSequential(String lastId) {
        sequential = Integer.parseInt(lastId.substring(7,lastId.length()));
    }

    @Override
    public List<String> generate(int amount) throws RangeLimitException {

        if(amount <= 0) return new ArrayList<>();

        calendar = Calendar.getInstance();

        ArrayList<String> list = new ArrayList<>(amount);
        for (int n = 0; n < amount; n++) {
            try {
                list.add(String.format(template, getDay(), getYear(), getHour(), getSequential()));
            }
            catch (RangeLimitException ex){
                if(n != 0) return list; // Return the last numbers we can before throw exception
                throw ex;
            }
        }

        String lastId = list.get(list.size() - 1);
        memcachedClient.set(LAST_ID,0,lastId);

        logger.info(String.format("[x]Last ID has been written to MC.[%s]",lastId));

        return list;
    }

    private int getDay(){
        return calendar.get(Calendar.DAY_OF_YEAR)  + dayOverlay;
    }

    private char getYear(){
        return (char)(STARTING_POINT_LATTER + (calendar.get(Calendar.YEAR)+ yearOverlay) - STARTING_YEAR);
    }

    private char getHour(){
        return (char)(STARTING_POINT_LATTER + calendar.get(Calendar.HOUR_OF_DAY) + hourOverlay);
    }

    private int getSequential() throws RangeLimitException {
        logger.info(String.format("[x]sequential [%d]",sequential));
        logger.info(String.format("[x]currentHour [%d]",currentHour));
        logger.info(String.format("[x]hourOverlay [%d]",hourOverlay));
        if(!nextHour() && sequential == SEQUENTIAL_LIMIT){ // We running out of sequential for current hour
            sequential = 0;
            hourOverlay = adjustHourOverlay();
        }
        else if(nextHour()){ // We ok to move for another range
            sequential = 0;
        }

        logger.info(String.format("[x]hourOverlay 2[%d]",hourOverlay));
        logger.info(String.format("[x]currentHour 2 [%d]",currentHour));
        if(hourOverlay + currentHour >= ALPHABET_LETTERS ) {
            // throw new RangeLimitException("Range of IDs for current hour has reached the limit.");
            dayOverlay = adjustDayOverlay();
            if (dayOverlay + currentDay >= DAY_LIMIT)
                throw new RangeLimitException("Range of IDs for current day has reached the limit.");
        }

        return ++sequential;
    }

    // Check if we hit next hour range
    private boolean nextHour(){
        logger.info(String.format("[x]nextHour currentHour 2 [%d]",currentHour));
        logger.info(String.format("[x]nextHour calendar.get(Calendar.HOUR_OF_DAY) 2 [%d]",calendar.get(Calendar.HOUR_OF_DAY)));
        if(currentHour != calendar.get(Calendar.HOUR_OF_DAY)) {
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            return true;
        }
        return false;
    }
    // Check if we hit next hour range
   /* private boolean nextDay(){
        if(currentDay != calendar.get(Calendar.DAY_OF_YEAR)) {
            currentDay = calendar.get(Calendar.DAY_OF_YEAR);
            return true;
        }
        return false;
    }

    // Check if we hit next hour range
    private boolean nextYear(){
        if(currentYear != calendar.get(Calendar.YEAR)) {
            currentYear = calendar.get(Calendar.YEAR);
            return true;
        }
        return false;
    }*/

    private int adjustHourOverlay(){
        if(currentDay != getDay()){
            currentDay = getDay();
            hourOverlay = 0;
        }
        else{
            hourOverlay++;
            /*if(hourOverlay + currentHour  >= ALPHABET_LETTERS){
                dayOverlay++;
                if(dayOverlay + currentDay >= DAY_LIMIT ){
                    dayOverlay = 0;
                    currentDay = 0;

                    yearOverlay++;
                }
                hourOverlay = 0;
                currentHour = 0;

            }*/
        }
        memcachedClient.set(HOUR_OVERLAY,0,hourOverlay);
        memcachedClient.set(DAY_OVERLAY,0,dayOverlay);
        memcachedClient.set(YEAR_OVERLAY,0,yearOverlay);

        return hourOverlay;
    }

    private int adjustDayOverlay(){
        if(hourOverlay + currentHour  >= ALPHABET_LETTERS) {
            if (currentYear != getYear()) {
                currentYear = getYear();
                dayOverlay = 0;
            } else {
                dayOverlay++;
            }
            hourOverlay = 0;
            currentHour = 0;
        }


        memcachedClient.set(HOUR_OVERLAY,0,hourOverlay);
        memcachedClient.set(DAY_OVERLAY,0,dayOverlay);
        memcachedClient.set(YEAR_OVERLAY,0,yearOverlay);

        return hourOverlay;
    }
}
