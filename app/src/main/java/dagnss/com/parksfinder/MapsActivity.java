package dagnss.com.parksfinder;

import dagnss.com.eventsDB.*;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.os.AsyncTask;


import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlPlacemark;
import com.microsoft.windowsazure.mobileservices.*;


import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

enum Sport
{
    Soccer,
    Baseball,
    Tennis,
    Frisbee,
    Basketball,
    Volleyball,
    Skating
}

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener
{

    private GoogleMap mMap;
    private MobileServiceClient mClient;
    LocationManager locationManager;
    LatLng currLoc;
    private EditText result;
    private final LatLng CalgaryCentre = new LatLng( 51.045, -114.057222 );

    boolean loaded = false;
    private ArrayList<Marker> SoccerMarkers;
    private ArrayList<Marker> TennisMarkers;
    private ArrayList<Marker> BaseballMarkers;
    private ArrayList<Marker> FrisbeeMarkers;
    private ArrayList<Marker> IceSkateMarkers;
    private ArrayList<Marker> BasketballMarkers;
    private ArrayList<Marker> VolleyballMarkers;

    private HashMap<Sport, ArrayList<Marker>> sportsMap;


    private Event_Manager eventManager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private Sport currentlySelected = Sport.Soccer;
    private boolean initialLoad = true;
    private KMLParser parser;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

	    try{
            mClient = new MobileServiceClient(
                    "https://sportyevents.azure-mobile.net/",
                    "AxZxcxblWqLJxImvALWxqJOIAdMrhe94",
                    this
            );
            eventManager = new Event_Manager(mClient);
        }catch(Exception e){
            e.printStackTrace();
        }

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {


                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        // TODO Auto-generated method stub
                        if(!loaded) {
                            LatLng latLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            //mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                            loaded = true;
                        }
                    }
                });

            }}


        initToolBar();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder( this ).addApi( AppIndex.API ).build();

        sportsMap.put( Sport.Soccer, SoccerMarkers );
        sportsMap.put( Sport.Baseball, BaseballMarkers );
        sportsMap.put( Sport.Basketball, BasketballMarkers );
        sportsMap.put( Sport.Frisbee, FrisbeeMarkers );
        sportsMap.put( Sport.Skating, IceSkateMarkers );
        sportsMap.put( Sport.Tennis, TennisMarkers );
        sportsMap.put( Sport.Volleyball, VolleyballMarkers );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu_buttons, menu );
        initSportMenu( menu.findItem( R.id.action_change_sport ) );
        return true;

    }

    private void initToolBar()
    {
        Toolbar myToolbar = ( Toolbar ) findViewById( R.id.my_toolbar );
        myToolbar.showOverflowMenu();
        setSupportActionBar( myToolbar );
    }

    private void initSportMenu( MenuItem item )
    {
        ArrayList<ItemData> list = new ArrayList<>();
        list.add( new ItemData( "Soccer", R.drawable.sport ) );
        list.add( new ItemData( "Baseball", R.drawable.baseball ) );
        list.add( new ItemData( "Tennis", R.drawable.tennis ) );
        list.add( new ItemData( "Frisbee", R.drawable.frisbee ) );
        list.add( new ItemData( "Basketball", R.drawable.basketball ) );
        list.add( new ItemData( "Volleyball", R.drawable.volleyball ) );
        list.add( new ItemData( "Skating", R.drawable.skating ) );

        Spinner sp = ( Spinner ) MenuItemCompat.getActionView( item );
        ImageSpinnerAdapter adapter = new ImageSpinnerAdapter( this,
                R.layout.image_spinner_layout, R.id.txt, list );
        sp.setAdapter( adapter );
        sp.setOnItemSelectedListener( this );
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady( GoogleMap googleMap )
    {
        mMap = googleMap;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        else {
            double longitude = 0;
            double latitude = 0;
            LocationRequest mLocationRequest = LocationRequest.create();

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                String locLat = String.valueOf(latitude) + "," + String.valueOf(longitude);
                currLoc = new LatLng(latitude, longitude);
                MarkerOptions mkrOpt = new MarkerOptions();
                if (mMap != null) {
                    mMap.addMarker(mkrOpt.position(currLoc).title("YOU!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 11));
                }
            }
        }
        eventManager.getAllEvents();

        new LongOperation().execute("");
        //eventManager.createEvent("Hockey", "A brutal sport", currLoc.latitude, currLoc.longitude);
        //getEventBySport(Sport.Baseball);
        // Add a marker in Sydney and move the camera
        int inputRes = R.raw.calgary_sports_surfaces;

        if ( initialLoad )
        {
            parser = new KMLParser( mMap, getApplicationContext() );
            initialLoad = false;
        }

        parser.loadKML( inputRes );

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( CalgaryCentre, 10 ) );

    }

    @Override
    public void onItemSelected( AdapterView<?> parent, View view, int pos, long id )
    {
        switch(pos)
        {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }

    @Override
    public void onNothingSelected( AdapterView<?> parent )
    {

    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )

    {
        int result = item.getItemId();
        switch ( result )
        {
            case R.id.action_change_location:
              /* result = (EditText) findViewById(R.id.action_change_location);*/

                EditText but_location = ( EditText ) findViewById( R.id.location_input );
                if ( but_location.getVisibility() == View.INVISIBLE )
                {
                    but_location.setVisibility( View.VISIBLE );
                } else
                {
                    but_location.setVisibility( View.INVISIBLE );
                }

                return true;

            case R.id.action_change_sport:
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            while(!eventManager.listSafe())
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
//            ArrayList<events> evens = eventManager.getEventList();
//            for (events e : evens) {
//                LatLng eloc = new LatLng(e.lati, e.longi);
//                MarkerOptions mkrOpt = new MarkerOptions();
//                if (mMap != null) {
//                    mMap.addMarker(mkrOpt.position(eloc).title(e.type + ": " + e.description));
//                }
//            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            ArrayList<events> evens = eventManager.getEventList();
            for (events e : evens) {
                LatLng eloc = new LatLng(e.lati, e.longi);
                MarkerOptions mkrOpt = new MarkerOptions();
                if (mMap != null) {
                    mMap.addMarker(mkrOpt.position(eloc).title(e.type + ": " + e.description));
                }
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse( "http://host/path" ),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse( "android-app://dagnss.com.parksfinder/http/host/path" )
        );
        AppIndex.AppIndexApi.start( client, viewAction );
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse( "http://host/path" ),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse( "android-app://dagnss.com.parksfinder/http/host/path" )
        );
        AppIndex.AppIndexApi.end( client, viewAction );
        client.disconnect();
    }

    public void toggleVisiblity( Sport sport )
    {
        ArrayList<Marker> sportList = sportsMap.get( sport );

        if ( sportList.isEmpty() )
        {
            buildMarkerList( sport, sportList );
        }
        else
        {
            for ( Marker elem : sportList )
            {
                elem.setVisible( !elem.isVisible() );
            }
        }
    }

    private void buildMarkerList( Sport sport, ArrayList<Marker> list )
    {
        String sportType = "";
        switch ( sport )
        {
            case Soccer:
                sportType = "SOCCER";
                break;
            case Tennis:
                sportType = "TENNIS";
                break;
            case Baseball:
                sportType = "BALL DIAMOND DUGOUT";
                break;
            case Basketball:
                sportType = "BASKETBALL";
                break;
            case Frisbee:
                sportType = "ULTIMATE FRISBEE";
                break;
            case Volleyball:
                sportType = "VOLLEYBALL";
                break;
            case Skating:
                sportType = "ICE AREA";
                break;
        }
        if ( sportType.isEmpty() )
        {
            return;
        }

        ArrayList<KmlPlacemark> placemarks = parser.getFacilities( sportType );

        for( KmlPlacemark location : placemarks )
        {
            LatLng pos = parser.getLocation( location );
            MarkerOptions mkrOpt = new MarkerOptions();
            Marker newMarker = mMap.addMarker( mkrOpt.position(pos) );

            list.add( newMarker );
        }
    }
}
