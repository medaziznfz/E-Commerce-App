package x7030.nefzi.tjinitawpanel;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;
import x7030.nefzi.tjinitawpanel.Common.Common;
import x7030.nefzi.tjinitawpanel.Model.ShipperModel;

public class SignIn extends AppCompatActivity {
    EditText edtPhone,edtPassword;
    Button btnSignIn;
    CheckBox ckbRemember;
    ImageView showPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        View loading_layout = LayoutInflater.from(this)
                .inflate(R.layout.loading_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        ckbRemember = (CheckBox) findViewById(R.id.remember_me);
        showPassword = (ImageView) findViewById(R.id.show_password);

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    edtPassword.setSelection(edtPassword.length());
                    showPassword.setImageResource(R.drawable.hide);
                } else {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtPassword.setSelection(edtPassword.length());
                    showPassword.setImageResource(R.drawable.show);
                }
                edtPassword.setTypeface(Typeface.DEFAULT);
            }
        });


        Paper.init(this);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference(Common.USER_REFERENCESER);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading_dialog.show();
                if (checkinputs(edtPhone, edtPassword)) {
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                loading_dialog.dismiss();

                                Common.currentShipper = dataSnapshot.child(edtPhone.getText().toString()).getValue(ShipperModel.class);


                                if (Common.currentShipper.getPassword().equals(edtPassword.getText().toString())) {

                                    if (ckbRemember.isChecked()) {
                                        Paper.book().write(Common.SHIPPER_KEY, edtPhone.getText().toString());
                                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());

                                    }


                                    Intent Home = new Intent(SignIn.this, HomeActivity.class);
                                    startActivity(Home);


                                } else {
                                    edtPassword.setText("");
                                    edtPassword.setHintTextColor(Color.RED);

                                }
                            } else {
                                loading_dialog.dismiss();
                                edtPhone.setText("");
                                edtPhone.setHintTextColor(Color.RED);
                                edtPassword.setText("");


                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    loading_dialog.dismiss();
                    Toast.makeText(SignIn.this, "Invalid Information", Toast.LENGTH_SHORT).show();


                }
            }

            private boolean checkinputs(EditText edtPhone, EditText edtPassword) {
                boolean flag = true;
                if (edtPhone == null || edtPhone.getText().length() != 8)
                    flag = false;
                if (edtPassword == null || edtPassword.getText().length() < 4)
                    flag = false;

                return flag;
            }
        });


    }


}