package com.eestec_ekrit.wojtek.eestec;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks {

    // Request code for location permission request.
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    LatLng latLng;
    Marker currLocationMarker;
    LocationRequest mLocationRequest;
    ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    FloatingActionMenu FAM;
    com.github.clans.fab.FloatingActionButton FABloc1, FABloc2, FABloc3;
    ;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    /**
     * Flag indicating whether a request permission has been denied after returning in
     * onRequestPermissionsResult(int, String[], in[])
     */
    private boolean mPermissionDenied = false;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
//        FloatingActionButton fab_my_position = (FloatingActionButton) findViewById(R.id.fab_my_position);
        FAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        FABloc1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        FABloc2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        FABloc3 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);

//        fab_my_position.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocationMarker.getPosition(), 16));
//            }
//        });

        FABloc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng origin = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                LatLng destination = new LatLng(50.061092, 19.932064); // hostel

                String url = getDirectionsUrl(origin, destination);
                DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        });

        FABloc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng origin = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                LatLng destination = new LatLng(50.065616, 19.921565); // uczelnia
                String url = getDirectionsUrl(origin, destination);
                DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        });

        FABloc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng origin = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                LatLng destination = new LatLng(50.067270, 19.947413); // dworzec
                String url = getDirectionsUrl(origin, destination);
                DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        });
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        if (markerPoints.size() >= 1) {
            markerPoints.clear();
            mMap.clear();
        }
        //                    Log.d("marker points",  String.valueOf(markerPoints.size()));
        markerPoints.add(origin);
        // Adding new item to the ArrayList
        markerPoints.add(dest);

        //Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
        MarkerOptions options_myLoc = new MarkerOptions();

        //Setting the position of the marker
        options.position(dest);
        options_myLoc.position(origin);

        // start location - red, end location - green

        options_myLoc.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        // Add new marker to map
        mMap.addMarker(options_myLoc);
        mMap.addMarker(options);

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // travel mode
        String travel_mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + travel_mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.d("maps url", url);

        return url;
    }

    /**
     * A method to download json data from url
     */

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();
        } catch (Exception e) {
            Log.d("E while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        Log.d("download url", data);
        return data;
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            // Access to the location has been granted to the app
            mMap.setMyLocationEnabled(true);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // if request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    enableMyLocation();
                } else {
                    // permission was denied
                    mPermissionDenied = true;
                }
                return;
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        turn_GPS_on();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            mPermissionDenied = false;
            showMessageOK("You need to allow Location service.",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                            }
                            finish();
                            startActivity(getIntent());
                        }
                    });
        }

    }

    /**
     * Displays dialog with error message explaining that the location permission is missing.
     */

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showMessageOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .create()
                .show();
    }

    private void turn_GPS_on() {
        int GPSoff = 0;
        try {
            GPSoff = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            Log.d("EESTEC", "turn_GPS_on: " + GPSoff);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (GPSoff == 0) {
            showMessageOKCancel("You need to turn Location on",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(onGPS);
                        }
                    });
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            if (currLocationMarker != null) {
                currLocationMarker.remove();
            }
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
            //zoom to current position:
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); // 5 seconds
        mLocationRequest.setFastestInterval(3000); // 3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(0.1f); // 1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
//        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        //mMap.clear();
        mLastLocation = location;
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currLocationMarker = mMap.addMarker(markerOptions);

//        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();


        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    //Fetch data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        //Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            //For storing data from web service
            String data = "";
            try {
                //Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        //Executes in UI thread, after the execution of doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();

            //Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                //Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        //Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            points = new ArrayList<LatLng>();

            //Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {

                lineOptions = new PolylineOptions();

                //Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                //Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                //Adding all the points in the route to LineOptions
//                lineOptions.addAll(points);
//                lineOptions.width(2);
//                lineOptions.color(Color.CYAN);
            }
            Log.d("onPostExecute", lineOptions.toString());
            lineOptions.addAll(points);
            lineOptions.width(20);
            lineOptions.color(Color.BLUE);
            //Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

}


// TODO: 06.01.2017 3 - kolory pasków na bardziej eestecowe

