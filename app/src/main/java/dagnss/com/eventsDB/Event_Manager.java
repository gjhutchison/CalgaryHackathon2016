package dagnss.com.eventsDB;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by Graham on 05/03/2016.
 */
public class Event_Manager {
    private MobileServiceClient mClient;
    private MobileServiceTable<events> mEventTable;

    private ArrayList<events> eList;
    private Boolean safe;

    public Event_Manager(MobileServiceClient client){
        mClient = client;
        mEventTable = mClient.getTable(events.class);

        eList = new ArrayList<events>();
    }

    public void getEventBySport(String sport){
        final String s = sport;
        setSafe(false);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params){
                MobileServiceList<events> result = null;
                try{
                    result = mEventTable.where().field("type").eq(s).execute().get();
                    eList.clear();
                    for(events event : result){
                        eList.add(event);
                    }
                    setSafe(true);
                }
                catch(Exception e) {
                    Log.e("ERROR", "Failed to get all "+s+" Events");
                }
                return null;
            }
        }.execute();
    }

    public void getEventsByDistance(double dis, double lat, double lon){

        final double lat1 = lat;
        final double lon1 = lon;
        final double distance = dis;

        setSafe(false);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params){
                MobileServiceList<events> result = null;
                try{
                    result = mEventTable.execute().get();
                    eList.clear();
                    for(events event : result){
                        eList.add(event);

                        double lat2 = event.lati;
                        double lon2 = event.longi;

                        double dLon = lon2-lon1;
                        double dLat = lat2-lat2;

                        double a = Math.pow(Math.sin(dLat/2),2)+Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin(dLon/2),2);
                        double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
                        double d = 6373 * c;

                        if(d < distance){
                            eList.add(event);
                        }
                    }
                    setSafe(true);
                }
                catch(Exception e) {
                    Log.e("ERROR", "Failed to get events within "+distance);
                }
                return null;
            }
        }.execute();
    }


    public void getAllEvents(){
        setSafe(false);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params){
                MobileServiceList<events> result = null;
                try{
                    result = mEventTable.execute().get();
                    eList.clear();
                    for(events event : result){
                        eList.add(event);
                    }
                    setSafe(true);
                }
                catch(Exception e) {
                    Log.e("ERROR","Failed to get all Events");
                }
                return null;
            }
        }.execute();
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

    public void setSafe(Boolean b){
        safe = b;
    }

    public Boolean listSafe(){
        return safe;
    }

    public ArrayList<events> getEventList(){
        if(listSafe()){
            return eList;
        }
        else{
            return null;
        }
    }
}
