package x7030.nefzi.tjinitaw.ui.Restaurant;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import x7030.nefzi.tjinitaw.Callback.IRestaurantCallbackListener;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Model.RestaurantModel;

public class RestaurantViewModel extends ViewModel implements IRestaurantCallbackListener {
    private MutableLiveData<List<RestaurantModel>> restaurantListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IRestaurantCallbackListener listener;


    public RestaurantViewModel() {
        listener = this;
    }

    public MutableLiveData<List<RestaurantModel>> getRestaurantListMutable() {
        if (restaurantListMutable == null)
        {
            restaurantListMutable = new MutableLiveData<>();
            loadRestaurantFromFirebase();
        }
        return restaurantListMutable;
    }

    private void loadRestaurantFromFirebase() {
        List<RestaurantModel> restaurantModels = new ArrayList<>();
        DatabaseReference restaurentRef = FirebaseDatabase.getInstance()
                .getReference(Common.RESTAURANT_REF);
        restaurentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot restaurantSnapShot : dataSnapshot.getChildren())
                    {
                        RestaurantModel restaurantModel = restaurantSnapShot.getValue(RestaurantModel.class);
                        restaurantModel.setUid(restaurantSnapShot.getKey());
                        restaurantModels.add(restaurantModel);


                    }
                    if (restaurantModels.size() > 0)
                        listener.OnRestaurantLoadSuccess(restaurantModels);
                    else
                        listener.OnRestaurantLoadFailed("Restaurant list empty");

                }
                else
                    listener.OnRestaurantLoadFailed("Restaurant List Doesn't exists!");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void OnRestaurantLoadSuccess(List<RestaurantModel> restaurantModelList) {
        restaurantListMutable.setValue(restaurantModelList);

    }

    @Override
    public void OnRestaurantLoadFailed(String message) {
        messageError.setValue(message);

    }
}