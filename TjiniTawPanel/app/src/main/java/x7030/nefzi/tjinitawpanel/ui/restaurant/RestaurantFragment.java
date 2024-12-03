package x7030.nefzi.tjinitawpanel.ui.restaurant;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitawpanel.Adapter.MyRestaurantActiveAdapter;
import x7030.nefzi.tjinitawpanel.Common.Common;
import x7030.nefzi.tjinitawpanel.Common.MySwipeHelper;
import x7030.nefzi.tjinitawpanel.EventBus.UpdateRestaurantEvent;
import x7030.nefzi.tjinitawpanel.Model.RestaurantModel;
import x7030.nefzi.tjinitawpanel.R;


public class RestaurantFragment extends Fragment {

    private RestaurantViewModel restaurantViewModel;
    private Unbinder unbinder;

    @BindView(R.id.recycler_restaurant)
    RecyclerView recycler_restaurant;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyRestaurantActiveAdapter adapter ;
    List<RestaurantModel> restaurantModelList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantViewModel =
                new ViewModelProvider(this).get(RestaurantViewModel.class);
        View itemView  = inflater.inflate(R.layout.fragment_restaurant,container,false);

        unbinder = ButterKnife.bind(this,itemView);
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();




        initViews();
        restaurantViewModel.getMessageError().observe(getViewLifecycleOwner(),s ->{
            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
            loading_dialog.dismiss();

        });
        restaurantViewModel.getRestaurantMutableList().observe(getViewLifecycleOwner(),shippers->{
            restaurantModelList = shippers;
            adapter = new MyRestaurantActiveAdapter(getContext(),restaurantModelList);
            recycler_restaurant.setAdapter(adapter);
            recycler_restaurant.setLayoutAnimation(layoutAnimationController);
            loading_dialog.dismiss();


        });
        return itemView;



    }

    private void initViews() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(Common.RESTAURANT_REF);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_restaurant.setLayoutManager(layoutManager);
        recycler_restaurant.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        MySwipeHelper swipeHelper = new MySwipeHelper(getContext(), recycler_restaurant, width/5) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Kallmou", 30, 0, Color.parseColor("#C5C5C5"),
                        pos -> {

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
                                    .append(restaurantModelList.get(pos).getPhone()).toString()));
                            startActivity(intent);



                        }));

            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateRestaurantEvent.class))
            EventBus.getDefault().removeStickyEvent(UpdateRestaurantEvent.class);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true,threadMode =  ThreadMode.MAIN)
    public void onUpdateRestaurantActive(UpdateRestaurantEvent event)
    {

        Map<String,Object> updateData = new HashMap<>();
        updateData.put("status",event.isActive());
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.RESTAURANT_REF)
                .child(event.getRestaurantModel().getUid())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "saye badelneh l "+event.isActive(), Toast.LENGTH_SHORT).show());

    }
}
