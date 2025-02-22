package com.fiek.hitchhikerkosova.fragments;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.fiek.hitchhikerkosova.map.MyClusterItem;
import com.fiek.hitchhikerkosova.R;
import com.fiek.hitchhikerkosova.map.MyClusterRenderer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;


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

    private List<MyClusterItem> myClusterItemList=new ArrayList<MyClusterItem>();
    private GoogleMap googleMap;
    private ClusterManager clusterManager;
    private LatLng fromLatLng;
    private LatLng toLatLng;
    private List<Polyline> polylines=null;

    final static LatLng PRISHTINA_LATLNG=new LatLng(42.6629, 21.1655);
    final static LatLng MITROVICA_LATLNG=new LatLng(42.8914, 20.8660);
    final static LatLng PEJA_LATLNG=new LatLng(42.6593, 20.2887);
    final static LatLng GJAKOVA_LATLNG=new LatLng(42.3844, 20.4285);
    final static LatLng FERIZAJ_LATLNG=new LatLng(42.3697, 21.1563);
    final static LatLng PRIZRENI_LATLNG=new LatLng(42.2171, 20.7436);
    final static LatLng GJILANI_LATLNG=new LatLng(42.4635, 21.4694);



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

        SupportMapFragment supportMapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.roadMapFragment);
        supportMapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.MapTitle));
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap=googleMap;
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
        clusterManager=new ClusterManager<>(getActivity(),googleMap);
        MyClusterRenderer myClusterRenderer=new MyClusterRenderer(getContext(),this.googleMap,clusterManager);
        clusterManager.setRenderer(myClusterRenderer);
        this.googleMap.setOnCameraIdleListener(clusterManager);
        this.googleMap.setOnMarkerClickListener(clusterManager);
        this.googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if(googleMap.getCameraPosition().zoom <8f){
                    polylines.get(0).setVisible(false);
                }else{
                    polylines.get(0).setVisible(true);
                }
            }
        });


        Findroutes(fromLatLng,toLatLng);
    }


    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(getContext(),R.string.toastUnableGetLocation,Toast.LENGTH_LONG).show();
        }
        else
        {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(getString(R.string.google_map_key))
                    .build();
            routing.execute();
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Toast.makeText(getContext(),getString(R.string.toastErrorRoutingFailed),Toast.LENGTH_LONG).show();
        Findroutes(fromLatLng,toLatLng);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(getContext(),getString(R.string.toastFindingRoute),Toast.LENGTH_LONG).show();
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
        myClusterItemList.add(new MyClusterItem(polylineStartLatLng,getString(R.string.tvFrom),""));

        //Add Marker on route ending position
        myClusterItemList.add(new MyClusterItem(polylineEndLatLng,getString(R.string.tvTo),""));
        clusterManager.addItems(myClusterItemList);

        clusterManager.cluster();
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(fromLatLng,toLatLng);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(fromLatLng,toLatLng);
    }

}
