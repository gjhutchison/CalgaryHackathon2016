package dagnss.com.parksfinder;

import dagnss.com.eventsDB.*;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;
import com.microsoft.windowsazure.mobileservices.*;


import java.io.IOException;
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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, GoogleMap.OnMarkerClickListener
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
    private HashMap<Sport, KmlLayer> layerMap;

    AlertDialog.Builder builder;
    Marker currentMarker = null;
    KmlLayer currentLayer = null;
    String EventDesc = null;

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

        sportsMap = new HashMap<>();
        layerMap = new HashMap<>();
        SoccerMarkers = new ArrayList<>();
        TennisMarkers= new ArrayList<>();
        BaseballMarkers= new ArrayList<>();
        FrisbeeMarkers= new ArrayList<>();
        IceSkateMarkers= new ArrayList<>();
        BasketballMarkers= new ArrayList<>();
        VolleyballMarkers= new ArrayList<>();

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
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
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

        builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Do you want to create an event here?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                eventManager.createEvent(EventDesc, "New event!", currentMarker.getPosition().latitude, currentMarker.getPosition().longitude);
                currentMarker.setIcon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                currentMarker.setTitle(EventDesc + ": New event!");
                currentMarker = null;
                EventDesc = null;
                dialog.dismiss();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate(R.menu.menu_buttons, menu);
        initSportMenu(menu.findItem(R.id.action_change_sport));
        return true;

    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        String title = marker.getTitle();
        String event[] = title.split(":");
        if(event[0].compareTo("Type") == 0) {

            currentMarker = marker;
            EventDesc = event[1];
            AlertDialog alert = builder.create();
            alert.show();

            return true;
        }

        return false;
    }

    private void initToolBar()
    {
        Toolbar myToolbar = ( Toolbar ) findViewById( R.id.my_toolbar );
        myToolbar.showOverflowMenu();
        setSupportActionBar(myToolbar);
    }

    private void initSportMenu( MenuItem item )
    {
        ArrayList<ItemData> list = new ArrayList<>();

        list.add(new ItemData("Basketball", R.drawable.basketball));
        list.add( new ItemData( "Soccer", R.drawable.sport ) );
        list.add( new ItemData( "Baseball", R.drawable.baseball ) );
        list.add( new ItemData( "Tennis", R.drawable.tennis ) );
        list.add( new ItemData( "Frisbee", R.drawable.frisbee ) );
        list.add(new ItemData("Volleyball", R.drawable.volleyball));
        list.add( new ItemData( "Skating", R.drawable.skating ) );

        Spinner sp = ( Spinner ) MenuItemCompat.getActionView( item );
        ImageSpinnerAdapter adapter = new ImageSpinnerAdapter( this,
                R.layout.image_spinner_layout, R.id.txt, list );
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);
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
        if ( initialLoad )
        {
            initialLoad = false;
            parser = new KMLParser( mMap, getApplicationContext() );
            layerMap.put( Sport.Baseball, parser.loadKML( R.raw.baseballplacemarks ) );
            layerMap.put( Sport.Basketball, parser.loadKML( R.raw.basketballplacemarks ) );
            layerMap.put( Sport.Volleyball, parser.loadKML( R.raw.volleyballplacemarks ) );
            layerMap.put( Sport.Tennis, parser.loadKML( R.raw.tennisplacemarks ) );
            layerMap.put( Sport.Skating, parser.loadKML( R.raw.icerinkplacemarks) );
            layerMap.put( Sport.Soccer, parser.loadKML( R.raw.soccerplacemarks ) );
            layerMap.put( Sport.Frisbee, parser.loadKML( R.raw.frisbeeplacemarks ) );
        }

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( CalgaryCentre, 10 ) );
        mMap.setOnMarkerClickListener( this );

    }

    @Override
    public void onItemSelected( AdapterView<?> parent, View view, int pos, long id )
    {
        if(currentLayer != null)
            currentLayer.removeLayerFromMap();

        switch(pos)
        {
            case 0:
                toggleVisibility(Sport.Basketball);
                currentlySelected = Sport.Basketball;
                currentLayer = layerMap.get( Sport.Basketball );
                break;
            case 1:
                toggleVisibility(Sport.Soccer);
                currentlySelected = Sport.Soccer;
                currentLayer = layerMap.get( Sport.Soccer );
                break;
            case 2:
                toggleVisibility(Sport.Baseball);
                currentlySelected = Sport.Baseball;
                currentLayer = layerMap.get( Sport.Baseball );
                break;
            case 3:
                toggleVisibility(Sport.Tennis);
                currentlySelected = Sport.Tennis;
                currentLayer = layerMap.get( Sport.Tennis );
                break;
            case 4:
                toggleVisibility(Sport.Frisbee);
                currentlySelected = Sport.Frisbee;
                currentLayer = layerMap.get( Sport.Frisbee );
                break;
            case 5:
                toggleVisibility(Sport.Volleyball);
                currentlySelected = Sport.Volleyball;
                currentLayer = layerMap.get( Sport.Volleyball );
                break;
            case 6:
                toggleVisibility(Sport.Skating);
                currentlySelected = Sport.Skating;
                currentLayer = layerMap.get( Sport.Skating );
                break;
        }

        try {
            if(currentLayer != null)
                currentLayer.addLayerToMap();
        }
        catch ( Exception e)
        {
            Log.e( ":::ERROR:::", e.toString() );
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

                Location l = mMap.getMyLocation(); // This is terrible
                LatLng latLng = new LatLng(l.getLatitude(), l.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                return true;

            case R.id.action_change_sport:
                return true;
            case R.id.action_refresh:
                eventManager.updateTable();
                eventManager.getAllEvents();
                new LongOperation().execute("");

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
                    mMap.addMarker(mkrOpt.position(eloc).title(e.type + ": " + e.description).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
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
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://dagnss.com.parksfinder/http/host/path")
        );
        AppIndex.AppIndexApi.end( client, viewAction );
        client.disconnect();
    }

    public void toggleVisibility( Sport sport )
    {
        ArrayList<Marker> sportList = sportsMap.get( sport );
        for(Marker m : sportsMap.get(currentlySelected))
        {
            m.setVisible(false);
        }

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
            Marker newMarker = mMap.addMarker( mkrOpt.position(pos).title("Type: " + sportType) );

            list.add( newMarker );
        }
    }
}
