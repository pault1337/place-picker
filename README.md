 [ ![Download](https://api.bintray.com/packages/pault1337/android/com.thevenot.placepicker/images/download.svg) ](https://bintray.com/pault1337/android/com.thevenot.placepicker/_latestVersion)

# PlacePicker
Place Picker for Google Maps has been deprecated in Android and we are told to move to paid APIs. Autocomplete, Nearby and Places photos APIs are chargeable after a number of loads. [Check Pricing here](https://cloud.google.com/maps-platform/pricing/sheet/)

<p align="center"><img src="https://github.com/pault1337/place-picker/blob/master/screens/place_picker_deprecated.png"></p>

Thankfully, Static and Dynamic Maps on Mobile and Geocoder is still free. PlacePicker is a Place Picker alternative library that allows you to pick a point in the map and get its coordinates and Address using Geocoder instead of Google APIs

<p align="center"><img width="300px" src="https://github.com/pault1337/place-picker/blob/master/screens/demo.gif"></p>

## Adding PlacePicker to your project

Include the following dependencies in your app's build.gradle :

```
dependencies {
  implementation 'com.google.android.gms:play-services-maps:16.1.0'
  implementation 'com.thevenot:placepicker:1.0.0'
  
  implementation 'com.google.android.material:material:1.1.0'
}
```
PlacePicker Uses **AndroidX** artifacts, thus to use it without issues, make sure your application has been migrated to AndroidX as well. If you havent done it already, [Here's How](https://developer.android.com/jetpack/androidx/migrate)

## How to use

1. You need a Maps API key and add it to your app. [Here's How](https://developers.google.com/maps/documentation/android-sdk/signup)
2. To start The `PlacePickerActivity`:

``` java
val intent = PlacePicker.IntentBuilder()
                .setLatLong(40.748672, -73.985628)  // The init location where the map will load into
                .showLatLong(true)  // whether the lat lon are displayed on the place picker or not
                .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
                .setResponseData(Arrays.asList(PlacePicker.Data.ADDRESS, PlacePicker.Data.COORDINATES)) // The required response data
                .setMyLocationButtonPosition(PlacePicker.Position.LEFT) // to set the my location button to the left (default is Position.RIGHT)
                .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
                .setMarkerDrawable(R.drawable.marker) // Overrides the default Marker Image
                .setMarkerImageImageColor(R.color.colorPrimary)
                .setFabColor(R.color.fabColor)
                .setPrimaryTextColor(R.color.primaryTextColor) // Change text color of Shortened Address
                .setSecondaryTextColor(R.color.secondaryTextColor) // Change text color of full Address
                .setMapRawResourceStyle(R.raw.map_style)  //The JSON styling string to customize Google Maps rendering (https://mapstyle.withgoogle.com/)
                .setMapType(MapType.NORMAL)
                .build(this)
            startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
```
3. Then get the data `onActivityResult`

```java
Intent intent = new PlacePicker.IntentBuilder()
                ...
                
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
                
                ...

@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
              AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
```

4. Customization

You can easily override some styles of the PlacePicker. Starting with :
```xml
<resources>
    <color name="colorPrimary"></color>
    <color name="colorPrimaryDark"></color>
    <color name="colorAccent"></color>
</resources>
```



## Changelog

### [1.0.1]
- Add option to set the my location button position 

### [1.0.0]
- First version


**Note:** This is inspired from [PlacePicker](https://github.com/suchoX/PlacePicker) which was inspired by Mapbox [Android Place Picker plugin](https://docs.mapbox.com/android/plugins/examples/place-picker/). Code and UI has been reused from the open source library hosted on [Github](https://github.com/mapbox/mapbox-plugins-android). Their copyright license has been added [here](https://github.com/pault1337/place-picker/blob/master/LICENSE)
