package dagnss.com.eventsDB;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;

/**
 * Created by Graham on 05/03/2016.
 */
public class Event_Manager {
    private MobileServiceClient mClient;
    private MobileServiceTable<events> mEventTable;
    public Event_Manager(MobileServiceClient client){
        mClient = client;
        mEventTable = mClient.getTable(events.class);
    }

    public MobileServiceList<events> getEventBySport(String sport){
        MobileServiceList<events> result = null;
        updateTable();


        try{
            result = mEventTable.where().field("type").eq(sport).execute().get();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
    public MobileServiceList<events> getEventByDistance(float distance, float curLat, float curLong){
        MobileServiceList<events> result = null;

        try{
            result = mEventTable.where().field("")
        }

        return result;
    }
    */

    public MobileServiceList<events> getAllEvents(){
        MobileServiceList<events> result = null;
        updateTable();
        try {
            result = mEventTable.execute().get();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void createEvent(String type, String desc, double eLat, double eLong){
        events e = new events();
        e.type = type;
        e.description = desc;
        e.lati = eLat;
        e.longi = eLong;

        mEventTable.insert(e);


        updateTable();
    }

    public void updateTable(){
        mEventTable = mClient.getTable(events.class);
    }

}
