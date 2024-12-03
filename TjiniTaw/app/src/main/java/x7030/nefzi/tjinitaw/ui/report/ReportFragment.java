package x7030.nefzi.tjinitaw.ui.report;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.EventBus.PlaceOrderClicked;
import x7030.nefzi.tjinitaw.EventBus.ReportClicked;
import x7030.nefzi.tjinitaw.MainActivity;
import x7030.nefzi.tjinitaw.R;

public class ReportFragment extends Fragment {

    private ReportViewModel mViewModel;
    Unbinder unbinder;
    @BindView(R.id.edt_report)
    EditText edt_report;
    @BindView(R.id.edt_program)
    EditText edt_program;
    @BindView(R.id.btnleave)
    Button btnleave;
    String notif = "";






    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        View root = inflater.inflate(R.layout.fragment_report, container, false);
        unbinder = ButterKnife.bind(this,root);
        initViews();
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builderLoad = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builderLoad.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);


        btnleave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading_dialog.show();
                notif +="User : "+Common.currentUser.getName()+"%0A";
                notif +="Phone : "+Common.currentUser.getPhone()+"%0A";
                notif +="Topic : "+edt_report.getText()+"%0A";
                notif +="Pragrammeur : "+edt_program.getText()+"%0A";
                sendNotification(notif);
            }

            private void sendNotification(String msg) {
                String urlfin = "https://api.telegram.org/bot5681889281:AAEdJX342l9sNybAypQf3PN11pOAAb2Rokk/sendMessage?chat_id=-1001646604884&text=";
                urlfin +=msg;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(urlfin)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String myResponse = response.body().string();
                            loading_dialog.dismiss();
                            EventBus.getDefault().postSticky(new ReportClicked(true));

                        }
                    }
                });
            }
        });
        return root;


    }

    private void initViews() {
        notif = "!!!!!!!!!!!!!!!!!Repport!!!!!!!!!!!!!!!%0A";
    }

    @Override
    public void onStart() {
        EventBus.getDefault().removeAllStickyEvents();
        super.onStart();
    }
}