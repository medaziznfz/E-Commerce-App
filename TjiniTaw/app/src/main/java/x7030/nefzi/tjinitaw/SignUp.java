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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Model.UserModel;

public class SignUp extends AppCompatActivity {

    EditText edtPhone,edtPassword,edtName,edtAddress,edtResetKey;
    Button btnSignUp;
    ImageView show_password,show_reset_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        View loading_layout = LayoutInflater.from(this)
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        edtPhone =  (EditText) findViewById(R.id.edtPhone);
        edtName =  (EditText) findViewById(R.id.edtName);
        edtPassword =  (EditText) findViewById(R.id.edtPassword);
        edtResetKey =  (EditText) findViewById(R.id.edt_reset_key);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        show_password = (ImageView) findViewById(R.id.show_password);
        show_reset_key = (ImageView) findViewById(R.id.show_reset_key);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference(Common.USER_REFERENCESER);


        show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD))
                {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    edtPassword.setSelection(edtPassword.length());
                    show_password.setImageResource(R.drawable.hide);
                }
                else
                {
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtPassword.setSelection(edtPassword.length());
                    show_password.setImageResource(R.drawable.show);
                }
                edtPassword.setTypeface(Typeface.DEFAULT);
            }
        });



        show_reset_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtResetKey.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD))
                {
                    edtResetKey.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    edtResetKey.setSelection(edtResetKey.length());
                    show_reset_key.setImageResource(R.drawable.hide);
                }
                else
                {
                    edtResetKey.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    edtResetKey.setSelection(edtResetKey.length());
                    show_reset_key.setImageResource(R.drawable.show);
                }
                edtResetKey.setTypeface(Typeface.DEFAULT);
            }
        });





        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading_dialog.show();

                if (checkinputs(edtPhone,edtPassword,edtName,edtAddress,edtResetKey))
                {
                    table_user.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(edtPhone.getText().toString()).exists())
                            {
                                loading_dialog.dismiss();
                                edtPhone.setText("");
                                edtPhone.setHintTextColor(Color.RED);

                            }
                            else
                            {
                                loading_dialog.dismiss();
                                UserModel user = new UserModel(edtName.getText().toString(), edtPassword.getText().toString(), edtPhone.getText().toString(), edtAddress.getText().toString(),edtResetKey.getText().toString());
                                table_user.child(edtPhone.getText().toString()).setValue(user);
                                login(edtPhone.getText().toString(),edtPassword.getText().toString());
                            }

                        }

                        private void login(String user, String pwd) {
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference table_user = database.getReference(Common.USER_REFERENCESER);

                            table_user.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange( DataSnapshot dataSnapshot) {


                                    if(dataSnapshot.child(user).exists()) {

                                        loading_dialog.dismiss();

                                        Common.currentUser = dataSnapshot.child(user).getValue(UserModel.class);

                                        if (Common.currentUser.getPassword().equals(pwd)) {
                                            Paper.book().write(Common.USER_KEY,user);
                                            Paper.book().write(Common.PWD_KEY,pwd);
                                            Intent Home = new Intent(SignUp.this,Home.class);
                                            startActivity(Home);




                                        } else {
                                            Paper.book().destroy();

                                        }
                                    }
                                    else
                                    {
                                        loading_dialog.dismiss();
                                        Paper.book().destroy();



                                    }



                                }

                                @Override
                                public void onCancelled( DatabaseError databaseError) {
                                    Toast.makeText(SignUp.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    loading_dialog.dismiss();
                    Toast.makeText(SignUp.this, "Thabet Mli7", Toast.LENGTH_SHORT).show();


                }
            }

            private boolean checkinputs(EditText edtPhone, EditText edtPassword, EditText edtName, EditText edtAddress,EditText edtResetKey) {
                boolean flag = true;
                if (edtPassword.getText() == null || edtPassword.getText().length() < 4)
                    flag = false;
                if (edtName.getText() == null || edtName.getText().length() < 4 )
                    flag = false;
                if (edtAddress.getText() == null || edtAddress.getText().length() < 4 )
                    flag = false;
                if (edtResetKey.getText() == null || edtResetKey.getText().length() != 6)
                    flag = false;

                return flag;
            }
        });



    }
}