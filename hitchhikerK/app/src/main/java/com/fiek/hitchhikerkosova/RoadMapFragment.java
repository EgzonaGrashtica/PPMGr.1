package com.fiek.hitchhikerkosova;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.fiek.hitchhikerkosova.ui.MainPostsFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoadMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoadMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "from";
    private static final String ARG_PARAM2 = "to";

    // TODO: Rename and change types of parameters
    private String from;
    private String to;


    GoogleMap googleMap;
    Location myLocation=null;
    Location destinationLocation=null;
    private LatLng fromLatLng;
    private LatLng toLatLng;

    final static LatLng PRISHTINA_LATLNG=new LatLng(42.6629, 21.1655);
    final static LatLng MITROVICA_LATLNG=new LatLng(42.8914, 20.8660);
    final static LatLng PEJA_LATLNG=new LatLng(42.6593, 20.2887);
    final static LatLng GJAKOVA_LATLNG=new LatLng(42.3844, 20.4285);
    final static LatLng FERIZAJ_LATLNG=new LatLng(42.3697, 21.1563);
    final static LatLng PRIZRENI_LATLNG=new LatLng(42.2171, 20.7436);
    final static LatLng GJILANI_LATLNG=new LatLng(42.4635, 21.4694);


    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission=false;

    private List<Polyline> polylines=null;

    public RoadMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoadMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoadMapFragment newInstance(String from, String to) {
        RoadMapFragment fragment = new RoadMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, from);
        args.putString(ARG_PARAM2, to);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            from = getArguments().getString(ARG_PARAM1);
            to = getArguments().getString(ARG_PARAM2);
        }
        switch (from){
            case "Prishtina":
                fromLatLng=PRISHTINA_LATLNG;
                break;
            case "Prizreni":
                fromLatLng=PRIZRENI_LATLNG;
                break;
            case "Ferizaj":
                fromLatLng=FERIZAJ_LATLNG;
                break;
            case "Pejë":
                fromLatLng=PEJA_LATLNG;
                break;
            case "Gjakovë":
                fromLatLng=GJAKOVA_LATLNG;
                break;
            case "Gjilan":
                fromLatLng=GJILANI_LATLNG;
                break;
            case "Mitrovicë":
                fromLatLng=MITROVICA_LATLNG;
                break;
        }
        switch (to){
            case "Prishtina":
                toLatLng=PRISHTINA_LATLNG;
                break;
            case "Prizreni":
                toLatLng=PRIZRENI_LATLNG;
                break;
            case "Ferizaj":
                toLatLng=FERIZAJ_LATLNG;
                break;
            case "Pejë":
                toLatLng=PEJA_LATLNG;
                break;
            case "Gjakovë":
                toLatLng=GJAKOVA_LATLNG;
                break;
            case "Gjilan":
                toLatLng=GJILANI_LATLNG;
                break;
            case "Mitrovicë":
                toLatLng=MITROVICA_LATLNG;
                break;
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_road_map, container, false);
        requestPermision();

        SupportMapFragment supportMapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.roadMapFragment);
        supportMapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        Findroutes(fromLatLng,toLatLng);

    }
    private void requestPermision()
    {
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission=true;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if permission granted.
                    locationPermission=true;
                    Findroutes(fromLatLng,toLatLng);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

   /* private void getMyLocation(){
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                myLocation=location;
                LatLng ltlng=new LatLng(location.getLatitude(),location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        ltlng, 16f);
                googleMap.animateCamera(cameraUpdate);
            }
        });

        //get destination location when user click on map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                to=latLng;

                googleMap.clear();

                from=new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                //start route finding
                Findroutes(from,to);
            }
        });

    }*/


    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(getContext(),"Unable to get location",Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(getString(R.string.google_map_key)) //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Toast.makeText(getContext(),"Finding Route Failed...",Toast.LENGTH_LONG).show();
        Findroutes(fromLatLng,toLatLng);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(getContext(),"Finding Route...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                fromLatLng, 16f);
        googleMap.animateCamera(cameraUpdate);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;

        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {
                if(i==shortestRouteIndex){
                polyOptions.color(getResources().getColor(R.color.colorAccent));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = googleMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);
                }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");
        googleMap.addMarker(startMarker);


        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        googleMap.addMarker(endMarker);
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(fromLatLng,toLatLng);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(fromLatLng,toLatLng);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
