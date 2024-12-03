package x7030.nefzi.tjinitaw;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;

import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.databinding.ActivityFindThingsBinding;

public class FindThingsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityFindThingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFindThingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        View loading_layout = LayoutInflater.from(this)
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng pharmacie = new LatLng(Common.currentPharmacie.getLat(), Common.currentPharmacie.getLng());
        mMap.addMarker(new MarkerOptions().position(pharmacie).title(Common.currentPharmacie.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pharmacie));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pharmacie,18));
        loading_dialog.dismiss();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().removeAllStickyEvents();
        super.onStop();
    }
}