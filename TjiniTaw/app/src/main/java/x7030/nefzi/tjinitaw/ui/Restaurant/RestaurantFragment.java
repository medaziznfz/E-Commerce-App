package x7030.nefzi.tjinitaw.ui.Restaurant;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitaw.Adapter.MyRestaurantAdapter;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.EventBus.HideFABCart;
import x7030.nefzi.tjinitaw.R;

public class RestaurantFragment extends Fragment {

    private RestaurantViewModel mViewModel;
    Unbinder unbinder;
    @BindView(R.id.recycler_restauernt)
    RecyclerView recycler_restaurant;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyRestaurantAdapter adapter;

    public static RestaurantFragment newInstance() {
        return new RestaurantFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        View root = inflater.inflate(R.layout.fragment_restaurant, container, false);
        unbinder = ButterKnife.bind(this,root);
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();

        initViews();
        mViewModel.getMessageError().observe(getViewLifecycleOwner(),message ->{
            loading_dialog.dismiss();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
        mViewModel.getRestaurantListMutable().observe(getViewLifecycleOwner(),restaurantModels -> {
            adapter = new MyRestaurantAdapter(getContext(),restaurantModels);
            recycler_restaurant.setAdapter(adapter);
            recycler_restaurant.setLayoutAnimation(layoutAnimationController);
            loading_dialog.dismiss();
        });
        return root;


    }

    private void initViews() {

        //EventBus.getDefault().postSticky(new HideFABCart(true));

        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(Common.RESTAURANT_REF);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_restaurant.setLayoutManager(linearLayoutManager);
        recycler_restaurant.addItemDecoration(new DividerItemDecoration(getContext(),linearLayoutManager.getOrientation()));








    }

    @Override
    public void onResume() {
        super.onResume();
        //EventBus.getDefault().postSticky(new CounterCartEvent(true));
        //EventBus.getDefault().postSticky(new MenuInflateEvent(false));
    }

    @Override
    public void onStart() {
        EventBus.getDefault().postSticky(new HideFABCart(true));
        super.onStart();
    }
}