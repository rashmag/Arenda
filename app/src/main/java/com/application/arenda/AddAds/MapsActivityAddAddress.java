package com.application.arenda.AddAds;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.application.arenda.Ads.EditAds;
import com.application.arenda.MainActivity;
import com.application.arenda.R;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivityAddAddress extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, LocationListener {
    private String cityName, address, state, country, postalCode, knownName, street;
    private LottieAnimationView animationView;
    private Task<LocationSettingsResponse> response;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Place place;
    private String latitude = "", longitude = "";
    private Geocoder geocoder;
    Marker marker, marker1;
    private LatLng latLng1;
    //    private TextView textView;
    private ConstraintLayout
//            circleFrameLayout,
            frameLayout;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private ImageView ic_gps;
    //    private AutocompleteSupportFragment autocompleteSupportFragment;
    private List<Place.Field> arrays;
    private boolean isMoving = false;
    private boolean isAnimationDown = false;
    private ProgressBar progressBar;
    private Button selectedPlaceBtn;
    private static final float DEFAULT_ZOOM = 15f;
    private Task<Location> locationTask;
    private String putAddress;
    private TextView textViewAddress;
    private Intent intentPlace;
    private ImageView searchView;
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
    private String parent,adsPhotoUrl,postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Intent intent = getIntent();
        parent = intent.getStringExtra("parent");
        adsPhotoUrl = intent.getStringExtra("adsPhotoUrl");
        postId = intent.getStringExtra("postId");
        init();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBpXH-V8MsgVnv3E2cD0kyTR20SLq3UvOM");
        }
        PlacesClient placesClient = Places.createClient(this);
        arrays = Arrays.asList(Place.Field.ADDRESS, Place.Field.ID,
                Place.Field.NAME, Place.Field.LAT_LNG);
//        autocompleteSupportFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentAutoComplete();
            }
        });
        textViewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentAutoComplete();
            }
        });
//        autocompleteSupportFragment.setCountries("RU");
//        autocompleteSupportFragment.setPlaceFields(arrays);
//        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                final LatLng latLng = place.getLatLng();
//
////                textViewAddress.setText(place.getAddress());
//
//                longitude = String.valueOf(place.getLatLng().longitude);
//                latitude = String.valueOf(place.getLatLng().latitude);
//                mMap.clear();
////                mMap.addMarker(new MarkerOptions().icon(
////                        BitmapDescriptorFactory.fromResource(R.drawable. )).position(place.getLatLng())
////                        .title(place.getName()));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15f));
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//                Log.d("test", "error");
//            }
//        });
        ic_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSettingsAndStartLocationUpdates();
            }
        });
        selectedPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(textViewAddress.getText().toString())) {
                    double lat = mMap.getCameraPosition().target.latitude;
                    double lon = mMap.getCameraPosition().target.longitude;
                    if(parent.equals("addAds")) {
                        intentPlace = new Intent(MapsActivityAddAddress.this,
                                MainActivity.class);
                        intentPlace.putExtra("fragmentSelect", "AddAds");
                        intentPlace.putExtra("lat", lat);
                        intentPlace.putExtra("lon", lon);
                        intentPlace.putExtra("place",
                                textViewAddress.getText().toString());
                        startActivity(intentPlace);
                    }else if(parent.equals("editAds")){
                        intentPlace = new Intent(MapsActivityAddAddress.this,
                                EditAds.class);
                        intentPlace.putExtra("fragmentSelect", "AddAds");
                        intentPlace.putExtra("lat", lat);
                        intentPlace.putExtra("lon", lon);
                        intentPlace.putExtra("postId", postId);
                        intentPlace.putExtra("adsPhotoUrl", adsPhotoUrl);
                        intentPlace.putExtra("place", textViewAddress.getText().toString());
                        startActivity(intentPlace);
                    }
                }else{
                    Toast.makeText(MapsActivityAddAddress.this, "Выберите местоположениеё", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void intentAutoComplete() {
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                arrays).build(MapsActivityAddAddress.this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void init() {
        textViewAddress = findViewById(R.id.textViewAddress);
        searchView = findViewById(R.id.searchViewMaps);
        selectedPlaceBtn = findViewById(R.id.selectedPlaceBtn);
        ic_gps = findViewById(R.id.ic_gps);
//        circleFrameLayout = findViewById(R.id.pin_view_circle);
        frameLayout = findViewById(R.id.mapContainer);
        progressBar = findViewById(R.id.profile_loader);
//        textView = findViewById(R.id.textView);
        animationView = findViewById(R.id.animationView);
//        textViewAddress = findViewById(R.id.textViewAddress);
//        searchView = findViewById(R.id.searchView);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.
                    loadRawResourceStyle(this, R.raw.map_style));
        } catch (Resources.NotFoundException e) {

        }
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        mMap.setOnMapLongClickListener(this);
//        mMap.setOnMarkerDragListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            checkSettingsAndStartLocationUpdates();
//            enableUserLocation();
//            zoomToUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We can show user a dialog why this permission is necessary
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }
        }
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                isMoving = true;
//                textView.setVisibility(View.GONE);
//                Toast.makeText(MapsActivity.this, "progress = " +
//                        animationView.getProgress(), Toast.LENGTH_SHORT).show();
                if (animationView.getProgress() >= 0.5f) {
                    animationView.setMinAndMaxProgress(0, 0.5f);
                    animationView.playAnimation();
                    animationView.loop(false);
                }
                progressBar.setVisibility(View.GONE);

//                Toast.makeText(MapsActivity.this, "response = " + response.isSuccessful()
//                        , Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    findAddress();
                }
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                isMoving = false;
//                textView.setVisibility(View.INVISIBLE);
//                Toast.makeText(MapsActivity.this, "progressId = " + animationView.getProgress(),
//                        Toast.LENGTH_SHORT).show();
                if (!isAnimationDown) {
                    if (animationView.getProgress() <= 0.5f) {
                        animationView.setMinAndMaxProgress(0.5f, 1);
                        animationView.playAnimation();
                        animationView.loop(false);
                    }
                    isAnimationDown = true;
                }


                progressBar.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    findAddress();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isMoving) {
//                            circleFrameLayout.setBackground(wDrawable);
//                            textView.setVisibility(View.VISIBLE);
//                            animationView.pauseAnimation();
                            if (isAnimationDown) {
                                if (animationView.getProgress() <= 0.5f) {
                                    animationView.setMinAndMaxProgress(0.4f, 1);
                                    animationView.playAnimation();
                                    animationView.loop(false);
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, 1500);
            }
        });

        MapsInitializer.initialize(this);
    }

    private void findAddress() {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Geocoder geocoder = new Geocoder(MapsActivityAddAddress.this, Locale.getDefault());
                double lat = mMap.getCameraPosition().target.latitude;
                double lon = mMap.getCameraPosition().target.longitude;
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(lat,
                            lon, 1);

                    if (addresses != null && addresses.size() > 0) {
                        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        cityName = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        country = addresses.get(0).getCountryName();
                        street = addresses.get(0).getThoroughfare();
                        postalCode = addresses.get(0).getPostalCode();
                        knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                        Log.d(TAG, "getAddress:  address " + address);
                        Log.d(TAG, "getAddress:  knownName " + knownName);
                        Log.d(TAG, "getAddress:  city " + cityName);
                        Log.d(TAG, "getAddress:  state " + state);
                        Log.d(TAG, "getAddress:  country " + country);
                        Log.d(TAG, "getAddress:  postalCode " + postalCode);
                        Log.d(TAG, "getAddress:  street " + street);
                        textViewAddress.setText(address);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
//                try {
//                    if (addresses != null) {
//                        Address returnedAddress = addresses.get(0);
//                        StringBuilder strReturnedAddress = new StringBuilder("");
//                        for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
//                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
//                        }
//                        Log.d("locationAddress", strReturnedAddress.toString());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
    }


    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationTask = fusedLocationProviderClient.getLastLocation();
        if (locationTask != null) {
            locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = (Location) task.getResult();
                        Toast.makeText(MapsActivityAddAddress.this, "zoom = "+
                                location,
                                Toast.LENGTH_SHORT).show();
                        if (location != null) {
                            if (location.getLatitude() != 0 && location.getLatitude() != 0) {
                                latLng1 = new LatLng(location.getLatitude(), location.getLongitude());

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15f));


//                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                                    @Override
//                                    public boolean onMarkerClick(Marker marker) {
//                                        // This causes the marker at Perth to bounce into position when it is clicked.
//                                        if (marker.equals(marker)) {
//                                            final Handler handler = new Handler();
//                                            final long start = SystemClock.uptimeMillis();
//                                            Projection proj = mMap.getProjection();
//                                            Point startPoint = proj.toScreenLocation(latLng);
//                                            startPoint.offset(0, -100);
//                                            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//                                            final long duration = 1500;
//                                            final Interpolator interpolator = new BounceInterpolator();
//                                            handler.post(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    long elapsed = SystemClock.uptimeMillis() - start;
//                                                    float t = interpolator.getInterpolation((float) elapsed / duration);
//                                                    double lng = t * latLng.longitude + (1 - t) * startLatLng.longitude;
//                                                    double lat = t * latLng.latitude + (1 - t) * startLatLng.latitude;
//                                                    marker.setPosition(new LatLng(lat, lng));
//                                                    if (t < 1.0) {
//                                                        // Post again 16ms later.
//                                                        handler.postDelayed(this, 16);
//                                                    }
//                                                }
//                                            });
//                                        }
//                                        // We return false to indicate that we have not consumed the event and that we wish
//                                        // for the default behavior to occur (which is for the camera to move such that the
//                                        // marker is centered and for the marker's info window to open, if it has one).
//                                        return false;
//                                    }
//                                });
                            }
                        }
                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(MapsActivityAddAddress.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);

        response = settingsClient.checkLocationSettings(request);
        response.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });
        response.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MapsActivityAddAddress.this, 1001);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates
                (locationRequest, callback, Looper.getMainLooper());

        zoomToUserLocation();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(callback);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "onMapLongClick: " + latLng.toString());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                );

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
                builder.include(marker.getPosition());
                builder.include(marker1.getPosition());

                LatLngBounds bounds = builder.build();

                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.25); // offset from edges of the map 10% of screen

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                mMap.animateCamera(cu);

//                List<Marker> markers = marker.getPoints();
//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                for (Marker marker : markers) {
//                    builder.include(marker.getPosition());
//                }
//                LatLngBounds bounds = builder.build();
//                int padding = 0; // offset from edges of the map in pixels
//                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                mMap.animateCamera(cu);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d(TAG, "onMarkerDragStart: ");
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("radius", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d(TAG, "onMarkerDrag: ");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d(TAG, "onMarkerDragEnd: ");
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                marker.setTitle(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettingsAndStartLocationUpdates();
//                enableUserLocation();
//                zoomToUserLocation();
            } else {
                //We can show a dialog that permission is not granted...
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
//                textViewAddress.setText(place.getAddress());

                longitude = String.valueOf(place.getLatLng().longitude);
                latitude = String.valueOf(place.getLatLng().latitude);
                mMap.clear();
                textViewAddress.setText(place.getAddress());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15f));
            }
        }
    }
    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            status("online");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            status("offline");
        }
    }
}