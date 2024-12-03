package x7030.nefzi.tjinitawpanel;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import x7030.nefzi.tjinitawpanel.Common.Common;
import x7030.nefzi.tjinitawpanel.Common.LatLngInterpolator;
import x7030.nefzi.tjinitawpanel.Common.MarkerAnimation;
import x7030.nefzi.tjinitawpanel.Model.OrderModel;
import x7030.nefzi.tjinitawpanel.databinding.ActivityShippingBinding;

public class ShippingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Marker shipperMarker;
    private Marker userMarker;
    private OrderModel shippingOrderModel;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();



    @BindView(R.id.txt_order_number)
    TextView txt_order_number;
    @BindView(R.id.txt_name)
    TextView txt_name;
    @BindView(R.id.txt_address)
    TextView txt_address;
    @BindView(R.id.txt_date)
    TextView txt_date;
    @BindView(R.id.txt_zyeda)
    TextView txt_zyeda;
    @BindView(R.id.btn_call)
    MaterialButton btn_call;
    @BindView(R.id.btn_done)
    MaterialButton btn_done;
    @BindView(R.id.show)
    MaterialButton show;
    @BindView(R.id.detail_ship)
    CardView detail_ship;


    @BindView(R.id.img_food_image)
    ImageView img_food_image;

    @OnClick(R.id.btn_call)
    void onCallClick(){
        if (shippingOrderModel != null)
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                Dexter.withActivity(this)
                        .withPermission(Manifest.permission.CALL_PHONE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {


                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(ShippingActivity.this, "You must accept this permission to Call user", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        }).check();

                return;
            }
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(new StringBuilder("tel:")
                    .append(shippingOrderModel
                            .getUserPhone()).toString()));
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_done)
    void onDoneClick(){
        if (shippingOrderModel != null)
        {
            if (Common.currentShippingOrder.getOrderStatus() == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Kamelt")
                        .setMessage("Mthabet el commande kemlet ?")
                        .setNegativeButton("BATELT", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setPositiveButton("EY", (dialogInterface, i) -> {

                            Map<String, Object> update_data = new HashMap<>();
                            update_data.put("orderStatus", 2);
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child(Common.TASKS_REF)
                                    .child(Common.currentShippingOrder.getOrderNumber())
                                    .updateChildren(update_data)
                                    .addOnFailureListener(e -> Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                    .addOnSuccessListener(aVoid -> {
                                        updateShipperFees(Common.currentShippingOrder.getFees());
                                        Intent intent = new Intent(this, HomeActivity.class);
                                        startActivity(intent);
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                        compositeDisposable.clear();
                                        Toast.makeText(this, "Saye ya3tik e sa7a", Toast.LENGTH_SHORT).show();


                                    });


                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else
            {
                Toast.makeText(this, "hezha 3andek e se3a el commande", Toast.LENGTH_SHORT).show();
            }


        }







    }

    private void updateShipperFees(double fees) {
        Map<String, Object> update_data = new HashMap<>();

        FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.USER_REFERENCESER)
                .child(Common.currentShipper.getPhone())
                .child("fees")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        double oldFees = snapshot.getValue(double.class);
                        update_data.put("fees", oldFees+fees);
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child(Common.USER_REFERENCESER)
                                .child(Common.currentShipper.getPhone())
                                .updateChildren(update_data)
                                .addOnFailureListener(e -> Toast.makeText(ShippingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                .addOnSuccessListener(aVoid -> {
                                    //loading_dialog.dismiss();
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });




};


    @OnClick(R.id.show)
    void showDeatil(){
        if (detail_ship.getVisibility() == View.VISIBLE)
        {
            detail_ship.setVisibility(View.GONE);
            show.setText("WARINI");

        }
        else
        {
            detail_ship.setVisibility(View.VISIBLE);
            show.setText("KHABI 3LIYA");
        }

    }




    private  boolean isTnit = false;
    private Location previousLocation=null;
    private Polyline redPolyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);


        ButterKnife.bind(this);
        buildLocationRequest();
        buildLocationCallback();



        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(ShippingActivity.this::onMapReady);

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ShippingActivity.this);
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());



                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ShippingActivity.this, "Lezemna el permission mel parametre 3ala Gps", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();



    }

    private void setShippingOrder() {

        if (Common.currentShippingOrder != null) {
            shippingOrderModel = Common.currentShippingOrder;
            if (shippingOrderModel != null)

            {
                Common.setSpanStringColor("Client: ",Common.currentShippingOrder.getUserName(),txt_name, Color.parseColor("#0E38CF"));
                txt_date.setText(new StringBuilder().append(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(shippingOrderModel.getCreateDate())));

                Common.setSpanStringColor("No: ",Common.currentShippingOrder.getOrderNumber(),txt_order_number, Color.parseColor("#0886EA"));
                Common.setSpanStringColor("Addristou: ",shippingOrderModel.getShippingAddress(),txt_zyeda, Color.parseColor("#747474"));
                try
                {
                    Glide.with(this)
                            .load(Common.currentShippingOrder.getCartItemList().get(0).getFoodIamge()).into(img_food_image);
                    Common.setSpanStringColor("Total: ",Common.formatPrice(Common.currentShippingOrder.getFinalPayment())+" TND",txt_address, Color.parseColor("#ff4444"));

                }
                catch (Exception exception)
                {
                    img_food_image.setVisibility(View.GONE);
                    Common.setSpanStringColor("Tache: ",Common.currentShippingOrder.getService(),txt_address, Color.parseColor("#000000"));
                }


            }


        }
        else
        {
            Toast.makeText(this, "Shipping Order is null!", Toast.LENGTH_SHORT).show();

        }
    }





    private void buildLocationCallback() {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LatLng locationShipper = new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());
                LatLng locationUser = new LatLng(Common.currentShippingOrder.getLat(),
                        Common.currentShippingOrder.getLng());

                if (shipperMarker == null)
                {
                    int height,width;
                    height = 80;
                    width = 80;
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat
                            .getDrawable(ShippingActivity.this,R.drawable.shipper);
                    Bitmap resize = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(),width,height,false);


                    shipperMarker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(resize))
                            .position(locationShipper).title("Enty"));
                    BitmapDrawable bitmapDrawable2 = (BitmapDrawable) ContextCompat
                            .getDrawable(ShippingActivity.this,R.drawable.box);
                    Bitmap resize2 = Bitmap.createScaledBitmap(bitmapDrawable2.getBitmap(),width,height,false);

                    userMarker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(resize2))
                            .position(locationUser).title(Common.currentShippingOrder.getUserName()));


                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper, 18));


                }

                if (isTnit && previousLocation != null)
                {
                    LatLng previousLocationLatLng = new LatLng(previousLocation.getLatitude(),
                            previousLocation.getLongitude());
                    MarkerAnimation.animationMarkerToGB(shipperMarker,locationShipper,new LatLngInterpolator.Spherical());
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(locationShipper));




                }
                if (!isTnit)
                {
                    isTnit = true;
                    previousLocation = locationResult.getLastLocation();
                }



            }
        };
    }


    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        mMap = googleMap;

        setShippingOrder();

        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    @Override
    protected void onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        compositeDisposable.clear();
        Common.currentShippingOrder = null;
        super.onDestroy();
    }
}