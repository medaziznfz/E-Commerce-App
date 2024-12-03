package x7030.nefzi.tjinitaw.ui.pharmacie;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitaw.Adapter.MyPharmacieAdapter;
import x7030.nefzi.tjinitaw.R;

public class PharmacieFragment extends Fragment {

    private PharmacieViewModel mViewModel;
    Unbinder unbinder;
    @BindView(R.id.recycler_pharmacie)
    RecyclerView recycler_pharmacie;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyPharmacieAdapter adapter;

    public static PharmacieFragment newInstance() {
        return new PharmacieFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(PharmacieViewModel.class);
        View root = inflater.inflate(R.layout.pharmacie_fragment, container, false);
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
        mViewModel.getPharmacieListMutable().observe(getViewLifecycleOwner(),pharmacieModels -> {
            adapter = new MyPharmacieAdapter(getContext(),pharmacieModels);
            recycler_pharmacie.setAdapter(adapter);
            recycler_pharmacie.setLayoutAnimation(layoutAnimationController);
            loading_dialog.dismiss();
        });
        return root;


    }

    private void initViews() {

        setHasOptionsMenu(true);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_pharmacie.setLayoutManager(linearLayoutManager);
        recycler_pharmacie.addItemDecoration(new DividerItemDecoration(getContext(),linearLayoutManager.getOrientation()));








    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}