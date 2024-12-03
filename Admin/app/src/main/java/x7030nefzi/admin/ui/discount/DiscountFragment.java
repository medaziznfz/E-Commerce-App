package x7030nefzi.admin.ui.discount;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
import x7030nefzi.admin.Adapter.MyDiscountAdapter;
import x7030nefzi.admin.Common.Common;
import x7030nefzi.admin.Common.MySwipeHelper;
import x7030nefzi.admin.Model.DiscountModel;
import x7030nefzi.admin.R;

public class DiscountFragment extends Fragment {

    private DiscountViewModel mViewModel;
    private Unbinder unbinder;

    @BindView(R.id.recycler_discount)
    RecyclerView recycler_discount;
    LayoutAnimationController layoutAnimationController;
    MyDiscountAdapter adapter;
    List<DiscountModel> discountModelList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        mViewModel = new ViewModelProvider(this).get(DiscountViewModel.class);
        View root = inflater.inflate(R.layout.fragment_discount, container, false);
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
        mViewModel.getDiscountMutableLiveData().observe(getViewLifecycleOwner(), list -> {
            if (list == null)
                discountModelList = new ArrayList<>();
            else
                discountModelList = list;
            adapter = new MyDiscountAdapter(getContext(), discountModelList);
            recycler_discount.setAdapter(adapter);
            recycler_discount.setLayoutAnimation(layoutAnimationController);
            loading_dialog.dismiss();

        });

        return root;

    }

    private void initView() {
        setHasOptionsMenu(true);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_discount.setLayoutManager(layoutManager);
        recycler_discount.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        MySwipeHelper swipeHelper = new MySwipeHelper(getContext(), recycler_discount, width/7) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "fasakh", 30, 0, Color.parseColor("#333639"),
                        pos -> {

                            Common.discountSelected = discountModelList.get(pos);
                            showDeleteDialog();


                        }));

                buf.add(new MyButton(getContext(), "baddel", 30, 0, Color.parseColor("#414243"),
                        pos -> {

                            Common.discountSelected = discountModelList.get(pos);
                            showUpdateDialog();


                        }));
            }
        };
    }


    private void showAddDialog() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar selectedDate = Calendar.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("A3mel code");
        builder.setMessage("3amrelna hedhy");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_discount, null);

        EditText edt_code = (EditText) itemView.findViewById(R.id.edt_code);
        EditText edt_percent = (EditText) itemView.findViewById(R.id.edt_percent);
        EditText edt_valid = (EditText) itemView.findViewById(R.id.edt_valid);
        ImageView img_calendar = (ImageView) itemView.findViewById(R.id.pickDate);


        DatePickerDialog.OnDateSetListener listener = (view, year, month, day) -> {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, day);
            edt_valid.setText(simpleDateFormat.format(selectedDate.getTime()));


        };
        img_calendar.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), listener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                    .show();


        });


        builder.setNegativeButton("BATELT", ((dialogInterface, i) -> dialogInterface.dismiss()))
                .setPositiveButton("MRYGL", ((dialogInterface, i) -> {

                    DiscountModel discountModel = new DiscountModel();
                    discountModel.setKey(edt_code.getText().toString());
                    discountModel.setPercent(Integer.parseInt(edt_percent.getText().toString()));
                    discountModel.setUntilDate(selectedDate.getTimeInMillis());

                    createDiscount(discountModel);

                }));
        builder.setView(itemView);
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void createDiscount(DiscountModel discountModel) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.DISCOUNT_REF)
                .child(discountModel.getKey())
                .setValue(discountModel)
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        mViewModel.loadDiscount();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "hawka saye", Toast.LENGTH_SHORT).show();
                    }


                });


    }

    private void showUpdateDialog() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Calendar selectedDate = Calendar.getInstance();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Baddel");
                builder.setMessage("3amrelna hedhy");

                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_discount, null);

                EditText edt_code = (EditText) itemView.findViewById(R.id.edt_code);
                EditText edt_percent = (EditText) itemView.findViewById(R.id.edt_percent);
                EditText edt_valid = (EditText) itemView.findViewById(R.id.edt_valid);
                ImageView img_calendar = (ImageView) itemView.findViewById(R.id.pickDate);

                edt_code.setText(Common.discountSelected.getKey());
                edt_code.setEnabled(false);

                edt_percent.setText(new StringBuilder().append(Common.discountSelected.getPercent()));
                edt_valid.setText(simpleDateFormat.format(Common.discountSelected.getUntilDate()));

                DatePickerDialog.OnDateSetListener listener = (view, year, month, day) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, day);
                    edt_valid.setText(simpleDateFormat.format(selectedDate.getTime()));


                };
                img_calendar.setOnClickListener(view -> {
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(getContext(), listener, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH))
                            .show();


                });


                builder.setNegativeButton("BATELT", ((dialogInterface, i) -> dialogInterface.dismiss()))
                        .setPositiveButton("BADDEL", ((dialogInterface, i) -> {
                            Map<String,Object> update_data = new HashMap<>();
                            update_data.put("percent",Integer.parseInt(edt_percent.getText().toString()));
                            update_data.put("untilDate",selectedDate.getTimeInMillis());

                            updateDiscount(update_data);

                        }));
                builder.setView(itemView);
                AlertDialog dialog = builder.create();
                dialog.show();


            }

    private void updateDiscount(Map<String, Object> update_data) {
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child(Common.DISCOUNT_REF)
                        .child(Common.discountSelected.getKey())
                        .updateChildren(update_data)
                        .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                mViewModel.loadDiscount();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "hawka saye badelneh", Toast.LENGTH_SHORT).show();
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
                        .child(Common.DISCOUNT_REF)
                        .child(Common.discountSelected.getKey())
                        .removeValue()
                        .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                mViewModel.loadDiscount();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "awka saye fasakhneh", Toast.LENGTH_SHORT).show();
                            }


                        });


            }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.discount_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_create)
            showAddDialog();
        return super.onOptionsItemSelected(item);
    }
}

    
    
    
    
    
    


