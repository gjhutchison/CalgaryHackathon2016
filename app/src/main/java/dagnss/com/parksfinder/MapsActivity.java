package dagnss.com.parksfinder;

import dagnss.com.eventsDB.*;

import android.net.Uri;
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
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MobileServiceClient mClient;
    private EditText result;

    private Event_Manager eventManager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buttons, menu);
        initSportMenu(menu.findItem(R.id.action_change_sport));
        return true;

    }

    private void initToolBar() {
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
    public boolean onOptionsItemSelected(MenuItem item)

    {


        int result = item.getItemId();
        switch(result)
        {
            case R.id.action_change_location:
              /* result = (EditText) findViewById(R.id.action_change_location);*/
                EditText but_location = (EditText) findViewById(R.id.location_input);
                if(but_location.getVisibility()== View.INVISIBLE) {
                    but_location.setVisibility(View.VISIBLE);
                }
                else
                    but_location.setVisibility(View.INVISIBLE);

                return true;

            case R.id.action_change_sport:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
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
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://dagnss.com.parksfinder/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
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
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
