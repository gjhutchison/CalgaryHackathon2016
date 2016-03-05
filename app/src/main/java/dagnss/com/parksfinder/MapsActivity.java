package dagnss.com.parksfinder;

import dagnss.com.eventsDB.*;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.windowsazure.mobileservices.*;



import java.net.MalformedURLException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MobileServiceClient mClient;
    LocationManager locationManager;
    LatLng currLoc;
    private Event_Manager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

	
        initToolBar();

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_buttons, menu);
        return true;

    }

    private void initToolBar()
    {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.showOverflowMenu();
        setSupportActionBar(myToolbar);
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
    public void onMapReady(GoogleMap googleMap) {
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
        //eventManager.createEvent("Hockey", "A brutal sport", currLoc.latitude, currLoc.longitude);
        MobileServiceList<events> e = eventManager.getEventBySport("Hockey");
        // Add a marker in Sydney and move the camera

        /*
        LatLng sydney = new LatLng(51.045, -114.057);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_change_location:
                return true;
            case R.id.action_change_sport:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
