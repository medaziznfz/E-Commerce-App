package x7030.nefzi.tjinitaw.ui.contact;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Model.ContactModel;
import x7030.nefzi.tjinitaw.R;

public class ContactFragment extends Fragment {

    private ContactViewModel mViewModel;
    Unbinder unbinder;

    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.instagram)
    TextView instagram;
    @BindView(R.id.facebook)
    TextView facebook;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.baseIn)
    LinearLayout baseIn;
    @BindView(R.id.image_admin)
    ShapeableImageView imageAdmin;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        View root = inflater.inflate(R.layout.fragment_contact, container, false);
        unbinder = ButterKnife.bind(this,root);


        loadContactInfo();


        return root;


    }

    private void loadContactInfo() {
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();
        FirebaseDatabase.getInstance()
                .getReference(Common.CONTACT_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ContactModel contactModel = dataSnapshot.getValue(ContactModel.class);
                        baseIn.setVisibility(View.VISIBLE);
                        email.setText(contactModel.getEmail());
                        phone.setText(contactModel.getPhone());
                        facebook.setText(contactModel.getFacebook());
                        instagram.setText(contactModel.getInstagram());
                        address.setText(contactModel.getAddress());
                        Glide.with(getContext())
                                .load(contactModel.getImage())
                                .placeholder(R.drawable.load)
                                .into(imageAdmin);
                        loading_dialog.dismiss();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed To Load Information", Toast.LENGTH_SHORT).show();

                    }
                });
    }


}