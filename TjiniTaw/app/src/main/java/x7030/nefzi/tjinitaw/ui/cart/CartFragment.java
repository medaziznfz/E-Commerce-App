package x7030.nefzi.tjinitaw.ui.cart;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import x7030.nefzi.tjinitaw.Adapter.MyCartAdapter;
import x7030.nefzi.tjinitaw.Callback.ILoadTimeFromFirebaseListener;
import x7030.nefzi.tjinitaw.Callback.ISearchCategoryCallbackListener;
import x7030.nefzi.tjinitaw.CaptureActivityPortrait;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Common.MySwipeHelper;
import x7030.nefzi.tjinitaw.Database.CartDataSource;
import x7030.nefzi.tjinitaw.Database.CartDatabase;
import x7030.nefzi.tjinitaw.Database.CartItem;
import x7030.nefzi.tjinitaw.Database.LocalCartDataSource;
import x7030.nefzi.tjinitaw.EventBus.CounterCartEvent;
import x7030.nefzi.tjinitaw.EventBus.HideFABCart;
import x7030.nefzi.tjinitaw.EventBus.PlaceOrderClicked;
import x7030.nefzi.tjinitaw.EventBus.ReportClicked;
import x7030.nefzi.tjinitaw.EventBus.UpdateItemInCart;
import x7030.nefzi.tjinitaw.Model.AddonModel;
import x7030.nefzi.tjinitaw.Model.CategoryModel;
import x7030.nefzi.tjinitaw.Model.DiscountModel;
import x7030.nefzi.tjinitaw.Model.FoodModel;
import x7030.nefzi.tjinitaw.Model.Order;
import x7030.nefzi.tjinitaw.Model.SizeModel;
import x7030.nefzi.tjinitaw.R;

public class CartFragment extends Fragment implements ILoadTimeFromFirebaseListener, ISearchCategoryCallbackListener, TextWatcher {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private BottomSheetDialog addonBottomSheetDialog;
    private ChipGroup chip_group_addon,chip_group_user_selected_addon;
    private EditText edt_search;
    private String code;
    private Double LatReciver = -1.0;
    private Double LngReciver = -1.0;
    String notif = ">>>>>>>>>>>Restaurant<<<<<<<<<<%0A";



    private ISearchCategoryCallbackListener searchFoodCallbackListener;

    private Parcelable recyclerViewState;
    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.empty)
    ImageView txt_is_empty;
    @BindView(R.id.group_place_holder)
    CardView group_place_holder;
    @BindView(R.id.edt_discount_code)
    EditText edt_discount_code;

    @OnClick(R.id.img_scan)
    void onScanQRCode(){


        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(CartFragment.this);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scani el code QR");
        integrator.setBeepEnabled(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.initiateScan();



    }

    @OnClick(R.id.img_check)
    void onApplyDiscount(){
        if (!TextUtils.isEmpty(edt_discount_code.getText().toString()))
        {
            code=edt_discount_code.getText().toString();
            final DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
            final DatabaseReference discountRef = FirebaseDatabase.getInstance().getReference()
                    .child(Common.DISCOUNT_REF);
            offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    long offset = snapshot.getValue(Long.class);
                    long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                    discountRef.child(edt_discount_code.getText().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if (snapshot.exists())
                                    {
                                        DiscountModel discountModel = snapshot.getValue(DiscountModel.class);
                                        discountModel.setKey(snapshot.getKey());
                                        if (discountModel.getUntilDate() < estimatedServerTimeMs)
                                        {
                                            listener.onLoadTimeFailed("El code Promo wfe e delai mte3ou");
                                        }
                                        else
                                        {
                                            Common.discountApply = discountModel;
                                            sumAllItemInCart();
                                        }

                                    }
                                    else
                                    {
                                        listener.onLoadTimeFailed("Code Promo ghalet");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                    listener.onLoadTimeFailed(error.getMessage());

                                }
                            });

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    listener.onLoadTimeFailed(error.getMessage());

                }
            });
        }




    }


    private MyCartAdapter adapter;

    private CartDataSource cartDataSource;

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    ILoadTimeFromFirebaseListener listener;


    private Unbinder unbinder;

    private CartViewModel cartViewModel;

    @OnClick(R.id.btn_place_order)
    void onPlaceOrderClick() {




        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("3AMRELNA HEDHY");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_place_order, null);

        EditText edt_address = (EditText) view.findViewById(R.id.edt_address);
        EditText edt_comment = (EditText) view.findViewById(R.id.edt_comment);
        RadioButton rdi_home = (RadioButton) view.findViewById(R.id.rdi_home_address);
        RadioButton rdi_other_address = (RadioButton) view.findViewById(R.id.rdi_other_address);
        RadioButton rdi_ship_to_this = (RadioButton) view.findViewById(R.id.rdi_ship_this_address);
        RadioButton rdi_cod = (RadioButton) view.findViewById(R.id.rdi_cod);


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
                View loading_layout = LayoutInflater.from(getContext())
                        .inflate(R.layout.loading_dialog,null);
                AlertDialog.Builder builderLoad = new AlertDialog.Builder(getContext()).setView(loading_layout);
                AlertDialog loading_dialog = builderLoad.create();
                loading_dialog.setCancelable(false);
                loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
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


        builder.setView(view);
        builder.setNegativeButton("Batelt", (dialogInterface, which) -> {
            dialogInterface.dismiss();

        }).setPositiveButton("3addi", (dialogInterface, which) -> {
            //Toast.makeText(getContext(), "Implement late!", Toast.LENGTH_SHORT).show();
            if (rdi_cod.isChecked()) {

                if (Common.currentRestaurant.getStatus())
                {
                    paymentCOD(edt_address.getText().toString(), edt_comment.getText().toString());
                }
                else
                    Toast.makeText(getContext(), "Dzl "+Common.currentRestaurant.getName()+" Msaker", Toast.LENGTH_SHORT).show();

            }

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void paymentCOD(String address, String comment) {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getName(),Common.currentRestaurant.getUid()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    cartDataSource.sumPriceInCart(Common.currentUser.getName(),Common.currentRestaurant.getUid()).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Double>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Double totalPrice) {
                            double finalPrice = totalPrice;
                            Order order = new Order();
                            order.setUserId(Common.currentUser.getPhone());
                            order.setUserName(Common.currentUser.getName());
                            order.setUserPhone(Common.currentUser.getPhone());
                            order.setShippingAddress(address);
                            order.setComment(comment);
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
                            order.setCartItemList(cartItems);
                            order.setTotalePayment(totalPrice);
                            if (Common.discountApply != null) {
                                order.setDiscount(Common.discountApply.getPercent());
                                deletDiscount(code);
                                Common.discountApply=null;

                            }

                            else
                                order.setDiscount(0);
                            order.setFinalPayment(finalPrice-((finalPrice*order.getDiscount())/100));
                            order.setCod(true);
                            order.setTransactionId("Khlas k tousel");
                            order.setRestaurantName(Common.currentRestaurant.getName());
                            order.setShipperName("noOne");
                            order.setShipperPhone("69");


                            notif += "Restaurant : "+Common.currentRestaurant.getName()+"%0A";
                            notif += "User : "+Common.currentUser.getName()+"%0A";
                            notif += "Phone : "+Common.currentUser.getPhone()+"%0A";
                            notif += "Address : "+order.getShippingAddress()+"%0A";
                            notif += "Totale : "+(finalPrice-((finalPrice*order.getDiscount())/100))+" TND%0A";

                            syncLocalTimeWithGlobaletime(order);

                        }

                        private void deletDiscount(String text) {

                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child(Common.DISCOUNT_REF)
                                    .child(text.toString())
                                    .removeValue()
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {

                                        }
                                        else
                                            Toast.makeText(getContext(), "Code Promo moch mawjoud !!!", Toast.LENGTH_SHORT).show();


                                    });

                        }

                        @Override
                        public void onError(Throwable e) {
                            //Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });



                }, throwable -> {
                    //Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                }));


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
            cartDataSource.clearCart(Common.currentUser.getName(),Common.currentRestaurant.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@NotNull Integer integer) {
                            recycler_cart.setVisibility(View.GONE);
                            group_place_holder.setVisibility(View.GONE);
                            txt_is_empty.setVisibility(View.VISIBLE);
                            loading_dialog.dismiss();
                            sendNotification(notif);
                            Toast.makeText(getContext(), "C bon el commande t3adet", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().postSticky(new PlaceOrderClicked(true));


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
                        public void onError(@NotNull Throwable e) {
                            //Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


        });


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


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        //ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        listener = this;

        cartViewModel.initCartDataSource(getContext());
        cartViewModel.getMutableLiveDataCartItems().observe(getViewLifecycleOwner(), new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {
                if (cartItems == null || cartItems.isEmpty()) {
                    recycler_cart.setVisibility(View.GONE);
                    group_place_holder.setVisibility(View.GONE);
                    txt_is_empty.setVisibility(View.VISIBLE);

                } else {
                    recycler_cart.setVisibility(View.VISIBLE);
                    group_place_holder.setVisibility(View.VISIBLE);
                    txt_is_empty.setVisibility(View.GONE);

                    adapter = new MyCartAdapter(getContext(), cartItems);
                    recycler_cart.setAdapter(adapter);


                }
            }
        });

        unbinder = ButterKnife.bind(this, root);
        initViews();
        initLocation();
        return root;
    }

    private void initLocation() {
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());


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

    private void initViews() {

        searchFoodCallbackListener = this;




        setHasOptionsMenu(true);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());
        EventBus.getDefault().postSticky(new HideFABCart(true));

        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_cart.setLayoutManager(layoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

            MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recycler_cart, width / 4) {
                @Override
                public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                    buf.add(new MyButton(getContext(), "Fasakh", 30, 0, Color.parseColor("#ab1033"),
                            pos -> {
                                CartItem cartItem = adapter.getItemAtPosition(pos);
                                cartDataSource.deleteCartItem(cartItem).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new SingleObserver<Integer>() {
                                            @Override
                                            public void onSubscribe(@NotNull Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(@NotNull Integer integer) {
                                                adapter.notifyItemRemoved(pos);
                                                sumAllItemInCart();
                                                EventBus.getDefault().postSticky(new CounterCartEvent(true));

                                            }

                                            @Override
                                            public void onError(@NotNull Throwable e) {
                                                //if (!e.getMessage().contains("Query returned empty"))
                                                //Toast.makeText(getContext(), "{SUM CART}"+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }));

                    buf.add(new MyButton(getContext(), "Baddel", 30, 0, Color.parseColor("#094184"),
                            pos -> {
                                CartItem cartItem = adapter.getItemAtPosition(pos);
                                View loading_layout = LayoutInflater.from(getContext())
                                        .inflate(R.layout.loading_dialog,null);
                                AlertDialog.Builder builderLoad = new AlertDialog.Builder(getContext()).setView(loading_layout);
                                AlertDialog loading_dialog = builderLoad.create();
                                loading_dialog.setCancelable(false);
                                loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                                loading_dialog.show();
                                FirebaseDatabase.getInstance()
                                        .getReference(Common.RESTAURANT_REF)
                                        .child(Common.currentRestaurant.getUid())
                                        .child(Common.CATEGORY_REF)
                                        .child(cartItem.getCategoryId())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    loading_dialog.dismiss();
                                                    CategoryModel categoryModel = dataSnapshot.getValue(CategoryModel.class);
                                                    searchFoodCallbackListener.onSearchCategoryFound(categoryModel, cartItem);


                                                } else {
                                                    loading_dialog.dismiss();
                                                    searchFoodCallbackListener.onSearchCategoryNotFound("Ma 3andek ma tbadel fel produit");

                                                }


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                loading_dialog.dismiss();
                                                searchFoodCallbackListener.onSearchCategoryNotFound(databaseError.getMessage());

                                            }
                                        });
                            }));


                }
            };



        sumAllItemInCart();
        addonBottomSheetDialog = new BottomSheetDialog(getContext(),R.style.DialogStyle);
        View layout_addon_display = getLayoutInflater().inflate(R.layout.layout_addon_display,null);
        chip_group_addon = (ChipGroup)layout_addon_display.findViewById(R.id.chip_group_addon);
        edt_search = (EditText)layout_addon_display.findViewById(R.id.edt_search);
        addonBottomSheetDialog.setContentView(layout_addon_display);

        addonBottomSheetDialog.setOnDismissListener(dialogInterface -> {
            displayUserSelectedAddon(chip_group_user_selected_addon);
            calculateTotalPrice();

        });



    }

    private void displayUserSelectedAddon(ChipGroup chip_group_user_selected_addon) {
        if (Common.selectedFood.getUserSelectedAddon() != null && Common.selectedFood.getUserSelectedAddon().size() > 0)
        {
            chip_group_user_selected_addon.removeAllViews();
            for (AddonModel addonModel:Common.selectedFood.getUserSelectedAddon())
            {
                Chip chip = (Chip)getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+").append(addonModel.getPrice()).append(" TND)"));
                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b)
                    {
                        if (Common.selectedFood.getUserSelectedAddon() == null)
                            Common.selectedFood.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedFood.getUserSelectedAddon().add(addonModel);
                    }

                });
                chip_group_user_selected_addon.addView(chip);
            }
        }
        else
            chip_group_user_selected_addon.removeAllViews();
    }

    private void sumAllItemInCart() {
        cartDataSource.sumPriceInCart(Common.currentUser.getName(),Common.currentRestaurant.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull Double aDouble) {
                        if (Common.discountApply != null)
                        {
                            aDouble = aDouble - (aDouble*Common.discountApply.getPercent()/100);
                            txt_total_price.setText(new StringBuilder("Total: ").append(Common.formatPrice(aDouble))
                                    .append("(-").append(Common.discountApply.getPercent())
                                    .append("%)"));
                        }
                        else
                        {
                            txt_total_price.setText(new StringBuilder("Total: ").append(Common.formatPrice(aDouble)));
                        }





                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        //if (!e.getMessage().contains("Query returned empty"))
                        //Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().postSticky(new HideFABCart(true));
        EventBus.getDefault().postSticky(new CounterCartEvent(true));
        calculateTotalPrice();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().postSticky(new CounterCartEvent(true));
        cartViewModel.onStop();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onUpdateItemInCart(UpdateItemInCart event)
    {
        if (event.getCartItem() != null)
        {
            recyclerViewState = recycler_cart.getLayoutManager().onSaveInstanceState();
            cartDataSource.UpdateCartItems(event.getCartItem())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@NotNull Integer integer) {
                            calculateTotalPrice();
                            recycler_cart.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            //if (! e.getMessage().contains("Query returned empty result set "))
                            // Toast.makeText(getContext(), "{UPDATE CART}"+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }


    }

    private void calculateTotalPrice() {

        cartDataSource.sumPriceInCart(Common.currentUser.getName(),Common.currentRestaurant.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double price) {
                        txt_total_price.setText(new StringBuilder("Total: ").append(Common.formatPrice(price)));


                    }

                    @Override
                    public void onError(Throwable e) {
                        //if (! e.getMessage().contains("Query returned empty result set "))
                        //Toast.makeText(getContext(), "{SUM CART}"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public void onLoadTimeSuccess(Order order, long estimateTimeTnMs) {
        order.setCreateDate(estimateTimeTnMs);
        order.setOrderStatus(0);
        writeOrderToFirebase(order);

    }

    @Override
    public void onLoadTimeFailed(String message) {
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSearchCategoryFound(CategoryModel categoryModel,CartItem cartItem) {
        FoodModel foodModel = Common.findFoodInListById(categoryModel,cartItem.getFoodId());
        if (foodModel != null)
        {
            showUpdateDialog(cartItem,foodModel);
        }
        else
        {
            Toast.makeText(getContext(), "Food Id not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdateDialog(CartItem cartItem, FoodModel foodModel) {
        Common.selectedFood = foodModel;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_update_cart,null);
        builder.setView(itemView);

        Button btn_ok = (Button)itemView.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button)itemView.findViewById(R.id.btn_cancel);

        RadioGroup rdi_group_size = (RadioGroup)itemView.findViewById(R.id.rdi_group_size);
        chip_group_user_selected_addon = (ChipGroup)itemView.findViewById(R.id.chip_group_user_selected_addon);
        ImageView img_add_on = (ImageView)itemView.findViewById(R.id.img_add_addon);
        img_add_on.setOnClickListener(view -> {
            if (foodModel.getAddon() != null)
            {
                displayAddonList();
                addonBottomSheetDialog.show();
            }

        });

        if (foodModel.getSize() != null) {
            for (SizeModel sizeModel : foodModel.getSize()){
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b)
                        Common.selectedFood.setUserSelectedSize(sizeModel);
                    calculateTotalPrice();
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1.0f);
                radioButton.setLayoutParams(params);
                radioButton.setText(sizeModel.getName());
                radioButton.setTag(sizeModel.getPrice());
                rdi_group_size.addView(radioButton);




            }


            if (rdi_group_size.getChildCount() > 0) {
                RadioButton radioButton = (RadioButton)rdi_group_size.getChildAt(0);
                radioButton.setChecked(true);

            }


            displayAlreadySelectedAddon(chip_group_user_selected_addon,cartItem);

            AlertDialog dialog = builder.create();
            dialog.show();

            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
            btn_ok.setOnClickListener(view -> {

                cartDataSource.deleteCartItem(cartItem)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                if (Common.selectedFood.getUserSelectedAddon() != null && !Common.selectedFood.getUserSelectedAddon().isEmpty())
                                    cartItem.setFoodAddon(new Gson().toJson(Common.selectedFood.getUserSelectedAddon()));
                                else
                                    cartItem.setFoodAddon("7ata shy");
                                if (Common.selectedFood.getUserSelectedSize() != null)
                                    cartItem.setFoodSize(new Gson().toJson(Common.selectedFood.getUserSelectedSize()));
                                else
                                    cartItem.setFoodSize("3adi");
                                cartItem.setFoodExtraPrice(Common.calculateExtraPrice(Common.selectedFood.getUserSelectedSize()
                                        ,Common.selectedFood.getUserSelectedAddon()));


                                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(()->{
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                            calculateTotalPrice();
                                            dialog.dismiss();

                                        },throwable -> {
                                            //Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                        })

                                );
                            }

                            @Override
                            public void onError(Throwable e) {
                                //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

            });
            btn_cancel.setOnClickListener(view -> {
                dialog.dismiss();

            });





        }

    }

    private void displayAlreadySelectedAddon(ChipGroup chip_group_user_selected_addon, CartItem cartItem) {

        if (cartItem.getFoodAddon() != null && !cartItem.getFoodAddon().equals("7ata shy"))
        {
            List<AddonModel> addonModels = new Gson().fromJson(
                    cartItem.getFoodAddon(),new TypeToken<List<AddonModel>>(){}.getType());
            Common.selectedFood.setUserSelectedAddon(addonModels);
            chip_group_user_selected_addon.removeAllViews();
            for (AddonModel addonModel:addonModels)
            {
                Chip chip = (Chip)getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+").append(addonModel.getPrice()).append(" TND)"));
                chip.setOnCloseIconClickListener(view -> {

                    chip_group_user_selected_addon.removeView(view);
                    Common.selectedFood.getUserSelectedAddon().remove(addonModel);
                    calculateTotalPrice();

                });
                chip_group_user_selected_addon.addView(chip);
            }
        }

    }

    private void displayAddonList() {
        if (Common.selectedFood.getAddon() != null && Common.selectedFood.getAddon().size() > 0)
        {
            chip_group_addon.clearCheck();
            chip_group_addon.removeAllViews();

            edt_search.addTextChangedListener(this);

            for (AddonModel addonModel:Common.selectedFood.getAddon())
            {
                Chip chip = (Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+").append(addonModel.getPrice()).append(" TND)"));
                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b)
                    {
                        if (Common.selectedFood.getUserSelectedAddon() == null)
                            Common.selectedFood.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedFood.getUserSelectedAddon().add(addonModel);
                    }

                });
                chip_group_addon.addView(chip);
            }
        }
    }

    @Override
    public void onSearchCategoryNotFound(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        chip_group_addon.clearCheck();
        chip_group_addon.removeAllViews();
        for (AddonModel addonModel:Common.selectedFood.getAddon())
        {
            if (addonModel.getName().toLowerCase().contains(charSequence.toString().toLowerCase()))
            {
                Chip chip = (Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+").append(addonModel.getPrice()).append(" TND)"));
                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b)
                    {
                        if (Common.selectedFood.getUserSelectedAddon() == null)
                            Common.selectedFood.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedFood.getUserSelectedAddon().add(addonModel);
                    }

                });
                chip_group_addon.addView(chip);
            }
        }


    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {



            } else {
                edt_discount_code.setText(result.getContents());

            }
        }
    }






}
