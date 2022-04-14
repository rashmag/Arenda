package com.application.arenda.Lenta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.arenda.Ads.LentaAds;
import com.application.arenda.Model.ModelAll;
import com.application.arenda.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchNearbyMapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, LocationListener {
    private View v;
    private ImageView icGps_Nearby,arrow_back_imageView;
    private CircleImageView adsImageMarker;
    private View view;
    private androidx.appcompat.widget.SearchView searchViewNearbyMaps;
    private BottomSheetBehavior bottomSheetBehavior;
    private GoogleMap mMap;
    private CircleOptions circleOptions;
    private Circle circle;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private TextView nameAdsMarker,addressAds;
    private boolean flag,flagCircle;
    private static final int REQUEST_CODE_GOOGLE_PLAY_SERVECES_ERROR = -1;
    private static final double EARTH_RADIOUS = 3958.75;
    private static final int METER_CONVERSION = 1609;
    private Task<LocationSettingsResponse> response;
    private LocationRequest locationRequest;
    private ModelAll modelAll;
    private static final String TAG = "MapsActivity";
    private Task<Location> locationTask;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private HashMap<String, Marker> hashMapMarker;
    private String latitude = "", longitude = "";
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private LatLng latLng1;
    private ViewPager2 viewPager2;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MotionLayout motionLayout;
    private LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                Log.d(TAG, "OnLocationResuil = " + location.toString());
            }
        }
    };

    public SearchNearbyMapsFragment(ViewPager2 viewPager2,MotionLayout motionLayout) {
        this.viewPager2 = viewPager2;
        this.motionLayout = motionLayout;
    }

    public SearchNearbyMapsFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search_nearby_maps, container,
                false);
        init();
        searchViewNearbyMaps.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    firebaseSearch(query);
                }else{
                    checkingLocationInTheCircle();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    firebaseSearch(newText);
                }else {
                    checkingLocationInTheCircle();
                }
                return false;
            }
        });
        bottomSheetBehavior = BottomSheetBehavior.from(view);
//        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                switch (newState){
//                    case BottomSheetBehavior.STATE_COLLAPSED:
//                        stateTextView2.setText("COLLAPSED");
//                        break;
//                    case BottomSheetBehavior.STATE_EXPANDED:
//                        stateTextView2.setText("EXPANDED");
//                        break;
//                    case BottomSheetBehavior.STATE_DRAGGING:
//                        stateTextView2.setText("DRAGGING");
//                        break;
//                    case BottomSheetBehavior.STATE_HIDDEN:
//                        stateTextView2.setText("HIDDEN");
//                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                        break;
//                    case BottomSheetBehavior.STATE_SETTLING:
//                        stateTextView2.setText("SETTLING");
//                        break;
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });
        adsImageMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelAll != null) {
                    Intent intent = new Intent(getActivity(), LentaAds.class);
                    intent.putExtra("postId", modelAll.getPostId());
                    intent.putExtra("publisherId", modelAll.getPublisher());
                    startActivity(intent);
                }
            }
        });
        arrow_back_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(0);
            }
        });

        return v;
    }
    private void firebaseSearch(String searchText) {
        String query = searchText.toLowerCase();
        Query firebaseQuery = databaseReference.orderByChild("search").startAt(query).
                endAt(query + "\uf8ff");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ModelAll modelAll = snapshot1.getValue(ModelAll.class);
                    if(modelAll.getName() != null) {
                        if(hashMapMarker.get(modelAll.getName()) != null) {
                            Marker marker = hashMapMarker.get(modelAll.getName());
                            marker.remove();
                            hashMapMarker.remove(modelAll.getName());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ModelAll modelAll = snapshot1.getValue(ModelAll.class);
                    if(modelAll.getSearch() != null){
                        if(modelAll.getLat() != 0 && modelAll.getLon() != 0){
                            Marker markerName = null;
                            float[] distance = new float[2];
                            Location.distanceBetween( modelAll.getLat(),modelAll.getLon(),
                                    circle.getCenter().latitude, circle.getCenter().longitude,
                                    distance);
                            LatLng latLngProduct = new LatLng(modelAll.getLat(),
                                    modelAll.getLon());
                            if( distance[0] > circle.getRadius() ){
                                if (hashMapMarker.get(modelAll.getName()) != null) {
                                    Log.d("name1", "Снаружи = " + hashMapMarker.get(modelAll.getName()));
                                    Marker marker = hashMapMarker.get(modelAll.getName());
                                    marker.remove();
                                    hashMapMarker.remove(modelAll.getName());
                                }
                            } else {
                                if(hashMapMarker.get(modelAll.getName()) == null) {
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLngProduct)
                                    );
                                    marker.setTag(modelAll);
                                    hashMapMarker.put(modelAll.getName(), marker);
                                    Log.d("name1", "Внутри = " + hashMapMarker.get(modelAll.getName()));
                                }
                            }
                        }
                        Log.d("searchTest",modelAll.getSearch() + "");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseRecyclerOptions<ModelAll> options =
                new FirebaseRecyclerOptions.Builder<ModelAll>()
                        .setQuery(firebaseQuery, ModelAll.class)
                        .build();
    }

    private void checkingLocationInTheCircle() {
        databaseReference = database.getReference("Post");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ModelAll modelAll = snapshot1.getValue(ModelAll.class);
                        if (modelAll.getLat() != 0 && modelAll.getLon() != 0) {
                            Marker markerName = null;
                            float[] distance = new float[2];
                            Location.distanceBetween(modelAll.getLat(), modelAll.getLon(),
                                    circle.getCenter().latitude, circle.getCenter().longitude,
                                    distance);
                            LatLng latLngProduct = new LatLng(modelAll.getLat(),
                                    modelAll.getLon());
                            if (distance[0] > circle.getRadius()) {
                                if (hashMapMarker.get(modelAll.getName()) != null) {
                                    Log.d("name1", "Снаружи = " + hashMapMarker.get(modelAll.getName()));
                                    Marker marker = hashMapMarker.get(modelAll.getName());
                                    marker.remove();
                                    hashMapMarker.remove(modelAll.getName());
                                }
                            } else {
                                if(TextUtils.isEmpty(searchViewNearbyMaps.getQuery())) {
                                    if (hashMapMarker.get(modelAll.getName()) == null) {
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(latLngProduct)
                                        );
                                        marker.setTag(modelAll);
                                        hashMapMarker.put(modelAll.getName(), marker);
                                        Log.d("name1", "Внутри = " + hashMapMarker.get(modelAll.getName()));
                                    }
                                }else {
                                    firebaseSearch(searchViewNearbyMaps.getQuery() + "");
                                }
                            }
                            Log.d("latLon", "Lat = " + modelAll.getLat() + " Lon = " +
                                    modelAll.getLon() + "Name = " + modelAll.getName());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }

    @Override
    public void onResume() {
        super.onResume();

        Handler handlerMotion = new Handler();
        handlerMotion.removeCallbacksAndMessages(null);
        handlerMotion.postDelayed(new Runnable() {
            @Override
            public void run() {
                motionLayout.transitionToEnd();
            }
        }, 100);

        Handler handler = new Handler();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("text",viewPager2.isUserInputEnabled() + "");
                viewPager2.setUserInputEnabled(false);
            }
        }, 500);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().
                    getApplicationContext());
            if (status != ConnectionResult.SUCCESS) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(),
                        REQUEST_CODE_GOOGLE_PLAY_SERVECES_ERROR);
                dialog.show();
            }
            hashMapMarker = new HashMap<>();
            database = FirebaseDatabase.getInstance();

            icGps_Nearby.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkSettingsAndStartLocationUpdates("btn");
                }
            });
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(4000);
            locationRequest.setFastestInterval(2000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mapFragment.getMapAsync(this);
        }
    }
    private void init() {
        searchViewNearbyMaps = v.findViewById(R.id.searchViewNearbyMaps);
        view = v.findViewById(R.id.bottom_sheet);
        addressAds = v.findViewById(R.id.addressAds);
        nameAdsMarker = v.findViewById(R.id.nameAdsMarker);
        arrow_back_imageView = v.findViewById(R.id.arrow_back_imageView);
        adsImageMarker = v.findViewById(R.id.adsImageMatker);
        icGps_Nearby = v.findViewById(R.id.icGps_Nearby);
    }



    public double distanceFrom(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = EARTH_RADIOUS * c;
        return new Double(dist * METER_CONVERSION)
                .floatValue();
    }
    public double calculateVisibleRadius() {
        float[] distanceWidth = new float[1];
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        LatLng farRight = visibleRegion.farRight;
        LatLng farLeft = visibleRegion.farLeft;
        LatLng nearRight = visibleRegion.nearRight;
        LatLng nearLeft = visibleRegion.nearLeft;
        //calculate the distance between left <-> right of map on screen
        Location.distanceBetween( (farLeft.latitude + nearLeft.latitude) / 2,
                farLeft.longitude, (farRight.latitude + nearRight.latitude) / 2,
                farRight.longitude, distanceWidth );
        // visible radius is / 2  and /1000 in Km:
        circle.setRadius(distanceWidth[0] / 5);
        return distanceWidth[0] / 2 / 1000 ;
    }

    private void checkSettingsAndStartLocationUpdates(String btn) {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());

        response = settingsClient.checkLocationSettings(request);
        response.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates(btn);
            }
        });
        response.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(getActivity(), 1001);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettingsAndStartLocationUpdates("btn");
//                enableUserLocation();
//                zoomToUserLocation();
            } else {
                //We can show a dialog that permission is not granted...
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(callback);
    }

    private void startLocationUpdates(String btn) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates
                (locationRequest, callback, Looper.getMainLooper());

        zoomToUserLocation(btn);
    }

    private void zoomToUserLocation(String btn) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationTask = fusedLocationProviderClient.getLastLocation();
        if (locationTask != null) {
            locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = (Location) task.getResult();
                    if (task.isSuccessful()) {
                        if (location != null) {
                            if (location.getLatitude() != 0 && location.getLatitude() != 0) {
                                latLng1 = new LatLng(location.getLatitude(), location.getLongitude());

//                                double lat = mMap.getCameraPosition().target.latitude;
//                                double lon = mMap.getCameraPosition().target.longitude;
                                if (btn.equals("btn")) {

                                } else {
                                    circleOptions = new CircleOptions()
                                            .center(latLng1)
                                            .radius(1000)
//                                            .fillColor(Color.parseColor("#30000000"))
//                                            .strokeColor(Color.RED);
                                            .strokeColor(Color.TRANSPARENT);
// In meters
                                    circle = mMap.addCircle(circleOptions);
                                }
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15f));
                            }
                        }
                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                    LatLng latLngProduct = new LatLng(42.983548,47.504035);
                    circleOptions = new CircleOptions()
                            .center(latLngProduct)
                            .radius(1000)
//                                            .fillColor(Color.parseColor("#30000000"))
//                                            .strokeColor(Color.RED);
                            .strokeColor(Color.TRANSPARENT);
// In meters
                    circle = mMap.addCircle(circleOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngProduct, 15f));
                    flagCircle = true;
                }
            });
        }
    }
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.
                    loadRawResourceStyle(getActivity(), R.raw.map_style));
        } catch (Resources.NotFoundException e) {

        }
        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                final int[] count = {0};
                modelAll = (ModelAll) marker.getTag();
                nameAdsMarker.setText(modelAll.getName());
                addressAds.setText(modelAll.getAddress());

                DatabaseReference referencePost = FirebaseDatabase.getInstance().
                        getReference("Post").
                        child(modelAll.getPostId()).child("arrayimagesurl");
                referencePost.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            if(count[0] != -1000){
                                count[0]++;
                                if(count[0] == 1){
                                    Glide.with(getApplicationContext())
                                            .load(String.valueOf(String.
                                                    valueOf(snapshot1.child("adsurl").
                                                            getValue())))
                                            .into(adsImageMarker);
//                        holder.setImageView(getActivity().getApplication(),
//                                String.valueOf(snapshot1.child("adsurl").getValue()));
                                }
                                if(count[0] == 2){
                                    count[0] = -1000;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            checkSettingsAndStartLocationUpdates("oncreate");
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We can show user a dialog why this permission is necessary
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }
        }
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if(flagCircle) {
                    calculateVisibleRadius();

                    double lat = mMap.getCameraPosition().target.latitude;
                    double lon = mMap.getCameraPosition().target.longitude;
                    LatLng latLng = new LatLng(lat, lon);
                    circle.setCenter(latLng);
                }

//                checkingLocationInTheCircle();
//                float zoomLevel = mMap.getCameraPosition().zoom;
//                VisibleRegion visibleRegion = mMap.getProjection().
//                        getVisibleRegion();
//                LatLng nearLeft = visibleRegion.nearLeft;
//                LatLng nearRight = visibleRegion.nearRight;
//                LatLng farLeft = visibleRegion.farLeft;
//                LatLng farRight = visibleRegion.farRight;
//                double dist_w = distanceFrom(nearLeft.latitude,
//                        nearLeft.longitude, nearRight.latitude, nearRight.longitude);
//                double dist_h = distanceFrom(farLeft.latitude, farLeft.longitude,
//                        farRight.latitude, farRight.longitude);
//                Log.d("testDistance", "DISTANCE WIDTH: " +
//                        dist_w/2 + " DISTANCE HEIGHT: " + dist_h/2);
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(flagCircle){
                    calculateVisibleRadius();
                    double lat = mMap.getCameraPosition().target.latitude;
                    double lon = mMap.getCameraPosition().target.longitude;
                    LatLng latLng = new LatLng(lat, lon);
                    circle.setCenter(latLng);
                    checkingLocationInTheCircle();
                }
//                if(!flag){
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            calculateVisibleRadius();
//                            double lat = mMap.getCameraPosition().target.latitude;
//                            double lon = mMap.getCameraPosition().target.longitude;
//                            LatLng latLng = new LatLng(lat,lon);
//                            circle.setCenter(latLng);
//                        }
//                    }, 2500);
//                    flag = true;
//                }else {
//                }
            }
        });
//        LatLng latLngProduct = new LatLng(42.983548,47.504035);
//
//        mMap.addMarker(new MarkerOptions()
//                .position(latLngProduct)
//        );

// Get back the mutable Circle

        MapsInitializer.initialize(getActivity());
    }
}