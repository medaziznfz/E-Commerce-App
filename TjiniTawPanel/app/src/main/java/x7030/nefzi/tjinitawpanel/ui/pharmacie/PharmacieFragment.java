package x7030.nefzi.tjinitawpanel.ui.pharmacie;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitawpanel.Adapter.MyPharmacieAdapter;
import x7030.nefzi.tjinitawpanel.Common.Common;
import x7030.nefzi.tjinitawpanel.EventBus.UpdatePharmacyEvent;
import x7030.nefzi.tjinitawpanel.Model.PharmacieModel;
import x7030.nefzi.tjinitawpanel.R;

public class PharmacieFragment extends Fragment {

    private PharmacieViewModel homeViewModel;
    private Unbinder unbinder;

    @BindView(R.id.recycler_pharmacy)
    RecyclerView recycler_pharmacy;
    LayoutAnimationController layoutAnimationController;
    MyPharmacieAdapter adapter ;
    List<PharmacieModel> pharmacieModelList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(PharmacieViewModel.class);
        View itemView  = inflater.inflate(R.layout.fragment_pharmacie,container,false);

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
        homeViewModel.getMessageError().observe(getViewLifecycleOwner(),s ->{
            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
            loading_dialog.dismiss();

        });
        homeViewModel.getPharmacyMutableList().observe(getViewLifecycleOwner(),shippers->{
            pharmacieModelList = shippers;
            adapter = new MyPharmacieAdapter(getContext(),pharmacieModelList);
            recycler_pharmacy.setAdapter(adapter);
            recycler_pharmacy.setLayoutAnimation(layoutAnimationController);
            loading_dialog.dismiss();


        });
        return itemView;



    }

    private void initViews() {
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_pharmacy.setLayoutManager(layoutManager);
        recycler_pharmacy.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(UpdatePharmacyEvent.class))
            EventBus.getDefault().removeStickyEvent(UpdatePharmacyEvent.class);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true,threadMode =  ThreadMode.MAIN)
    public void onUpdatePharmacyActive(UpdatePharmacyEvent event)
    {

        Map<String,Object> updateData = new HashMap<>();
        updateData.put("active",event.isActive());
        FirebaseDatabase.getInstance()
                .getReference(Common.PHARMACY_REF)
                .child(event.getPharmacieModel().getUid())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Update status to "+event.isActive(), Toast.LENGTH_SHORT).show());

    }
}