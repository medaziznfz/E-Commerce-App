package x7030.nefzi.tjinitaw.ui.settings;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.paperdb.Paper;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.MainActivity;
import x7030.nefzi.tjinitaw.R;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;
    Unbinder unbinder;

    @BindView(R.id.change_name_option)
    CardView change_name_option;
    @BindView(R.id.change_address_option)
    CardView change_address_option;
    @BindView(R.id.change_password_option)
    CardView change_password_option;
    @BindView(R.id.change_name_input)
    CardView change_name_input;
    @BindView(R.id.change_address_input)
    CardView change_address_input;
    @BindView(R.id.change_password_input)
    CardView change_password_input;
    @BindView(R.id.txt_user_name)
    TextView txt_user_name;
    @BindView(R.id.txt_user_address)
    TextView txt_user_address;
    @BindView(R.id.edt_user_name)
    EditText edt_user_name;
    @BindView(R.id.edt_user_address)
    EditText edt_user_address;
    @BindView(R.id.edt_prev_password)
    EditText edt_prev_password;
    @BindView(R.id.edt_new_password)
    EditText edt_new_password;
    @BindView(R.id.save_name)
    Button save_name;
    @BindView(R.id.save_address)
    Button save_address;
    @BindView(R.id.save_password)
    Button save_password;
    @BindView(R.id.position_name)
    ImageView position_name;
    @BindView(R.id.position_address)
    ImageView position_address;
    @BindView(R.id.position_password)
    ImageView position_password;
    @BindView(R.id.position_reset_key)
    ImageView position_reset_key;
    @BindView(R.id.edt_prev_reset_key)
    EditText edt_prev_reset_key;
    @BindView(R.id.edt_new_reset_key)
    EditText edt_new_reset_key;
    @BindView(R.id.change_reset_key_option)
    CardView change_reset_key_option;
    @BindView(R.id.change_reset_key_input)
    CardView change_reset_key_input;
    @BindView(R.id.save_reset_key)
    Button save_reset_key;
    @BindView(R.id.show_prev_password)
    ImageView show_prev_password;
    @BindView(R.id.show_new_password)
    ImageView show_new_password;
    @BindView(R.id.show_prev_reset_key)
    ImageView show_prev_reset_key;
    @BindView(R.id.show_new_reset_key)
    ImageView show_new_reset_key;






    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this,root);
        initViews();







        show_new_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_new_password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD))
                {
                    edt_new_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    edt_new_password.setSelection(edt_new_password.length());
                    show_new_password.setImageResource(R.drawable.hide);
                }
                else
                {
                    edt_new_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edt_new_password.setSelection(edt_new_password.length());
                    show_new_password.setImageResource(R.drawable.show);
                }
                edt_new_password.setTypeface(Typeface.DEFAULT);
            }
        });

        show_prev_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_prev_password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD))
                {
                    edt_prev_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    edt_prev_password.setSelection(edt_prev_password.length());
                    show_prev_password.setImageResource(R.drawable.hide);
                }
                else
                {
                    edt_prev_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edt_prev_password.setSelection(edt_prev_password.length());
                    show_prev_password.setImageResource(R.drawable.show);
                }
                edt_prev_password.setTypeface(Typeface.DEFAULT);
            }
        });

        show_prev_reset_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_prev_reset_key.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD))
                {
                    edt_prev_reset_key.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    edt_prev_reset_key.setSelection(edt_prev_reset_key.length());
                    show_prev_reset_key.setImageResource(R.drawable.hide);
                }
                else
                {
                    edt_prev_reset_key.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    edt_prev_reset_key.setSelection(edt_prev_reset_key.length());
                    show_prev_reset_key.setImageResource(R.drawable.show);
                }
                edt_prev_reset_key.setTypeface(Typeface.DEFAULT);
            }
        });

        show_new_reset_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_new_reset_key.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD))
                {
                    edt_new_reset_key.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    edt_new_reset_key.setSelection(edt_new_reset_key.length());
                    show_new_reset_key.setImageResource(R.drawable.hide);
                }
                else
                {
                    edt_new_reset_key.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    edt_new_reset_key.setSelection(edt_new_reset_key.length());
                    show_new_reset_key.setImageResource(R.drawable.show);
                }
                edt_new_reset_key.setTypeface(Typeface.DEFAULT);
            }
        });



        change_name_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (change_name_input.getVisibility() == View.GONE)
                {
                    change_name_input.setVisibility(View.VISIBLE);
                    position_name.setImageResource(R.drawable.up);
                }
                else
                {
                    change_name_input.setVisibility(View.GONE);
                    position_name.setImageResource(R.drawable.down);
                }
            }
        });
        change_address_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (change_address_input.getVisibility() == View.GONE)
                {
                    change_address_input.setVisibility(View.VISIBLE);
                    position_address.setImageResource(R.drawable.up);
                }
                else
                {
                    change_address_input.setVisibility(View.GONE);
                    position_address.setImageResource(R.drawable.down);
                }
            }
        });
        change_password_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (change_password_input.getVisibility() == View.GONE)
                {
                    change_password_input.setVisibility(View.VISIBLE);
                    position_password.setImageResource(R.drawable.up);
                }
                else
                {
                    change_password_input.setVisibility(View.GONE);
                    position_password.setImageResource(R.drawable.down);
                }
            }
        });
        change_reset_key_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (change_reset_key_input.getVisibility() == View.GONE)
                {
                    change_reset_key_input.setVisibility(View.VISIBLE);
                    position_reset_key.setImageResource(R.drawable.up);
                }
                else
                {
                    change_reset_key_input.setVisibility(View.GONE);
                    position_reset_key.setImageResource(R.drawable.down);
                }
            }
        });
        save_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_user_name.getText() != null && edt_user_name.getText().length() > 3)
                {
                    String name = edt_user_name.getText().toString();
                    updateName(name);

                }
                else
                {
                    Toast.makeText(getContext(), "Thabet mli7", Toast.LENGTH_SHORT).show();
                }
            }

            private void updateName(String name) {
                Map<String,Object> updateData = new HashMap<>();
                updateData.put("name",name);

                FirebaseDatabase.getInstance()
                        .getReference(Common.USER_REFERENCESER)
                        .child(Common.currentUser.getPhone())
                        .updateChildren(updateData)
                        .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnSuccessListener(aVoid -> {
                            Common.currentUser.setName(name);

                        });
            }
        });
        save_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_user_address.getText() != null && edt_user_address.getText().length() > 3)
                {
                    String address = edt_user_address.getText().toString();
                    updateAddress(address);

                }
                else
                {
                    Toast.makeText(getContext(), "Thabt mli7", Toast.LENGTH_SHORT).show();
                }
            }

            private void updateAddress(String address) {
                Map<String,Object> updateData = new HashMap<>();
                updateData.put("address",address);

                FirebaseDatabase.getInstance()
                        .getReference(Common.USER_REFERENCESER)
                        .child(Common.currentUser.getPhone())
                        .updateChildren(updateData)
                        .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnSuccessListener(aVoid -> {
                            Common.currentUser.setAddress(address);

                        });
            }
        });
        save_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((edt_prev_password.getText() != null && edt_prev_password.getText().length() > 3)
                        &&(edt_new_password.getText() != null && edt_new_password.getText().length() > 3))
                {
                    if (edt_prev_password.getText().toString().equals(Common.currentUser.getPassword()))
                    {
                        String password = edt_new_password.getText().toString();
                        updatePassword(password);
                        Paper.book().destroy();
                        startActivity(new Intent(getContext(), MainActivity.class));

                    }
                    else
                    {
                        Toast.makeText(getContext(), "Motdpassek el qdim ghalet", Toast.LENGTH_SHORT).show();
                    }



                }
                else
                {
                    Toast.makeText(getContext(), "Thabet mil7", Toast.LENGTH_SHORT).show();

                }



            }

            private void updatePassword(String password) {
                Map<String,Object> updateData = new HashMap<>();
                updateData.put("password",password);

                FirebaseDatabase.getInstance()
                        .getReference(Common.USER_REFERENCESER)
                        .child(Common.currentUser.getPhone())
                        .updateChildren(updateData)
                        .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnSuccessListener(aVoid -> {

                        });


            }
        });
        save_reset_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((edt_prev_reset_key.getText() != null && edt_prev_reset_key.getText().length() == 6)
                        &&(edt_new_reset_key.getText() != null && edt_new_reset_key.getText().length() == 6))
                {
                    if (edt_prev_reset_key.getText().toString().equals(Common.currentUser.getResetKey()))
                    {
                        String key = edt_new_reset_key.getText().toString();
                        updateKey(key);

                    }
                    else
                    {
                        Toast.makeText(getContext(), "El key el qdim mte3ek ghalet", Toast.LENGTH_SHORT).show();
                    }



                }
                else
                {
                    Toast.makeText(getContext(), "Thabet mli7", Toast.LENGTH_SHORT).show();

                }



            }

            private void updateKey(String key) {
                Map<String,Object> updateData = new HashMap<>();
                updateData.put("resetKey",key);

                FirebaseDatabase.getInstance()
                        .getReference(Common.USER_REFERENCESER)
                        .child(Common.currentUser.getPhone())
                        .updateChildren(updateData)
                        .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnSuccessListener(aVoid -> {
                            Common.currentUser.setResetKey(key);


                        });
            }


        });




        return root;


    }

    private void initViews() {
        txt_user_name.setText(Common.currentUser.getName());
        txt_user_address.setText(Common.currentUser.getAddress());
    }

    @Override
    public void onStart() {
        EventBus.getDefault().removeAllStickyEvents();
        super.onStart();
    }
}