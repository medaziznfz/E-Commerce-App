package x7030.nefzi.tjinitaw.ui.view_orders;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import x7030.nefzi.tjinitaw.Adapter.MyOrderAdapter;
import x7030.nefzi.tjinitaw.Callback.ILoadOrderCallbackListener;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Common.MySwipeHelper;
import x7030.nefzi.tjinitaw.Database.CartDataSource;
import x7030.nefzi.tjinitaw.Database.CartDatabase;
import x7030.nefzi.tjinitaw.Database.LocalCartDataSource;
import x7030.nefzi.tjinitaw.Model.Order;
import x7030.nefzi.tjinitaw.R;

public class ViewOrdersFragment extends Fragment implements ILoadOrderCallbackListener {
    AlertDialog dialog;
    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SwipeRefreshLayout swipeLayout;
    LayoutAnimationController layoutAnimationController;

    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;

    private Unbinder unbinder;

    private ViewOrdersViewModel viewOrdersViewModel;
    private ILoadOrderCallbackListener listener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewOrdersViewModel =
                new ViewModelProvider(this).get(ViewOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_view_order, container, false);

        swipeLayout = (SwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadOrderFromFirebase();

                
            }
        });



        unbinder = ButterKnife.bind(this, root);
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();
        initViews(root);
        loadOrderFromFirebase();

        viewOrdersViewModel.getMutableLiveDataOrderList().observe(getViewLifecycleOwner(), orderList -> {

            MyOrderAdapter adapter = new MyOrderAdapter(getContext(), orderList);
            recycler_orders.setAdapter(adapter);
            recycler_orders.setLayoutAnimation(layoutAnimationController);
            loading_dialog.dismiss();


        });

        return root;
    }

    private void loadOrderFromFirebase() {
        List<Order> orderList = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.TASKS_REF)
                .orderByChild("userId")
                .equalTo(Common.currentUser.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapShot : dataSnapshot.getChildren()) {
                            Order order = orderSnapShot.getValue(Order.class);
                            order.setOrderNumber(orderSnapShot.getKey());
                            orderList.add(order);


                        }
                        listener.onLoadOrderSuccess(orderList);
                        swipeLayout.setRefreshing(false);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onLoadOrderFailed(databaseError.getMessage());

                    }
                });


    }

    private void initViews(View root) {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        listener = this;


        recycler_orders.setHasFixedSize(true);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_orders.setLayoutManager(layoutManager);
        recycler_orders.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;


        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recycler_orders, width/5) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Annuler", 30, 0, Color.parseColor("#ab1033"),
                        pos -> {
                            loading_dialog.show();
                            Order orderModel = ((MyOrderAdapter) recycler_orders.getAdapter()).getItemAtPosition(pos);
                            if (orderModel.getOrderStatus() == 0) {
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                                builder.setTitle("Annuler")
                                        .setMessage("Mthabet theb t'annuli el commande ?")
                                        .setNegativeButton("Batelt", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                            loading_dialog.dismiss();
                                        })
                                        .setPositiveButton("EY", (dialogInterface, i) -> {

                                            Map<String, Object> update_data = new HashMap<>();
                                            update_data.put("orderStatus", -1);
                                            FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child(Common.TASKS_REF)
                                                    .child(orderModel.getOrderNumber())
                                                    .updateChildren(update_data)
                                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                                    .addOnSuccessListener(aVoid -> {
                                                        orderModel.setOrderStatus(-1);
                                                        ((MyOrderAdapter) recycler_orders.getAdapter()).setItemAtPosition(pos, orderModel);
                                                        recycler_orders.getAdapter().notifyItemChanged(pos);
                                                        loading_dialog.dismiss();
                                                        Toast.makeText(getContext(), "C bon ayka annuler!", Toast.LENGTH_SHORT).show();


                                                    });

                                        });
                                androidx.appcompat.app.AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {
                                loading_dialog.dismiss();
                                Toast.makeText(getContext(), new StringBuilder("el commande mte3ek ")
                                        .append(Common.convertStatusToText(orderModel.getOrderStatus()))
                                        .append(" , 3ala edheka matnajemech t'annuleha!"), Toast.LENGTH_SHORT).show();
                            }


                        }));


                buf.add(new MyButton(getContext(), "Kallmou", 30, 0, Color.parseColor("#E91E63"),
                        pos -> {
                            Order orderModel = ((MyOrderAdapter) recycler_orders.getAdapter()).getItemAtPosition(pos);
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                Dexter.withContext(getContext())
                                        .withPermission(Manifest.permission.CALL_PHONE)
                                        .withListener(new PermissionListener() {
                                            @Override
                                            public void onPermissionGranted(PermissionGrantedResponse response) {


                                            }

                                            @Override
                                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                                Toast.makeText(getContext(), "Lezmek teqbel el permission bech tnajem totleb", Toast.LENGTH_SHORT).show();

                                            }

                                            @Override
                                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                                            }
                                        }).check();

                                return;
                            }
                            if (orderModel.getOrderStatus() == 1) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse(new StringBuilder("tel:")
                                        .append(orderModel.getShipperPhone()).toString()));
                                startActivity(intent);
                            }
                            else 
                            {
                                Toast.makeText(getContext(), "El commande mehich en charge ken t7eb contactina ala noumrouna", Toast.LENGTH_SHORT).show();
                            }





                        }));


            }
        };


    }

    @Override
    public void onLoadOrderSuccess(List<Order> orderList) {
        Collections.reverse(orderList);
        viewOrdersViewModel.setMutableLiveDataOrderList(orderList);

    }

    @Override
    public void onLoadOrderFailed(String message) {
        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();

    }

}
