package dagnss.com.parksfinder;

import dagnss.com.eventsDB.*;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.windowsazure.mobileservices.*;



import java.net.MalformedURLException;
import java.util.ArrayList;

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
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private MobileServiceClient mClient;

    private Event_Manager eventManager;

    private Sport currentlySelected = Sport.Soccer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//	    try{
//            mClient = new MobileServiceClient(
//                    "https://sportyevents.azure-mobile.net/",
//                    "AxZxcxblWqLJxImvALWxqJOIAdMrhe94",
//                    this
//            );
//            eventManager = new Event_Manager(mClient);
//        }catch(Exception e){
//            e.printStackTrace();
//        }

	
        initToolBar();

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_buttons, menu);
        initSportMenu(menu.findItem(R.id.action_change_sport));
        return true;

    }

    private void initToolBar()
    {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.showOverflowMenu();
        setSupportActionBar(myToolbar);
    }

    private void initSportMenu(MenuItem item)
    {
        ArrayList<ItemData> list=new ArrayList<>();
        list.add(new ItemData("Soccer",R.drawable.sport));
        list.add(new ItemData("Baseball",R.drawable.baseball));
        list.add(new ItemData("Tennis",R.drawable.tennis));
        list.add(new ItemData("Frisbee",R.drawable.frisbee));
        list.add(new ItemData("Basketball",R.drawable.basketball));
        list.add(new ItemData("Volleyball",R.drawable.volleyball));
        list.add(new ItemData("Skating",R.drawable.skating));

        Spinner sp=(Spinner) MenuItemCompat.getActionView(item);
        ImageSpinnerAdapter adapter=new ImageSpinnerAdapter(this,
                R.layout.image_spinner_layout,R.id.txt,list);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        /*
        LatLng sydney = new LatLng(51.045, -114.057);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int result = item.getItemId();
        switch(result)
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
