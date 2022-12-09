package edu.cuhk.bmeg3130.stepsfrompath;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
// Importing Arrays class from the utility class
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = "StepsFromPath";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String url = "http://www.cse.cuhk.edu.hk/~ypchui/bmeg3130/asg2/walk1.json";
//    possible extension: setup a web-server on the PC and publize the json for reading
//    private String url = "http://10.0.2.2:5000/me"; // localhost on Android, like 127.0.0.1 in PC

    // TODO: add extra attributes here

    double [] mLats, mLngs;
    Button mButton1;
    TextView mTextDistance, mTextGender, mTextHeight, mTextPace, mTextSteps;
    // End of class attributes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mRequestQueue = Volley.newRequestQueue(this);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonParse();
            }
        });

        // TODO:
        //  done 1. Add UI widgets into "activity_maps.xml" via Layout Editor
        //  done     a. Add a Button below the map, and add onClick event handling to call jsonParse()
        //  done     b. Add 5 TextView below the Button for display info
        //  done 2. get reference of UI objects below for later update
        // End of your UI setup

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        // obtain a reference to the map object, use this to update map
        mMap = googleMap;

        // Default marker and camera zoom, you don't have to modify the following
        // Add a marker in Campus and move the camera
        LatLng home = new LatLng(22.419871, 114.206169);
        mMap.addMarker(new MarkerOptions().position(home).title("Marker in CUHK"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 15));
    }

    /**
     *  To be invoked by a button click
     *  To parse a json obtained from server to draw a path on map and display step info
     */
    private void jsonParse() {
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Double> latList = null;
                        ArrayList<Double> lngList = null;
                        try {
                            int distance = response.getInt("distance");
                            String gender = response.getString("gender");
                            int height = response.getInt("height");
                            String pace = response.getString("pace");
                            JSONArray lat = response.getJSONArray("Lat");
                            latList = new ArrayList<Double>();
                            if (lat != null) {
                                for (int i = 0; i < lat.length(); i++) {
                                    latList.add(lat.getDouble(i));
                                }
                            }
                            JSONArray lng = response.getJSONArray("Lng");
                            lngList = new ArrayList<Double>();
                            if (lng != null) {
                                for (int i = 0; i < lng.length(); i++) {
                                    lngList.add(lng.getDouble(i));
                                }
                            }

                            // calculation of step
                            double step = 0;
                            if (pace.equals("Walk")) {
                                if (gender.equals("Male")) {
                                    System.out.println("USing this shit fuck");
                                    step = (1916 + (63.4 * 9) - (14.1 * height / 2.54)) * distance / 1609;
                                } else if (gender.equals("Female")) {
                                    step = (1949 + 63.4 * 9 - 14.1 * height / 2.54) * distance / 1609;
                                }
                            } else if (pace.equals("Jog")) {
                                if (gender.equals("Male")) {
                                    step = (1916 + 63.4 * 12 - 14.1 * height / 2.54) * distance / 1609;
                                } else if (gender.equals("Female")) {
                                    step = (1949 + 63.4 * 12 - 14.1 * height / 2.54) * distance / 1609;
                                }
                            }
                            int stepInt = (int) step;

                            System.out.println("printing stuff");
                            System.out.println(distance + gender + height + pace + step + latList + lngList);
                            System.out.println("end printing stuff");

                            TextView mTextDistance = (TextView) findViewById(R.id.textDistance);
                            TextView mTextGender = (TextView) findViewById(R.id.textGender);
                            TextView mTextHeight = (TextView) findViewById(R.id.textHeight);
                            TextView mTextPace = (TextView) findViewById(R.id.textPace);
                            TextView mTextSteps = (TextView) findViewById(R.id.textSteps);
                            mTextDistance.setText("Distance: " + distance);
                            mTextGender.setText("Gender: " + gender);
                            mTextHeight.setText("Height: " + height);
                            mTextPace.setText("Pace: " + pace);
                            mTextSteps.setText("Step: " + stepInt);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        PolylineOptions polylineOptions= new PolylineOptions().clickable(true);
                        for (int i=0; i<lngList.size(); i++){
                            polylineOptions.add(new LatLng(latList.get(i), lngList.get(i)));
                        }
                        Polyline polyline = mMap.addPolyline(polylineOptions);
                        polyline.setTag("A");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            lngList.sort(Comparator.naturalOrder());
                            latList.sort(Comparator.naturalOrder());
                        }

                        LatLngBounds australiaBounds = new LatLngBounds(
                                new LatLng(latList.get(0), lngList.get(0)), // SW bounds
                                new LatLng(latList.get(latList.size() - 1), lngList.get(lngList.size() - 1))  // NE bounds
                        );
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 100));

                        // TODO:
                        //   done 1. get the JSON fields, a try-catch block might be needed here
                        //   done   a. textual fields for display/calculation
                        //   done   b. Lat/Lng pairs for path drawing (via PolyLines)
                        //   done     make suitable function calls to "mMap"
                        //  2. zoom the map camera to enclose just the path and its padding
                        //     Hints: use Arrays.sort for finding bound of path
                        //  3. do steps calculation and display on TextView


                        // End of your implementation here
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        mRequestQueue.add(request);
    }
}