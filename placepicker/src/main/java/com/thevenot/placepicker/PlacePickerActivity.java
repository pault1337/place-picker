package com.thevenot.placepicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.thevenot.placepicker.Constants.ADDRESS_INTENT;

public class PlacePickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final int DEFAULT_INTENT_INT_VALUE = -1;
    private final String TAG = "PlacePickerActivity";

    private GoogleMap map;
    private ImageView markerImage;
    private ImageView markerShadowImage;
    private FloatingActionButton placeSelectedFab;
    private FloatingActionButton myLocationFab;
    private TextView placeNameTextView;
    private TextView placeAddressTextView;
    private TextView placeCoordinatesTextView;
    private ProgressBar placeProgressBar;

    private double latitude = Constants.DEFAULT_LATITUDE;
    private double longitude = Constants.DEFAULT_LONGITUDE;
    private boolean showLatLong = true;
    private float zoom = Constants.DEFAULT_ZOOM;
    private List<PlacePicker.Data> responseData;
    private boolean hideMarkerShadow;
    private int markerDrawableRes = DEFAULT_INTENT_INT_VALUE;
    private int markerColorRes = DEFAULT_INTENT_INT_VALUE;
    private int fabColorRes = DEFAULT_INTENT_INT_VALUE;
    private int primaryTextColorRes = DEFAULT_INTENT_INT_VALUE;
    private int secondaryTextColorRes = DEFAULT_INTENT_INT_VALUE;
    private int mapRawResourceStyleRes = DEFAULT_INTENT_INT_VALUE;
    private List<Address> addresses;
    private MapType mapType;

    private Location mLastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSION_ACCESS_LOCATION = 1;
    private static final String KEY_LOCATION = "location";

    public PlacePickerActivity() {
        this.mapType = MapType.NORMAL;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_place_picker);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        this.getIntentData();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        this.bindViews();
        placeCoordinatesTextView.setVisibility(this.showLatLong ? View.VISIBLE : View.GONE);

        placeSelectedFab.setOnClickListener(view -> {
            boolean send = true;
            Intent returnIntent = new Intent();
            AddressData addressData = new AddressData();
            if (responseData.contains(PlacePicker.Data.ADDRESS)) {
                if (addresses != null) {
                    addressData.setAddressList(addresses);
                } else {
                    send = false;
                    Toast.makeText(PlacePickerActivity.this, getString(R.string.no_address), Toast.LENGTH_LONG).show();
                }
            }
            if (responseData.contains(PlacePicker.Data.COORDINATES)) {
                addressData.setLatitude(this.latitude);
                addressData.setLongitude(this.longitude);
            }
            returnIntent.putExtra(ADDRESS_INTENT, addressData);
            if (send) {
                setResult(RESULT_OK, returnIntent);
                this.finish();
            }
        });

        myLocationFab.setOnClickListener(view -> moveToMyLocation());

        setIntentCustomization();

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (map != null) {
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
        }
    }

    private void bindViews() {
        markerImage = findViewById(R.id.marker_image_view);
        markerShadowImage = findViewById(R.id.marker_shadow_image_view);
        placeSelectedFab = findViewById(R.id.place_chosen_button);
        myLocationFab = findViewById(R.id.my_location_button);
        placeNameTextView = findViewById(R.id.text_view_place_name);
        placeAddressTextView = findViewById(R.id.text_view_place_address);
        placeCoordinatesTextView = findViewById(R.id.text_view_place_coordinates);
        placeProgressBar = findViewById(R.id.progress_bar_place);
    }

    private void getIntentData() {
        this.latitude = this.getIntent().getDoubleExtra(Constants.INITIAL_LATITUDE_INTENT, Constants.DEFAULT_LATITUDE);
        this.longitude = this.getIntent().getDoubleExtra(Constants.INITIAL_LONGITUDE_INTENT, Constants.DEFAULT_LONGITUDE);
        this.showLatLong = this.getIntent().getBooleanExtra(Constants.SHOW_LAT_LONG_INTENT, false);
        this.responseData = this.getIntent().getParcelableArrayListExtra(Constants.REQUIRED_RESPONSE_DATA);
        this.hideMarkerShadow = this.getIntent().getBooleanExtra(Constants.HIDE_MARKER_SHADOW_INTENT, false);
        this.zoom = this.getIntent().getFloatExtra(Constants.INITIAL_ZOOM_INTENT, Constants.DEFAULT_ZOOM);
        this.markerDrawableRes = this.getIntent().getIntExtra(Constants.MARKER_DRAWABLE_RES_INTENT, DEFAULT_INTENT_INT_VALUE);
        this.markerColorRes = this.getIntent().getIntExtra(Constants.MARKER_COLOR_RES_INTENT, DEFAULT_INTENT_INT_VALUE);
        this.fabColorRes = this.getIntent().getIntExtra(Constants.FAB_COLOR_RES_INTENT, DEFAULT_INTENT_INT_VALUE);
        this.primaryTextColorRes = this.getIntent().getIntExtra(Constants.PRIMARY_TEXT_COLOR_RES_INTENT, DEFAULT_INTENT_INT_VALUE);
        this.secondaryTextColorRes = this.getIntent().getIntExtra(Constants.SECONDARY_TEXT_COLOR_RES_INTENT, DEFAULT_INTENT_INT_VALUE);
        this.mapRawResourceStyleRes = this.getIntent().getIntExtra(Constants.MAP_RAW_STYLE_RES_INTENT, DEFAULT_INTENT_INT_VALUE);
        this.mapType = (MapType) this.getIntent().getSerializableExtra(Constants.MAP_TYPE_INTENT);
    }

    private void setIntentCustomization() {
        markerShadowImage.setVisibility(hideMarkerShadow ? View.GONE : View.VISIBLE);
        if (this.markerColorRes != DEFAULT_INTENT_INT_VALUE) {
            markerImage.setColorFilter(ContextCompat.getColor(this, this.markerColorRes));
        }
        if (this.markerDrawableRes != DEFAULT_INTENT_INT_VALUE) {
            markerImage.setImageDrawable(ContextCompat.getDrawable(this, this.markerDrawableRes));
        }

        if (this.fabColorRes != DEFAULT_INTENT_INT_VALUE) {
            placeSelectedFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, this.fabColorRes)));
            myLocationFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, this.fabColorRes)));
        }

        if (this.primaryTextColorRes != DEFAULT_INTENT_INT_VALUE) {
            placeNameTextView.setTextColor(ContextCompat.getColor(this, this.primaryTextColorRes));
        }

        if (this.secondaryTextColorRes != DEFAULT_INTENT_INT_VALUE) {
            placeAddressTextView.setTextColor(ContextCompat.getColor(this, this.secondaryTextColorRes));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setOnCameraMoveStartedListener(i -> {
            if (markerImage.getTranslationY() == 0f) {
                markerImage.animate()
                        .translationY(-75f)
                        .setInterpolator(new OvershootInterpolator())
                        .setDuration(250)
                        .start();
            }
        });

        map.setOnCameraIdleListener(() -> {
            markerImage.animate()
                    .translationY(0f)
                    .setInterpolator(new OvershootInterpolator())
                    .setDuration(250)
                    .start();

            showLoadingBottomDetails();
            LatLng latLng = map.getCameraPosition().target;
            latitude = latLng.latitude;
            longitude = latLng.longitude;
            AsyncTask.execute(() -> {
                final Address currentAddress = getAddressForLocation();
                runOnUiThread(() -> setPlaceDetails(latitude, longitude, currentAddress));
            });
        });

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        if (mapRawResourceStyleRes != DEFAULT_INTENT_INT_VALUE) {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapRawResourceStyleRes));
        }

        switch (mapType) {
            case SATELLITE:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case HYBRID:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case TERRAIN:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case NONE:
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case NORMAL:
            default:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
    }

    private void showLoadingBottomDetails() {
        placeNameTextView.setText("");
        placeAddressTextView.setText("");
        placeCoordinatesTextView.setText("");
        placeProgressBar.setVisibility(View.VISIBLE);
    }

    private void setPlaceDetails(double latitude, double longitude, Address currentAddress) {
        if (latitude != -1.0D && longitude != -1.0D) {
            placeProgressBar.setVisibility(View.INVISIBLE);
            placeNameTextView.setText(formatShortAddress(currentAddress));
            placeAddressTextView.setText(formatPostalCodeAndCity(currentAddress));
            placeCoordinatesTextView.setText(getString(R.string.lat_lon, Location.convert(latitude, Location.FORMAT_DEGREES), Location.convert(longitude, Location.FORMAT_DEGREES)));
        } else {
            placeNameTextView.setText("");
            placeAddressTextView.setText("");
            placeProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private Address getAddressForLocation() {
        return this.setAddress(this.latitude, this.longitude);
    }

    private Address setAddress(double latitude, double longitude) {
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        Address currentAddress = null;
        try {
            this.addresses = geoCoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() != 0) {
                currentAddress = addresses.get(0);
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            }

        } catch (Exception e) {
            Log.e(TAG, "An error occurred while getting the address", e);
        }
        return currentAddress;
    }

    private String formatPostalCodeAndCity(Address currentAddress) {
        StringBuilder postalCodeAndCity = new StringBuilder();
        if (currentAddress != null) {
            if (currentAddress.getPostalCode() != null) {
                postalCodeAndCity.append(currentAddress.getPostalCode()).append(" ");
            }
            if (currentAddress.getLocality() != null) {
                postalCodeAndCity.append(currentAddress.getLocality());
            }
        }

        return postalCodeAndCity.toString();
    }

    private String formatShortAddress(Address currentAddress) {
        if (currentAddress != null) {
            String addressLine = currentAddress.getAddressLine(0);
            List<String> addressParts = Arrays.asList(addressLine.split(","));
            if (addressParts.size() > 1) {
                return addressParts.get(0);
            }
        }
        return getString(R.string.dropped_pin);
    }

    /**
     * On click on the my location button which is only shown if the permissions have been accepted.
     */
    @SuppressLint("MissingPermission")
    private void moveToMyLocation() {
        getDeviceLocation();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_ACCESS_LOCATION);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void getDeviceLocation() {
        checkPermissions();
        if (mLocationPermissionGranted) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                mLastKnownLocation = location;
                moveCameraCurrentLocationOrDefault();
            });
        } else {
            moveCameraCurrentLocationOrDefault();
        }
    }

    private void moveCameraCurrentLocationOrDefault() {
        // Set the map's camera position to the current location of the device.
        Log.d(TAG, "Last know pos " + mLastKnownLocation);
        if (mLastKnownLocation != null) {
            CameraPosition cameraPosition = new CameraPosition(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), zoom, 0, 0);
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
        } else {
            Log.d(TAG, "Current location is null. Using defaults, lat " + this.latitude + ", lon " + this.longitude);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.latitude, this.longitude), zoom));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "Perm result " + Arrays.toString(grantResults));
        if (requestCode == PERMISSION_ACCESS_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location granted");
                mLocationPermissionGranted = true;
                getDeviceLocation();
            }
        }
    }
}
