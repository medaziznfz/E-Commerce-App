package x7030.nefzi.tjinitawpanel.ui.home;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
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
import x7030.nefzi.tjinitawpanel.Common.Common;
import x7030.nefzi.tjinitawpanel.MainActivity;
import x7030.nefzi.tjinitawpanel.R;
import x7030.nefzi.tjinitawpanel.databinding.FragmentHomeBinding;

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
    private double fees;
    private Handler handler;
    private Runnable runnable;

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
                        Common.totalCommande = totalSize;
                        txt_task_number.setText(String.valueOf(totalSize)+" Taches !");
                        loading_dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance()
                .getReference(Common.USER_REFERENCESER)
                .child(Common.currentShipper.getPhone())
                .child("fees")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        fees = (double) snapshot.getValue(double.class);
                        txt_user_fees.setText(String.valueOf(fees)+" tnd");
                        loading_dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        /*
        bousa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int reqCode = 1;
                Intent intent = new Intent(getActivity(), MainActivity.class);
                showNotification(getContext(), "Title", "This is the message to display", intent, reqCode);

            }
        });

         */
// Create the Handler
        handler = new Handler();

// Define the code block to be executed
       runnable = new Runnable() {
            @Override
            public void run() {
                // Insert custom code here
                checkCommande();
                // Repeat every 2 seconds
                handler.postDelayed(runnable, 300000);
            }

           private void checkCommande() {
               FirebaseDatabase.getInstance()
                       .getReference("Tasks")
                       .orderByChild("orderStatus")
                       .equalTo(0)
                       .addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                               totalSize = (int) snapshot.getChildrenCount();
                               Common.totalCommande = totalSize;
                               if (Common.totalCommande > 0)
                               {
                                   int reqCode = 1;
                                   Intent intent = new Intent(getActivity(), MainActivity.class);
                                   showNotification(getContext(), "Taches Disponible", "Restant : "+Common.totalCommande, intent, reqCode);
                               }

                           }

                           @Override
                           public void onCancelled(@NonNull @NotNull DatabaseError error) {

                           }
                       });
           }
       };

// Start the Runnable immediately
        handler.post(runnable);




        return root;
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        //SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(context);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "nefzi";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Med Aziz Nefzi";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}