package x7030nefzi.admin.ui.shippers;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030nefzi.admin.Adapter.MyShippersAdapter;
import x7030nefzi.admin.Common.Common;
import x7030nefzi.admin.Common.MySwipeHelper;
import x7030nefzi.admin.Model.ShipperModel;
import x7030nefzi.admin.R;

public class ShippersFragment extends Fragment {

    private ShippersViewModel mViewModel;
    private Unbinder unbinder;

    @BindView(R.id.recycler_shippers)
    RecyclerView recycler_shippers;
    LayoutAnimationController layoutAnimationController;
    MyShippersAdapter adapter;
    List<ShipperModel> shippersModelList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        mViewModel = new ViewModelProvider(this).get(ShippersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shippers, container, false);
        unbinder = ButterKnife.bind(this, root);
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext()).setView(loading_layout);
        android.app.AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();
        initView();
        mViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            loading_dialog.dismiss();
        });
        mViewModel.getShippersMutableLiveData().observe(getViewLifecycleOwner(), list -> {
            if (list == null)
                shippersModelList = new ArrayList<>();
            else
                shippersModelList = list;
            adapter = new MyShippersAdapter(getContext(), shippersModelList);
            recycler_shippers.setAdapter(adapter);
            recycler_shippers.setLayoutAnimation(layoutAnimationController);
            loading_dialog.dismiss();

        });

        return root;

    }

    private void initView() {
        setHasOptionsMenu(true);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_shippers.setLayoutManager(layoutManager);
        recycler_shippers.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        MySwipeHelper swipeHelper = new MySwipeHelper(getContext(), recycler_shippers, width/7) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "fasakh", 30, 0, Color.parseColor("#333639"),
                        pos -> {

                            Common.shipperSelected = shippersModelList.get(pos);
                            showDeleteDialog();


                        }));

                buf.add(new MyButton(getContext(), "baddel", 30, 0, Color.parseColor("#414243"),
                        pos -> {

                            Common.shipperSelected = shippersModelList.get(pos);
                            showUpdateDialog();


                        }));
                buf.add(new MyButton(getContext(), "khalles", 30, 0, Color.parseColor("#00FF00"),
                        pos -> {

                            Common.shipperSelected = shippersModelList.get(pos);
                            remiseazero();


                        }));
            }

            private void remiseazero() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("REMISE A ZERO");
                builder.setMessage("Saye bech tet7aseb enty o eyeh ?");

                builder.setNegativeButton("LE", ((dialogInterface, i) -> dialogInterface.dismiss()))
                        .setPositiveButton("EY", ((dialogInterface, i) -> {
                            Map<String,Object> update_data = new HashMap<>();
                            update_data.put("fees",0.0);
                            updateShipper(update_data);

                        }));
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
    }


    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Zid Livreur");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_shipper, null);

        EditText edt_name = (EditText) itemView.findViewById(R.id.edt_name);
        EditText edt_phone = (EditText) itemView.findViewById(R.id.edt_phone);
        EditText edt_password = (EditText) itemView.findViewById(R.id.edt_password);



        builder.setNegativeButton("BATELT", ((dialogInterface, i) -> dialogInterface.dismiss()))
                .setPositiveButton("MRYGL", ((dialogInterface, i) -> {

                    ShipperModel shipperModel = new ShipperModel();
                    shipperModel.setName(edt_name.getText().toString());
                    shipperModel.setPhone(edt_phone.getText().toString());
                    shipperModel.setPassword(edt_password.getText().toString());
                    shipperModel.setFees(0.0);


                    createShipper(shipperModel);

                }));
        builder.setView(itemView);
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void createShipper(ShipperModel shipperModel) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.SHIPPERS_REF)
                .child(shipperModel.getPhone())
                .setValue(shipperModel)
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        mViewModel.loadShippers();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "hawka saye", Toast.LENGTH_SHORT).show();
                    }


                });


    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Baddel");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_shipper, null);

        EditText edt_name = (EditText) itemView.findViewById(R.id.edt_name);
        EditText edt_phone = (EditText) itemView.findViewById(R.id.edt_phone);
        edt_phone.setEnabled(false);
        EditText edt_password = (EditText) itemView.findViewById(R.id.edt_password);

        edt_name.setText(Common.shipperSelected.getName());
        edt_phone.setText(Common.shipperSelected.getPhone());
        edt_password.setText(Common.shipperSelected.getPassword());



        builder.setNegativeButton("BATELT", ((dialogInterface, i) -> dialogInterface.dismiss()))
                .setPositiveButton("BADDEL", ((dialogInterface, i) -> {
                    Map<String,Object> update_data = new HashMap<>();
                    update_data.put("name",edt_name.getText().toString());
                    update_data.put("password",edt_password.getText().toString());


                    updateShipper(update_data);

                }));
        builder.setView(itemView);
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void updateShipper(Map<String, Object> update_data) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.SHIPPERS_REF)
                .child(Common.shipperSelected.getPhone())
                .updateChildren(update_data)
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        mViewModel.loadShippers();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "hawka saye !", Toast.LENGTH_SHORT).show();
                    }


                });


    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Fasakh");
        builder.setMessage("Mthabet nfaskhou el code ");
        builder.setNegativeButton("BATELT", ((dialogInterface, i) -> dialogInterface.dismiss()))
                .setPositiveButton("FASAKH", ((dialogInterface, i) -> {
                    deleteDiscount();


                }));
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void deleteDiscount() {

        FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.SHIPPERS_REF)
                .child(Common.shipperSelected.getPhone())
                .removeValue()
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        mViewModel.loadShippers();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "awka saye fasakhneh", Toast.LENGTH_SHORT).show();
                    }


                });


    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.shippers_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_create)
            showAddDialog();
        return super.onOptionsItemSelected(item);
    }
}
