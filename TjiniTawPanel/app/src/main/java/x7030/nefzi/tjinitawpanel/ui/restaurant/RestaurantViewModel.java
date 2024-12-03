package x7030.nefzi.tjinitawpanel.ui.restaurant;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import x7030.nefzi.tjinitawpanel.Callback.IRestaurantLoadCallbackListener;
import x7030.nefzi.tjinitawpanel.Common.Common;
import x7030.nefzi.tjinitawpanel.Model.RestaurantModel;

public class RestaurantViewModel extends ViewModel implements IRestaurantLoadCallbackListener {

    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private MutableLiveData<List<RestaurantModel>> restaurantMutableList;
    private IRestaurantLoadCallbackListener restaurantLoadCallbackListener;

    public RestaurantViewModel() {
        restaurantLoadCallbackListener = this;
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    public MutableLiveData<List<RestaurantModel>> getRestaurantMutableList() {
        if (restaurantMutableList == null)
        {
            restaurantMutableList = new MutableLiveData<>();
            loadRestaurant();

        }
        return restaurantMutableList;
    }

    private void loadRestaurant() {
        List<RestaurantModel> templist = new ArrayList<>();
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference()
                .child(Common.RESTAURANT_REF);
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot restaurantSnapShot:dataSnapshot.getChildren())
                {
                    RestaurantModel restaurantModel = restaurantSnapShot.getValue(RestaurantModel.class);
                    restaurantModel.setUid(restaurantSnapShot.getKey());
                    templist.add(restaurantModel);


                }
                restaurantLoadCallbackListener.onRestaurantLoadSuccess(templist);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                restaurantLoadCallbackListener.onRestaurantLoadFailed(databaseError.getMessage());

            }
        });

    }

    @Override
    public void onRestaurantLoadSuccess(List<RestaurantModel> restaurantModelList) {
        if (restaurantMutableList != null)
            restaurantMutableList.setValue(restaurantModelList);

    }


    @Override
    public void onRestaurantLoadFailed(String message) {
        messageError.setValue(message);

    }
}