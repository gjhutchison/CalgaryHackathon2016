package dagnss.com.parksfinder;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlGeometry;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;
import com.google.maps.android.kml.KmlPolygon;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Sven on 2016-03-05.
 */
public class KMLParser
{
    private GoogleMap m_map;
    private Context m_mapContext;
    private KmlLayer mainLayer;
    private ArrayList<KmlPlacemark> m_TennisList = new ArrayList<>();
    private ArrayList<KmlPlacemark> m_DiamondList = new ArrayList<>();
    private ArrayList<KmlPlacemark> m_SoccerList = new ArrayList<>();
    private ArrayList<KmlPlacemark> m_FrisbeeList = new ArrayList<>();
    private ArrayList<KmlPlacemark> m_BasketballList = new ArrayList<>();
    private ArrayList<KmlPlacemark> m_VolleyballList = new ArrayList<>();
    private ArrayList<KmlPlacemark> m_IceSkateList = new ArrayList<>();

    //private KMLParser(){}
    public KMLParser( GoogleMap map, Context mapContext )
    {
        m_map = map;
        m_mapContext = mapContext;
    }

    public KmlLayer loadKML( int inputRes )
    {
        KmlLayer mainLayer;
        try
        {
            mainLayer = new KmlLayer( m_map, inputRes, m_mapContext );
            Log.i( "success", "Loaded KML Layer successfully." );
        } catch ( Exception e )
        {
            Log.e( "error", e.getMessage() );
            return null;
        }
        KmlContainer mainDoc = mainLayer.getContainers().iterator().next();
        KmlContainer mainContainer = mainDoc.getContainers().iterator().next();
        ArrayList<KmlPlacemark> placemarkList = new ArrayList<>();
        Iterable<KmlPlacemark> temp = mainContainer.getPlacemarks();

        for ( KmlPlacemark elem : temp )
        {
            placemarkList.add( elem );
        }

        populateLists( placemarkList );

        return mainLayer;
    }

    public void populateLists( Iterable<KmlPlacemark> placemarks )
    {
        for ( KmlPlacemark placemark : placemarks )
        {
            String assetType = getAssetType( placemark.getProperty( "description" ) );

            switch ( assetType )
            {
                case "SOCCER":
                    m_SoccerList.add( placemark );
                    break;
                case "TENNIS":
                    m_TennisList.add( placemark );
                    break;
                case "BALL DIAMOND INFIELD":
                case "BALL DIAMOND OUTFIELD":
                case "BALL DIAMOND DUGOUT":
                    m_DiamondList.add( placemark );
                    break;
                case "ULTIMATE FRISBEE":
                    m_FrisbeeList.add( placemark );
                    break;
                case "BASKETBALL":
                    m_BasketballList.add( placemark );
                    break;
                case "VOLLEYBALL":
                    m_VolleyballList.add( placemark );
                    break;
                case "ICE AREA":
                    m_IceSkateList.add( placemark );
                    break;
            }
        }
    }

    public ArrayList<KmlPlacemark> getFacilities( String facilityType )
    {
        switch ( facilityType )
        {
            case "SOCCER":
                return m_SoccerList;
            case "TENNIS":
                return m_TennisList;
            case "BALL DIAMOND INFIELD":
            case "BALL DIAMOND OUTFIELD":
            case "BALL DIAMOND DUGOUT":
                return m_DiamondList;
            case "ULTIMATE FRISBEE":
                return m_FrisbeeList;
            case "BASKETBALL":
                return m_BasketballList;
            case "VOLLEYBALL":
                return m_VolleyballList;
            case "ICE AREA":
                return m_IceSkateList;
            default:
                return new ArrayList<KmlPlacemark>();
        }
    }

    private String getAssetType( String description )
    {
        int startIndex = description.lastIndexOf( "ASSET_TYPE" );
        int endIndex = description.indexOf( "MATERIAL" );
        if ( endIndex <= startIndex || startIndex < 0 )
        {
            return "";
        }

        String shortened = description.substring( startIndex, endIndex );
        startIndex = shortened.lastIndexOf( "<td>" );
        endIndex = shortened.indexOf( "</td>" );
        return shortened.substring( startIndex + 4, endIndex );
    }

    public LatLng getLocation( KmlPlacemark placemark )
    {
        KmlGeometry geometry = placemark.getGeometry();
        KmlPolygon polygon;
        if ( geometry instanceof KmlPolygon )
        {
            polygon = ( KmlPolygon ) geometry;
        }
        else
        {
            return new LatLng( 0, 0 );
        }

        ArrayList<LatLng> boundaries = polygon.getOuterBoundaryCoordinates();

        double maxLat = -91;
        double maxLong = -181;
        double minLat = 91;
        double minLong = 181;

        for( LatLng elem : boundaries )
        {
            if( elem.latitude < minLat )
                minLat = elem.latitude;
            if( elem.longitude < minLong )
                minLong = elem.longitude;

            if( elem.latitude > maxLat )
                maxLat = elem.latitude;
            if( elem.longitude > maxLong )
                maxLong = elem.longitude;
        }

        double avgLat = ( minLat + maxLat ) / 2;
        double avgLong = ( minLong + maxLong ) / 2;
        LatLng avgPos = new LatLng( avgLat, avgLong );

        return avgPos;
    }
}
