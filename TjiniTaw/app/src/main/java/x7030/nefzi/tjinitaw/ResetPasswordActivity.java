package x7030.nefzi.tjinitaw;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Model.UserModel;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText edtKey,edtPhone,edtPassword;
    Button btnResetPassword,btnSavePassword;
    LinearLayout passwordOption;
    ImageView showResetKey,showPassword;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        View loading_layout = LayoutInflater.from(this)
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        edtKey =  (EditText) findViewById(R.id.edtResetKey);
        edtPhone =  (EditText) findViewById(R.id.edtPhone);
        edtPassword =  (EditText) findViewById(R.id.edtPassword);
        btnResetPassword = (Button) findViewById(R.id.btnResetPassword);
        btnSavePassword = (Button) findViewById(R.id.btnSavePassword);
        passwordOption = (LinearLayout) findViewById(R.id.option);
        showResetKey = (ImageView) findViewById(R.id.show_reset_key);
        showPassword = (ImageView) findViewById(R.id.show_password);

        showResetKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtKey.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD))
                {
                    edtKey.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    edtKey.setSelection(edtKey.length());
                    showResetKey.setImageResource(R.drawable.hide);
                }
                else
                {
                    edtKey.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    edtKey.setSelection(edtKey.length());
                    showResetKey.setImageResource(R.drawable.show);
                }
                edtKey.setTypeface(Typeface.DEFAULT);
            }
        });



        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD))
                {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    edtPassword.setSelection(edtPassword.length());
                    showPassword.setImageResource(R.drawable.hide);
                }
                else
                {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtPassword.setSelection(edtPassword.length());
                    showPassword.setImageResource(R.drawable.show);
                }
                edtPassword.setTypeface(Typeface.DEFAULT);
            }
        });



        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference(Common.USER_REFERENCESER);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading_dialog.show();
                if (checkinputs(edtPhone,edtKey)){
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange( DataSnapshot dataSnapshot) {


                            if(dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                loading_dialog.dismiss();

                                Common.currentUser = dataSnapshot.child(edtPhone.getText().toString()).getValue(UserModel.class);


                                if (Common.currentUser.getResetKey().equals(edtKey.getText().toString())) {

                                    btnResetPassword.setVisibility(View.GONE);
                                    passwordOption.setVisibility(View.VISIBLE);
                                    btnSavePassword.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (edtPassword != null && edtPassword.getText().length() > 3)
                                            {
                                                String password = edtPassword.getText().toString();
                                                updatePassword(password);
                                                Intent SignIn = new Intent(ResetPasswordActivity.this,SignIn.class);
                                                startActivity(SignIn);

                                            }
                                            else
                                            {
                                                edtPassword.setText("");
                                                edtPassword.setHintTextColor(Color.RED);
                                            }



                                        }

                                        private void updatePassword(String password) {
                                            Map<String,Object> updateData = new HashMap<>();
                                            updateData.put("password",password);

                                            FirebaseDatabase.getInstance()
                                                    .getReference(Common.USER_REFERENCESER)
                                                    .child(Common.currentUser.getPhone())
                                                    .updateChildren(updateData)
                                                    .addOnFailureListener(e -> Toast.makeText(ResetPasswordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                                                    .addOnSuccessListener(aVoid -> {

                                                    });
                                        }
                                    });




                                } else {
                                    edtKey.setText("");
                                    edtKey.setHintTextColor(Color.RED);

                                }
                            }
                            else
                            {
                                loading_dialog.dismiss();
                                edtPhone.setText("");
                                edtPhone.setHintTextColor(Color.RED);
                                edtKey.setText("");



                            }



                        }

                        @Override
                        public void onCancelled( DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    loading_dialog.dismiss();
                    Toast.makeText(ResetPasswordActivity.this, "Thabet mli7", Toast.LENGTH_SHORT).show();


                }
            }

            private boolean checkinputs(EditText edtPhone, EditText edtKey) {
                boolean flag = true;
                if (edtPhone == null || edtPhone.getText().length() !=8)
                    flag=false;
                if (edtKey == null || edtKey.getText().length() != 6)
                    flag=false;

                return  flag;
            }
        });




    }
}