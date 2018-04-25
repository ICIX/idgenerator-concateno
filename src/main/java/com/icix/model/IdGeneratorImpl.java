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
    private final int SEQUENTIAL_LIMIT = 99999;
    private final int STARTING_YEAR = 2016;
    private final String template = "%03d-%c%c-%05d"; //00D-YH-0000N
    private Calendar calendar;
    private int sequential = 0;
    private int hourOverlay = 0;
    private int currentHour = 0;
    private int currentDay = 0;

    private MemcachedClient memcachedClient;

    public IdGeneratorImpl(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
        this.calendar = Calendar.getInstance();

        Object obj = memcachedClient.get(HOUR_OVERLAY);
        if(null != obj) hourOverlay = (int)obj;

        obj =  memcachedClient.get(LAST_ID);
        if(null != obj) {
            String lastId =obj.toString();
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

        logger.debug(String.format("[x]Last ID has been written to MC.[%s]",lastId));

        return list;
    }

    private int getDay(){
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    private char getYear(){
        return (char)(STARTING_POINT_LATTER + calendar.get(Calendar.YEAR) - STARTING_YEAR);
    }

    private char getHour(){
        return (char)(STARTING_POINT_LATTER + calendar.get(Calendar.HOUR_OF_DAY) + hourOverlay);
    }

    private int getSequential() throws RangeLimitException {
        if(!nextHour() && sequential == SEQUENTIAL_LIMIT){ // We running out of sequential for current hour
            sequential = 0;
            hourOverlay = adjustHourOverlay();
        }
        else if(nextHour()){ // We ok to move for another range
            sequential = 0;
        }

        if(hourOverlay + currentHour >= ALPHABET_LETTERS )
            throw new RangeLimitException("Range of IDs for current hour has reached the limit.");

        return ++sequential;
    }

    // Check if we hit next hour range
    private boolean nextHour(){
        if(currentHour != calendar.get(Calendar.HOUR_OF_DAY)) {
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            return true;
        }
        return false;
    }

    private int adjustHourOverlay(){
        if(currentDay != getDay()){
            currentDay = getDay();
            hourOverlay = 0;
        }
        else{
            hourOverlay++;
        }
        memcachedClient.set(HOUR_OVERLAY,0,hourOverlay);

        return hourOverlay;
    }
}
