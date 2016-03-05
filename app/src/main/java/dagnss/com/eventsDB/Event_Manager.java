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

    /*
    public MobileServiceList<events> getEventByID(String id){
        MobileServiceList<events> result = null;

        try{


        }catch(Exception e){

        }
    }
    */
    public MobileServiceList<events> getEventsByDistance(float distance, double lat1, double lon1){
        MobileServiceList<events> result = null;

        ArrayList<String> ids = new ArrayList<String>();

    /*
        dlon = lon2 - lon1
        dlat = lat2 - lat1
        a = (sin(dlat/2))^2 + cos(lat1) * cos(lat2) * (sin(dlon/2))^2
        c = 2 * atan2( sqrt(a), sqrt(1-a) )
        d = R * c (where R is the radius of the Earth)
     */

        try{
            result = mEventTable.execute().get();

            for(int i = 0;i<result.size();i++){
                if(result.get(i)!=null){
                    double lat2 = result.get(i).lati;
                    double lon2 = result.get(i).longi;

                    double dLon = lon2-lon1;
                    double dLat = lat2-lat2;

                    double a = Math.pow(Math.sin(dLat/2),2)+Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin(dLon/2),2);
                    double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
                    double d = 6373 * c;

                    if(d > distance){
                        ids.add(result.get(i).id);
                    }
                }
            }

            for(int i = 0;i<ids.size();i++){
                for(int j = 0;j < result.size();j++){
                    if(result.get(j).id.equals(ids.get(i))){
                        result.remove(j);
                        break;
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result;
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
