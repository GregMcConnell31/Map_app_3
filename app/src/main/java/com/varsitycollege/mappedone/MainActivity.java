package com.varsitycollege.mappedone;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {
    //variables
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 7171;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private Button btnStart;
    private Button landmarkBtn;
    private String geoJsonSourceLayerId="GeoJsonSourceLayerId";
    private String symbolIconId="SymbolIconId";
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.Theme_Dark);
        } else {
            setTheme(R.style.Theme_Light);
        }
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView= findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        btnSettings=findViewById(R.id.settingsButton);
        //btnStart.setBackgroundColor(getResources().getColor(R.color.mapboxRed));


    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap=mapboxMap;
        mapboxMap.setStyle(getString(com.mapbox.services.android.navigation.ui.v5.R.string.navigation_guidance_day),style ->{
                    enableLocationComponent(style);
                    addDestinationIconSymbolLayer(style);
                    mapboxMap.addOnMapClickListener(this);

                    btnStart=findViewById(R.id.startButton);

                    btnStart.setOnClickListener(v->{
                        try {
                            boolean simulateRoute = true;
                            NavigationLauncherOptions options = NavigationLauncherOptions.builder().directionsRoute(currentRoute).shouldSimulateRoute(simulateRoute).build();
                            NavigationLauncher.startNavigation(this, options);
                        }
                        catch (Exception e){
                            Toast.makeText(this, "A destination must be selected to begin navigation", Toast.LENGTH_SHORT).show();
                        }

                    });
                    btnSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(MainActivity.this, SetttingsActivity.class);
                            startActivity(i);
                        }
                    });


                    initSearchBtn();
                    setUpSource(style);
                    setUpLayer(style);
                    Drawable drawable= ResourcesCompat.getDrawable(getResources(),R.drawable.ic_baseline_location_on_red_24,null);
                    Bitmap bitmap= BitmapUtils.getBitmapFromDrawable(drawable);
                    style.addImage(symbolIconId,bitmap);
                }

        );
    }

    private void setUpLayer(Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID",geoJsonSourceLayerId).withProperties(iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})));
    }

    private void setUpSource(Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geoJsonSourceLayerId));
    }

    private void initSearchBtn() {
        landmarkBtn=findViewById(R.id.landmarkBtn);
        landmarkBtn.setOnClickListener(view -> {
            Intent i = new PlaceAutocomplete.IntentBuilder().accessToken(Mapbox.getAccessToken()!=null ? Mapbox.getAccessToken():getString(R.string.access_token))
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(10)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(this);
            startActivityForResult(i, REQUEST_CODE_AUTOCOMPLETE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK && requestCode== REQUEST_CODE_AUTOCOMPLETE){
            CarmenFeature selectedCarmen=PlaceAutocomplete.getPlace(data);
            if (mapboxMap != null) {
                Style style=mapboxMap.getStyle();
                if(style!=null){
                    GeoJsonSource source=style.getSourceAs(geoJsonSourceLayerId);
                    if(source!=null){
                        source.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{Feature.fromJson(selectedCarmen.toJson())}));
                    }
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(((Point)selectedCarmen.geometry()).latitude(),
                                    ((Point)selectedCarmen.geometry()).longitude())).zoom(14)
                            .build()),4000);
                }
            }
        }
    }

    private void addDestinationIconSymbolLayer(Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(this.getResources(), com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource= new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer= new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true));
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Point destinationPoint= Point.fromLngLat(point.getLongitude(),point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());
        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if(source!=null){
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

        getRoute(originPoint, destinationPoint);
        btnStart.setEnabled(true);
        //btnStart.setBackgroundResource(R.color.mapboxBlue);

        return true;
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this).accessToken(Mapbox.getAccessToken()).origin(origin).destination(destination).build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                Log.d(TAG, "Response code: "+response.code());
                if(response.body()==null){
                    Log.d(TAG, "No routes found");
                    return;
                }
                else if (response.body().routes().size()>1){
                    Log.d(TAG, "No routes found");
                    return;
                }
                currentRoute=response.body().routes().get(0);
                if(navigationMapRoute!=null){
                    navigationMapRoute.removeRoute();
                }
                else{
                    navigationMapRoute=new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                }
                navigationMapRoute.addRoute(currentRoute);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e(TAG, "error"+t.getMessage());
            }
        });

    }

    private void enableLocationComponent(Style loadedMapStyle) {
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            locationComponent=mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            //camera
            locationComponent.setCameraMode(CameraMode.TRACKING);
        }
        else{
            permissionsManager= new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocationComponent(mapboxMap.getStyle());
        }
        else{
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle outState) {
        super.onPostCreate(outState);
        mapView.onSaveInstanceState(outState);
    }



}