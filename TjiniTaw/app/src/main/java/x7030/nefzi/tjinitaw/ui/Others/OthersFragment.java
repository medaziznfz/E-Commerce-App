package x7030.nefzi.tjinitaw.ui.Others;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import x7030.nefzi.tjinitaw.Callback.ILoadTimeFromFirebaseListener;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.EventBus.CounterCartEvent;
import x7030.nefzi.tjinitaw.EventBus.PlaceOrderClicked;
import x7030.nefzi.tjinitaw.Model.Order;
import x7030.nefzi.tjinitaw.R;

public class OthersFragment extends Fragment implements ILoadTimeFromFirebaseListener{

    private OthersViewModel slideshowViewModel;
    Unbinder unbinder;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    ILoadTimeFromFirebaseListener listener;
    String notif = "" ;
    private Double LatReciver = -1.0;
    private Double LngReciver = -1.0;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(OthersViewModel.class);

        View root = inflater.inflate(R.layout.fragment_others, container, false);
        listener = this;
        unbinder = ButterKnife.bind(this,root);
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builderLoad = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builderLoad.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);


        EditText edt_address = (EditText) root.findViewById(R.id.edt_address);
        EditText edt_task = (EditText) root.findViewById(R.id.edt_task);
        RadioButton rdi_home = (RadioButton) root.findViewById(R.id.rdi_home_address);
        RadioButton rdi_other_address = (RadioButton) root.findViewById(R.id.rdi_other_address);
        RadioButton rdi_ship_to_this = (RadioButton) root.findViewById(R.id.rdi_ship_this_address);
        Button btnupload = (Button) root.findViewById(R.id.btnupload);
        edt_address.setText(Common.currentUser.getAddress());
        rdi_home.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                edt_address.setText(Common.currentUser.getAddress());
                edt_address.setEnabled(true);
                LatReciver = -1.0 ;
                LngReciver = -1.0 ;
            }

        });
        rdi_other_address.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                edt_address.setText("");
                edt_address.setEnabled(true);
                LatReciver = -1.0 ;
                LngReciver = -1.0 ;
            }

        });
        rdi_ship_to_this.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                loading_dialog.show();
                fusedLocationProviderClient.getLastLocation()
                        .addOnFailureListener(e -> {
                            rdi_home.setChecked(true);

                        }).addOnCompleteListener(task -> {
                    try {
                        String coordinates = new StringBuilder().append(task.getResult().getLatitude()).append("/")
                                .append(task.getResult().getLongitude()).toString();
                        LatReciver = task.getResult().getLatitude();
                        LngReciver = task.getResult().getLongitude();

                        Single<String> singleAddress = Single.just(getAddressFromLatLng(task.getResult().getLatitude(),task.getResult().getLongitude()));
                        Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>(){
                            @Override
                            public void onSuccess(@NotNull String s) {

                                edt_address.setText(coordinates);
                                edt_address.setEnabled(false);
                                loading_dialog.dismiss();

                            }

                            @Override
                            public void onError(@NotNull Throwable e) {
                                edt_address.setText(coordinates);
                                loading_dialog.dismiss();

                            }
                        });


                    }
                    catch (Exception e)
                    {
                        loading_dialog.dismiss();
                        if (e.getMessage().contains("ACCESS_FINE_LOCATION"))
                            Toast.makeText(getContext(), "Lezemna el permission mte3 e loclisation mel parametre", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), "7el e localisation mte3ek!", Toast.LENGTH_SHORT).show();
                        rdi_home.setChecked(true);



                    }


                });
            }

        });

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_task.getText().toString().isEmpty())
                {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                    builder.setTitle("Service")
                            .setMessage("Mthabet theb t3adih el commande ?")
                            .setNegativeButton("BATELT", (dialogInterface, i) -> dialogInterface.dismiss())
                            .setPositiveButton("EY", (dialogInterface, i) -> {

                                Order order = new Order();

                                notif += ">>>>>>>>>>Service<<<<<<<<<<%0A";
                                order.setUserId(Common.currentUser.getPhone());
                                order.setUserName(Common.currentUser.getName());
                                notif += "User : "+order.getUserName()+"%0A";
                                notif += "Phone : "+order.getUserPhone()+"%0A";
                                order.setUserPhone(Common.currentUser.getPhone());
                                order.setService(edt_task.getText().toString());
                                order.setShippingAddress(edt_address.getText().toString());
                                notif += "Address : "+order.getShippingAddress()+"%0A";
                                notif += "Tache : "+order.getService()+"%0A";
                                order.setFees(0.0);

                                if (LatReciver !=- 1.0 && LngReciver !=-1.0)
                                {
                                    order.setLat(LatReciver);
                                    order.setLng(LngReciver);
                                }
                                else
                                {
                                    order.setLat(-0.1f);
                                    order.setLng(-0.1f);

                                }

                                order.setCod(true);
                                order.setTransactionId("Khlas k tousel");
                                order.setRestaurantName("Service");
                                order.setShipperName("noOne");
                                order.setShipperPhone("69");

                                syncLocalTimeWithGlobaletime(order);



                            });
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    Toast.makeText(getContext(), "3amrelna chnw t7eb !", Toast.LENGTH_SHORT).show();
                }

            }

            private void syncLocalTimeWithGlobaletime(Order order) {

                final DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
                offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long offset = dataSnapshot.getValue(Long.class);
                        long estimateServerTimeMs = System.currentTimeMillis()+offset;
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                        Date resultDate = new Date(estimateServerTimeMs);
                        Log.d("TEST_DATE",""+sdf.format(resultDate));
                        listener.onLoadTimeSuccess(order,estimateServerTimeMs);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onLoadTimeFailed(databaseError.getMessage());

                    }
                });
            }
        });







        return root;
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
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

    private String getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String result = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
            if (addressList != null && addressList.size() > 0)
            {
                Address address  = addressList.get(0);
                StringBuilder sb = new StringBuilder(address.getAddressLine(0));
                result = sb.toString();
            }
            else
                result = "Addristek moch mawjouda";
        }
        catch (Exception e){

            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLoadTimeSuccess(Order order, long estimateTimeTnMs) {
        order.setCreateDate(estimateTimeTnMs);
        order.setOrderStatus(0);
        writeOrderToFirebase(order);

    }

    private void writeOrderToFirebase(Order order) {
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();

        FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.TASKS_REF)
                .child(Common.createOrderNumber())
                .setValue(order)
                .addOnFailureListener( e -> {
                    //Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }).addOnCompleteListener(task -> {

                    loading_dialog.dismiss();
                    Toast.makeText(getContext(), "C bon el commande t3adet", Toast.LENGTH_SHORT).show();



                    sendNotification(notif);


                    EventBus.getDefault().postSticky(new PlaceOrderClicked(true));



        });


    }

    private void sendNotification(String msg) {
        String urlfin = "https://api.telegram.org/bot5681889281:AAEdJX342l9sNybAypQf3PN11pOAAb2Rokk/sendMessage?chat_id=-1001646604884&text=";
        urlfin +=msg;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlfin)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                }
            }
        });
    }

    @Override
    public void onLoadTimeFailed(String message) {
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();

    }
}