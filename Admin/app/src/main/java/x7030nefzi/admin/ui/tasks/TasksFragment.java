package x7030nefzi.admin.ui.tasks;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import x7030nefzi.admin.Adapter.MyOrderAdapter;
import x7030nefzi.admin.Callback.IOrderCallbackListener;
import x7030nefzi.admin.Common.Common;
import x7030nefzi.admin.Common.MySwipeHelper;
import x7030nefzi.admin.Model.OrderModel;
import x7030nefzi.admin.R;
import x7030nefzi.admin.ShippingActivity;


public class TasksFragment extends Fragment implements IOrderCallbackListener {
    AlertDialog dialog;
    //CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SwipeRefreshLayout swipeLayout;
    LayoutAnimationController layoutAnimationController;

    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;
    @BindView(R.id.txt_order_filter)
    TextView txt_order_filter;

    private Unbinder unbinder;

    private TasksViewModel viewOrdersViewModel;
    private IOrderCallbackListener listener;
    private MyOrderAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewOrdersViewModel =
                new ViewModelProvider(this).get(TasksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

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
        initViews(root);
        loadOrderFromFirebase();

        viewOrdersViewModel.getMutableLiveDataOrderList().observe(getViewLifecycleOwner(), orderList -> {

            adapter = new MyOrderAdapter(getContext(), orderList);
            recycler_orders.setAdapter(adapter);
            recycler_orders.setLayoutAnimation(layoutAnimationController);

            updateTextCounter();


        });

        return root;
    }

    private void updateTextCounter() {

        txt_order_filter.setText(new StringBuilder("Les Taches eli 3ana (").append(adapter.getItemCount()).append(")"));

    }

    private void loadOrderFromFirebase() {
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();
        List<OrderModel> orderList = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.TASKS_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapShot : dataSnapshot.getChildren()) {
                            OrderModel order = orderSnapShot.getValue(OrderModel.class);
                            order.setOrderNumber(orderSnapShot.getKey());
                                orderList.add(order);


                        }
                        listener.onOrderLoadSuccess(orderList);
                        loading_dialog.dismiss();
                        swipeLayout.setRefreshing(false);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onOrderLoadFailed(databaseError.getMessage());

                    }
                });


    }

    private void initViews(View root) {
        //cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        setHasOptionsMenu(true);
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


        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recycler_orders, width / 7) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Fasakh", 30, 0, Color.parseColor("#ab1033"),
                        pos -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                    .setTitle("Bech tfasakh")
                                    .setMessage("Mthabet T7eb Tfasakh el commande?")
                                    .setNegativeButton("BATELT", (dialog, which) -> dialog.dismiss())
                                    .setPositiveButton("FASAKH", (dialog, which) -> {
                                        loading_dialog.show();
                                        OrderModel orderModel = adapter.getItemPosition(pos);
                                        FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child(Common.TASKS_REF)
                                                .child(orderModel.getOrderNumber())
                                                .removeValue()
                                                .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                                .addOnSuccessListener(aVoid -> {
                                                    loading_dialog.dismiss();
                                                    adapter.removeItem(pos);
                                                    adapter.notifyItemRemoved(pos);
                                                    updateTextCounter();
                                                    dialog.dismiss();


                                                    Toast.makeText(getContext(), "Saye awka tfasakh", Toast.LENGTH_SHORT).show();

                                                });


                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            negativeButton.setTextColor(Color.GRAY);
                            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            positiveButton.setTextColor(Color.RED);


                        }));


                buf.add(new MyButton(getContext(), "Annuler", 30, 0, Color.parseColor("#ff4444"),
                        pos -> {

                            OrderModel orderModel = ((MyOrderAdapter) recycler_orders.getAdapter()).getItemPosition(pos);
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                            if (orderModel.getOrderStatus() == 0) {
                                builder.setTitle("Annuler lel commande")
                                        .setMessage("Mthabet theb t'annulih ?")
                                        .setNegativeButton("BATELT", (dialogInterface, i) -> dialogInterface.dismiss())
                                        .setPositiveButton("Annuller", (dialogInterface, i) -> {
                                            loading_dialog.show();
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
                                                        recycler_orders.getAdapter().notifyItemChanged(pos);
                                                        loading_dialog.dismiss();
                                                        Toast.makeText(getContext(), "Saye el commande annuler", Toast.LENGTH_SHORT).show();
                                                        loadOrderFromFirebase();

                                                    });

                                        });
                                androidx.appcompat.app.AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {

                                Toast.makeText(getContext(), "Matnajemech t'annuleha commande " + Common.convertStatusToString(orderModel.getOrderStatus()), Toast.LENGTH_SHORT).show();
                            }


                        }));

                buf.add(new MyButton(getContext(), "Gps", 30, 0, Color.parseColor("#0E38CF"),
                        pos -> {

                            OrderModel orderModel = ((MyOrderAdapter) recycler_orders.getAdapter()).getItemPosition(pos);
                                Common.currentShippingOrder = orderModel;
                                if (orderModel.getShippingAddress().contains(".") && orderModel.getShippingAddress().contains("/"))
                                    getContext().startActivity(new Intent(getContext(), ShippingActivity.class));
                                else
                                    Toast.makeText(getContext(), "El client moch yesta3mel f Gps", Toast.LENGTH_SHORT).show();


                        }));

                buf.add(new MyButton(getContext(), "Kalmou", 30, 0, Color.parseColor("#ffc600"),
                        pos -> {
                            OrderModel orderModel = ((MyOrderAdapter) recycler_orders.getAdapter()).getItemPosition(pos);

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
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(new StringBuilder("tel:")
                                    .append(orderModel.getUserPhone()).toString()));
                            startActivity(intent);


                        }));

            }
        };



    }

    @Override
    public void onOrderLoadSuccess(List<OrderModel> orderList) {
        Collections.reverse(orderList);
        viewOrdersViewModel.setMutableLiveDataOrderList(orderList);

    }

    @Override
    public void onOrderLoadFailed(String message) {
        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {

        compositeDisposable.clear();


        super.onStop();
    }

}