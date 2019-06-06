package com.thevenot.placepickerexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.thevenot.placepicker.AddressData;
import com.thevenot.placepicker.Constants;
import com.thevenot.placepicker.MapType;
import com.thevenot.placepicker.PlacePicker;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.open_place_picker_button).setOnClickListener(view -> {
            Intent intent = new PlacePicker.IntentBuilder()
                    .setInitLatLon(37.4133865,-122.1162864)
                    .showLatLon(true)
                    .setMapRawResourceStyle(R.raw.map_style)
                    .setMapType(MapType.NORMAL)
                    .setResponseData(Arrays.asList(PlacePicker.Data.ADDRESS, PlacePicker.Data.COORDINATES))
                    .setMarkerDrawable(R.drawable.ic_custom_map_marker)
                    .build(MainActivity.this);

            startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                try {
                    AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                    ((TextView) findViewById(R.id.address_data_text_view)).setText(addressData.getAddressString() + "\nlat : " + addressData.getLatitude() + ", lon : " + addressData.getLongitude());
                } catch (Exception e) {
                    Log.e("MainActivity", "An error occured while getting results from place picker",e);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
