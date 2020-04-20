package com.example.abc;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Rides extends FragmentActivity implements OnMapReadyCallback ,TaskLoadedCallback{
    ArrayList markerPoints= new ArrayList();
    private GoogleMap mMap;
    ImageButton button1,button2;
    EditText searchSource,searchDestination;
    private FusedLocationProviderClient mLocationClient;
    LatLng source,destination;
    Polyline route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        searchSource = findViewById(R.id.editText);
        searchDestination = findViewById(R.id.editText2);
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationName = searchSource.getText().toString();
                Geocoder geocoder = new Geocoder(Rides.this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);
                        source = new LatLng(address.getLatitude(), address.getLongitude());
                        markerPoints.add(source);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 12.0f));
                        Toast.makeText(Rides.this, address.getLocality(), Toast.LENGTH_SHORT).show();
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title("Pickup here"));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationName = searchDestination.getText().toString();
                Geocoder geocoder = new Geocoder(Rides.this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);
                        destination = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(source).title("Pickup here"));
                        markerPoints.add(destination);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 12.0f));
                        Toast.makeText(Rides.this, address.getLocality(), Toast.LENGTH_SHORT).show();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title("Your destination"));
                        drawRoutes();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public String getUrl(LatLng origin,LatLng dest,String directionMode)
    {
        //Origin of route
        String str_origin="origin="+origin.latitude+","+origin.longitude;
        //Destination of route
        String str_dest="destination="+dest.latitude+","+dest.longitude;
        //Mode
        String mode="mode="+directionMode;
        //Building parameters
        String parameters=str_origin+"&"+str_dest+"&"+mode;
        //Output format
        String output="json";
        //Building the url to the web service
        String url="https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters+"&key="+getString(R.string.google_maps_key);
        //String url ="https://maps.googleapis.com/maps/api/directions/json?origin=Lucknow&destination=Lakhimpur&key="+"AIzaSyCRedcX80cUN0eUug3UC3-dY-vckf3Zryg";
        return url;
    }
    public void drawRoutes(){

        MarkerOptions place1,place2;
        place1=new MarkerOptions().position(new LatLng(source.latitude,source.longitude)).title("Source");
        place2=new MarkerOptions().position(new LatLng(destination.latitude,destination.longitude)).title("Destination");

        String url=getUrl(place1.getPosition(),place2.getPosition(),"driving");
        new FetchURL(Rides.this).execute(url,"driving");


       /*Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
         .clickable(true)
         .add(source,destination));*/

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10.0f));

   }

    public void confirm(View view)
    {
        startActivity(new Intent(getApplicationContext(),DriverDetails.class));
    }

    @Override

    public void onTaskDone(Object... values) {
        if (route != null)
            route.remove();
        route = mMap.addPolyline((PolylineOptions) values[0]);
    }
}



















