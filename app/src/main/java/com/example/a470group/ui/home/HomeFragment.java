package com.example.a470group.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.a470group.R;
import com.example.a470group.Stop;
import com.example.a470group.ui.dialog.CustomAboutDialogFragment;
import com.example.a470group.ui.dialog.CustomFutureDialogFragment;
import com.example.a470group.ui.dialog.CustomHelpDialogFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback{
  protected static final String FRAGMENT_NAME = "HomeFragment";
  private static final int DEFAULT_ZOOM = 15;
  private static final int WIDE_ZOOM = 14;
  private GoogleMap mMap;

  private boolean PERMISSION_GRANTED = false;
  
  private final LatLng waterloo = new LatLng(43.47287253812701, -80.53814926494488);

  private final ActivityResultLauncher<String> requestPermissionLauncher =
          registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
              PERMISSION_GRANTED = true;
              checkLocationPermission();
            } else {
              Toast.makeText(this.requireActivity().getApplicationContext(), "You may use a custom location.", Toast.LENGTH_LONG).show();
              PERMISSION_GRANTED = false;
            }
          });

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Log.d(FRAGMENT_NAME, "onCreateView Created");

    // Initialize view
    View view=inflater.inflate(R.layout.fragment_home, container, false);

    // Initialize map fragment
    SupportMapFragment supportMapFragment=(SupportMapFragment)
        getChildFragmentManager().findFragmentById(R.id.google_map);

    assert supportMapFragment != null;
    supportMapFragment.getMapAsync(this);

    if (mMap != null) {
      mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    return view;
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    menu.add(getString(R.string.home_menu_option1));
    menu.add(getString(R.string.home_menu_option3));
    menu.add(getString(R.string.home_menu_option2));
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    CharSequence title = item.getTitle();
    if (title.equals(getString(R.string.home_menu_option1))) {
      helpDialog();
    } else if (title.equals(getString(R.string.home_menu_option2))) {
      aboutDialog();
    }else if (title.equals(getString(R.string.home_menu_option3))) {
      futureDialog();
    }
    return true;
  }

  private void helpDialog() {
    new CustomHelpDialogFragment().show(
            getChildFragmentManager(), CustomHelpDialogFragment.TAG);
  }

  private void aboutDialog() {
    new CustomAboutDialogFragment().show(
            getChildFragmentManager(), CustomAboutDialogFragment.TAG);
  }

  private void futureDialog() {
    new CustomFutureDialogFragment().show(
            getChildFragmentManager(), CustomFutureDialogFragment.TAG
    );
  }

  @Override
  @SuppressLint("MissingPermission")
  public void onMapReady(@NonNull GoogleMap googleMap) {
    Log.d(FRAGMENT_NAME, "onMapReady Created");
    mMap = googleMap;
    mMap.setOnMyLocationButtonClickListener(this);
    mMap.setOnMyLocationClickListener(this);

    checkLocationPermission();

    // When map is loaded
    Stop[] stops = new Stop[4];
    stops[0] = new Stop(43.491552,-80.537559,"Albert / Weber");
    stops[1] = new Stop(43.490533,-80.539508,"Albert / Longwood");
    stops[2] = new Stop(43.488146,-80.541394,"Albert / Greenbrier");
    stops[3] = new Stop(43.487633,-80.541571,"Albert / Quiet");

    // add markers
    addStopMarker(stops[0]);
    addStopMarker(stops[1]);
    addStopMarker(stops[2]);
    addStopMarker(stops[3]);

    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(waterloo, WIDE_ZOOM));
  }

  /**
   * Given a stop, will take its data and put a marker onto the map
   * @param stop - A Stop object to convert into a marker on map
   */
  private void addStopMarker(Stop stop) {
    if (stop == null) return;

    mMap.addMarker(new MarkerOptions().position(stop.getLatLng()).title(stop.getName()));
  }

  @SuppressLint("MissingPermission")
  private void checkLocationPermission() {
    if(ContextCompat.checkSelfPermission(this.requireActivity().getApplicationContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this.requireActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      Log.d(FRAGMENT_NAME, "Permission Granted");
      mMap.setMyLocationEnabled(true);

      Location myLocation = mMap.getMyLocation();
      return;
    }

      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
      Log.d(FRAGMENT_NAME, "hmm");
  }

  @Override
  public boolean onMyLocationButtonClick() {
    Toast.makeText(this.requireActivity().getApplicationContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
    return false;
  }

  @Override
  public void onMyLocationClick(@NonNull Location location) {
    Toast.makeText(this.requireActivity().getApplicationContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
  }
}
