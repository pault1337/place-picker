package com.thevenot.placepicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlacePicker {

    public static final class IntentBuilder {
        private boolean showLatLong;
        private double latitude;
        private double longitude;
        private float zoom;
        private List<Data> responseData;
        private boolean hideMarkerShadow;
        private int markerDrawableRes;
        private int markerImageColorRes;
        private int fabBackgroundColorRes;
        private int primaryTextColorRes;
        private int secondaryTextColorRes;
        private int mapRawResourceStyleRes;
        private MapType mapType;

        public IntentBuilder() {
            this.latitude = Constants.DEFAULT_LATITUDE;
            this.longitude = Constants.DEFAULT_LONGITUDE;
            this.zoom = Constants.DEFAULT_ZOOM;
            this.responseData = Arrays.asList(Data.values());
            this.markerDrawableRes = -1;
            this.markerImageColorRes = -1;
            this.fabBackgroundColorRes = -1;
            this.primaryTextColorRes = -1;
            this.secondaryTextColorRes = -1;
            this.mapRawResourceStyleRes = -1;
            this.mapType = MapType.NORMAL;
        }

        /**
         * Set whether the lat lon are displayed on the place picker or not.
         *
         * @param showLatLon true to show lat lon
         */
        public final PlacePicker.IntentBuilder showLatLon(boolean showLatLon) {
            this.showLatLong = showLatLon;
            return this;
        }

        /**
         * Set the init location where the map will load into
         *
         * @param latitude  startup latitude
         * @param longitude startup longitude
         */
        public final PlacePicker.IntentBuilder setInitLatLon(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            return this;
        }

        /**
         * Map Zoom Level. Default is {@link Constants#DEFAULT_ZOOM}
         *
         * @param zoom the map zoom level
         */
        public final PlacePicker.IntentBuilder setMapZoom(float zoom) {
            this.zoom = zoom;
            return this;
        }

        /**
         * Set the required response data. Default is all values of {@link Data} meaning that all types of values will be returned.
         *
         * @param responseData the required response data
         */
        public final PlacePicker.IntentBuilder setResponseData(List<Data> responseData) {
            this.responseData = responseData;
            return this;
        }

        /**
         * Allows to hide the marker shadow
         *
         * @param hideMarkerShadow true to hide the shadow
         */
        public final PlacePicker.IntentBuilder hideMarkerShadow(boolean hideMarkerShadow) {
            this.hideMarkerShadow = hideMarkerShadow;
            return this;
        }

        /**
         * Define the marker drawable used to render the marker on map.
         *
         * @param markerDrawableRes the resource id of the drawable
         */
        public final PlacePicker.IntentBuilder setMarkerDrawable(@DrawableRes int markerDrawableRes) {
            this.markerDrawableRes = markerDrawableRes;
            return this;
        }

        /**
         * Define the marker image color.
         *
         * @param markerImageColorRes the resource color
         */
        public final PlacePicker.IntentBuilder setMarkerImageImageColor(@ColorRes int markerImageColorRes) {
            this.markerImageColorRes = markerImageColorRes;
            return this;
        }

        public final PlacePicker.IntentBuilder setFabColor(@ColorRes int fabBackgroundColor) {
            this.fabBackgroundColorRes = fabBackgroundColor;
            return this;
        }

        public final PlacePicker.IntentBuilder setPrimaryTextColor(@ColorRes int primaryTextColor) {
            this.primaryTextColorRes = primaryTextColor;
            return this;
        }

        public final PlacePicker.IntentBuilder setSecondaryTextColor(@ColorRes int secondaryTextColorRes) {
            this.secondaryTextColorRes = secondaryTextColorRes;
            return this;
        }

        /**
         * The JSON styling string to customize Google Maps rendering.
         *
         * @param mapRawResourceStyleRes the json resource id.
         */
        public final PlacePicker.IntentBuilder setMapRawResourceStyle(@RawRes int mapRawResourceStyleRes) {
            this.mapRawResourceStyleRes = mapRawResourceStyleRes;
            return this;
        }

        public final PlacePicker.IntentBuilder setMapType(MapType mapType) {
            this.mapType = mapType;
            return this;
        }

        public final Intent build(Activity activity) {
            Intent intent = new Intent(activity, PlacePickerActivity.class);
            intent.putParcelableArrayListExtra(Constants.REQUIRED_RESPONSE_DATA, new ArrayList<>(responseData));
            intent.putExtra(Constants.SHOW_LAT_LONG_INTENT, showLatLong);
            intent.putExtra(Constants.INITIAL_LATITUDE_INTENT, latitude);
            intent.putExtra(Constants.INITIAL_LONGITUDE_INTENT, longitude);
            intent.putExtra(Constants.INITIAL_ZOOM_INTENT, zoom);
            intent.putExtra(Constants.HIDE_MARKER_SHADOW_INTENT, hideMarkerShadow);
            intent.putExtra(Constants.MARKER_DRAWABLE_RES_INTENT, markerDrawableRes);
            intent.putExtra(Constants.MARKER_COLOR_RES_INTENT, markerImageColorRes);
            intent.putExtra(Constants.FAB_COLOR_RES_INTENT, fabBackgroundColorRes);
            intent.putExtra(Constants.PRIMARY_TEXT_COLOR_RES_INTENT, primaryTextColorRes);
            intent.putExtra(Constants.SECONDARY_TEXT_COLOR_RES_INTENT, secondaryTextColorRes);
            intent.putExtra(Constants.MAP_RAW_STYLE_RES_INTENT, mapRawResourceStyleRes);
            intent.putExtra(Constants.MAP_TYPE_INTENT, mapType);
            return intent;
        }

    }

    public enum Data implements Parcelable {
        ADDRESS("address"), COORDINATES("coordinates");

        private String dataId;

        Data(String dataId) {
            this.dataId = dataId;
        }

        public void setDataId(String dataId) {
            this.dataId = dataId;
        }

        @NonNull
        public static final Creator<Data> CREATOR = new Creator<Data>(){

            @Override
            public Data createFromParcel(Parcel in) {
                Data data = Data.values()[in.readInt()];
                data.setDataId(in.readString());
                return data;
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(ordinal());
            dest.writeString(dataId);
        }
    }
}
