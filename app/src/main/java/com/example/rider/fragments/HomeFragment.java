package com.example.rider.fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rider.R;
import com.example.rider.utils.Constaint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private GoogleMap mMap;

    TextView address;
    View mView;
    MapView mMapView;

    private GeoApiContext geoApiContext = null;
    private static MarkerOptions markerOptions;

    private Location location;
    private TextView latitudeTV, longitudeTV;
    private Location mUserPosition;
    private Double LatitudeTV, LongitudeTV;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds


    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private static final int LOCATION_UAPDATE_INTERVAL=3000;

    private ArrayList<String> mUserLocations = new ArrayList<>();
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private LatLng mylocation;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);


        mMapView = mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        markerOptions = new MarkerOptions()
                .title("My Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.loc));

        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(getActivity()).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();

        //We build google ApiContext
        if (geoApiContext==null)
        {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getResources().getString(R.string.google_directions_key))
                    .build();
        }

        return mView;

    }


    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        startLocationUpdates();
    }


    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getActivity(),"You need to enable permissions to display location !",Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }


    @Override
    public void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            Toast.makeText(getActivity(),"You need to install Google Play Services to use the App properly",Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        ///Get Longitude and Latitude Value

        if (location != null) {
            Constaint.CURRENT_LOCATION = location;
            LatitudeTV = location.getLatitude();
            LongitudeTV = location.getLongitude();
            Log.e("Location: ", LatitudeTV+" "+LongitudeTV);

//            if (utils.NEW_REQ != null && utils.is_busy_status==0){
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
//                        .setMessage(utils.NEW_REQ.getShopName() + "\n"+utils.NEW_REQ.getLocation()+"\nDelivery Address"+utils.NEW_REQ.getAddress())
//                        .setTitle("Request for Delivery")
//                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                utils.NEW_REQ = null;
//                            }
//                        }).setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                utils.is_busy_status = 1;
//                                changeCurrentIsBusyStatus(Common.currentUser.getId(), 1, utils.NEW_REQ.getId());
//                                Intent intent = new Intent(getActivity(), ScheduledService.class);
//                                getActivity().stopService(intent);
//                                Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                builder.show();
//
//            }

            ///Adding Marker in current Location
            mylocation = new LatLng(LatitudeTV, LongitudeTV);
            markerOptions.position(mylocation);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.clear();
            mMap.addMarker(markerOptions);

        }

        Geocoder geocoder;
        List<Address> addresses;
        //address = mView.findViewById(R.id.Address);

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(LatitudeTV, LongitudeTV, 2); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String add = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();

            // address.setText(add);





        } catch (IOException e) {

            Toast.makeText(getActivity(),"Couldn't Fetch your current location. Please turn on your location or internet!",Toast.LENGTH_SHORT).show();

        }


    }

//    private void changeCurrentIsBusyStatus(int id, final int status, final int order_id){
//        StringRequest stringRequest = new StringRequest(Request.Method.POST,
//                utils.CHANGE_BUSY_STATUS, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e( "onResponse: ", response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("id", String.valueOf(Common.currentUser.getId()));
//                params.put("status", String.valueOf(status));
//                params.put("order_id", String.valueOf(order_id));
//                return params;
//            }
//        };
//        Volley.newRequestQueue(getActivity()).add(stringRequest);
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String perm : permissionsToRequest) {
                if (!hasPermission(perm)) {
                    permissionsRejected.add(perm);
                }
            }

            if (permissionsRejected.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                        new AlertDialog.Builder(Objects.requireNonNull(getActivity())).
                                setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requestPermissions(permissionsRejected.
                                                toArray(new String[0]), ALL_PERMISSIONS_RESULT);
                                    }
                                }).setNegativeButton("Cancel", null).create().show();

                    }
                }
            }


            else
            {

                if (googleApiClient != null) {
                    googleApiClient.connect();
                }
            }
        }


    }

    ////////////////////////////////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private  boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                Objects.requireNonNull(getActivity()).finish();
            }

            return false;
        }

        return true;
    }
/////////////////////////////////////////////////////////////


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        CameraPosition position= new  CameraPosition.Builder().
//                target(mylocation).zoom(15).bearing(19).tilt(30).build();
//
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

    }
}