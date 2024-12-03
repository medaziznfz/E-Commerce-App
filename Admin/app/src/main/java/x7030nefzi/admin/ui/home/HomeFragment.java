package x7030nefzi.admin.ui.home;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030nefzi.admin.Common.Common;
import x7030nefzi.admin.Model.DiscountModel;
import x7030nefzi.admin.Model.ShipperModel;
import x7030nefzi.admin.R;
import x7030nefzi.admin.databinding.FragmentHomeBinding;

import static x7030nefzi.admin.Common.Common.formatPrice;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    Unbinder unbinder ;
    @BindView(R.id.txt_user_number)
    TextView txt_user_number;
    @BindView(R.id.txt_user_fees)
    TextView txt_user_fees;
    @BindView(R.id.txt_task_number)
    TextView txt_task_number;
    private int totalSize;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home,container,false);


        unbinder = ButterKnife.bind(this,root);
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        totalSize = (int) snapshot.getChildrenCount();
                        txt_user_number.setText(String.valueOf(totalSize)+" Users !");
                        loading_dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance()
                .getReference("Tasks")
                .orderByChild("orderStatus")
                .equalTo(0)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        totalSize = (int) snapshot.getChildrenCount();
                        txt_task_number.setText(String.valueOf(totalSize)+" Taches !");
                        loading_dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance()
                .getReference(Common.SHIPPERS_REF)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.getChildren().iterator().hasNext())
                        {
                            double totale = 0;
                            for (DataSnapshot feesSnapShot:snapshot.getChildren())
                            {
                                ShipperModel shipperModel = feesSnapShot.getValue(ShipperModel.class);
                                totale += shipperModel.getFees();

                            }
                            txt_user_fees.setText(formatPrice(totale)+" tnd");
                            loading_dialog.dismiss();
                        }
                        else
                            Toast.makeText(getContext(), "Mafama hata livreur !!", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}