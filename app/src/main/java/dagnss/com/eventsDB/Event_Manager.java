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
                        if(event == null){
                            break;
                        }
                        eList.add(event);
                    }
                }
                catch(Exception e) {
                    Log.e("ERROR", "Failed to get all "+s+" Events");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                setSafe(true);
                return;
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

                        if(event == null){
                            break;
                        }

                        double lat2 = event.lati;
                        double lon2 = event.longi;

                        double dLon = lon2-lon1;
                        double dLat = lat2-lat1;

                        double a = Math.pow(Math.sin(dLat/2),2)+Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin(dLon/2),2);
                        double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
                        double d = 6373 * c;

                        if(d < distance){
                            eList.add(event);
                        }
                    }
                }
                catch(Exception e) {
                    Log.e("ERROR", "Failed to get events within "+distance);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                setSafe(true);
                return;
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
                        if(event == null){
                            break;
                        }
                        eList.add(event);
                    }
                }
                catch(Exception e) {
                    Log.e("ERROR","Failed to get all Events");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                setSafe(true);
                return;
            }
        }.execute();
    }




    public void GetClosestEvent(double lon,double lat){
        final double lat1 = lat;
        final double lon1 = lon;

        setSafe(false);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params){
                MobileServiceList<events> result = null;
                try{
                    result = mEventTable.execute().get();
                    events e = new events();
                    eList.clear();
                    double lowestDistance = -1;
                    for(int i = 0;i<result.size();i++){

                        if(result.get(i) == null){
                            break;
                        }

                        double lat2 = result.get(i).lati;
                        double lon2 = result.get(i).longi;

                        double dLon = lon2-lon1;
                        double dLat = lat2-lat1;

                        double a = Math.pow(Math.sin(dLat/2),2)+Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin(dLon/2),2);
                        double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
                        double d = 6373 * c;
                        d = Math.abs(d);

                        if(lowestDistance == -1){
                            lowestDistance = d;
                        }

                        if(d < lowestDistance){
                            e = result.get(i);
                            lowestDistance = d;
                        }
                    }
                    eList.add(e);
                }
                catch(Exception e) {
                    Log.e("ERROR", "Failed to get events with min distance");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                setSafe(true);
                return;
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
