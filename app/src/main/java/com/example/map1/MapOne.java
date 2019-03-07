package com.example.map1;

import android.content.Intent;
import android.location.Location;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;


import com.example.map1.R;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

import java.util.List;



    public class MapOne extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {

        private MapView mapView;
        private MapboxMap map;
        private PermissionsManager permissionsManager;
        private LocationEngine locationEngine;
        private LocationLayerPlugin locationLayerPlugin;
        private Location originLocation;
        FloatingActionButton fab;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Mapbox.getInstance(this , "pk.eyJ1Ijoidmluejk2IiwiYSI6ImNqbHY2ZzI3ZTBxMGwzcXF4dW0wY2c0YTEifQ.I7WWi1_zydwqgtbEGERUQA");

            setContentView(R.layout.activity_map_one);
            mapView = findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);

            fab = findViewById(R.id.fabtn);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent hello = new Intent(MapOne.this, SMSOne.class);
                    MapOne.this.startActivity(hello);
                }
            });
        }

        @Override
        public void onMapReady(MapboxMap mapboxMap) {
            map = mapboxMap;
            enableLocation();
        }

        private void enableLocation(){
            if (PermissionsManager.areLocationPermissionsGranted(this)){
                initializeLocationEngine();
                initializeLocationLayer();
            }else{
                permissionsManager = new PermissionsManager(this);
                permissionsManager.requestLocationPermissions(this);
            }
        }

        @SuppressWarnings("MissingPermission")
        private void initializeLocationEngine(){
            locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
            locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
            locationEngine.activate();

            Location lastLocation = locationEngine.getLastLocation();
            if(lastLocation!= null){
                originLocation = lastLocation;
                setCameraPosition(lastLocation);
            }else{
                locationEngine.addLocationEngineListener(this);
            }
        }
        @SuppressWarnings("MissingPermission")
        private void initializeLocationLayer(){
            locationLayerPlugin= new LocationLayerPlugin(mapView, map, locationEngine);
            locationLayerPlugin.setLocationLayerEnabled(true);
            locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
            locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
        }

        private void setCameraPosition(Location location){
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                    location.getLongitude()),13.0));
        }

        @Override
        @SuppressWarnings("MissingPermission")
        public void onConnected() {
            locationEngine.requestLocationUpdates();
        }

        @Override
        public void onLocationChanged(Location location) {
            if(location != null){
                originLocation=location;
                setCameraPosition(location);
            }
        }

        @Override
        public void onExplanationNeeded(List<String> permissionsToExplain) {
            // present a dialogue
        }

        @Override
        public void onPermissionResult(boolean granted) {
            if(granted){
                enableLocation();
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        @SuppressWarnings("MissingPermission")
        @Override
        protected void onStart() {
            super.onStart();
            if (locationEngine != null){
                locationEngine.requestLocationUpdates();
            }
            if (locationLayerPlugin != null ){
                locationLayerPlugin.onStart();
            }
            mapView.onStart();
        }

        @Override
        protected void onResume() {
            super.onResume();
            mapView.onResume();
        }

        @Override
        protected void onPause() {
            super.onPause();
            mapView.onPause();
        }

        @Override
        protected void onStop() {
            super.onStop();
            if(locationEngine != null){
                locationEngine.removeLocationUpdates();
            }
            if (locationLayerPlugin!= null)
            {
                locationLayerPlugin.onStop();
            }
            mapView.onStop();
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            mapView.onSaveInstanceState(outState);
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mapView.onLowMemory();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if(locationEngine != null){
                locationEngine.deactivate();
            }
            mapView.onDestroy();
        }

    }
