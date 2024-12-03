package x7030.nefzi.tjinitaw.ui.home;

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

import x7030.nefzi.tjinitaw.Callback.IPlatsCallbackListener;
import x7030.nefzi.tjinitaw.Callback.IOffersCallbackListener;
import x7030.nefzi.tjinitaw.Callback.IOthersCallbackListener;
import x7030.nefzi.tjinitaw.Callback.IRestaurantPubCallbackListener;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Model.OffresModel;
import x7030.nefzi.tjinitaw.Model.OthersModel;
import x7030.nefzi.tjinitaw.Model.PlatsModel;
import x7030.nefzi.tjinitaw.Model.RestaurantModel;

public class HomeViewModel extends ViewModel implements IOthersCallbackListener, IPlatsCallbackListener, IOffersCallbackListener,IRestaurantPubCallbackListener {
    private MutableLiveData<List<OthersModel>> popularList;
    private MutableLiveData<List<PlatsModel>> platsList;
    private MutableLiveData<List<OffresModel>> offres;
    private MutableLiveData<List<RestaurantModel>> restaurantList;
    private MutableLiveData<String> messageError;
    private IOthersCallbackListener othersCallbackListener;
    private IPlatsCallbackListener platsCallbackListener;
    private IOffersCallbackListener offersCallbackListener;
    private IRestaurantPubCallbackListener restaurantPubCallbackListener;


    public HomeViewModel() {
        othersCallbackListener = this;
        platsCallbackListener = this;
        offersCallbackListener = this;
        restaurantPubCallbackListener = this;
    }

    public MutableLiveData<List<PlatsModel>> getPlatsList() {
        if(platsList == null)
        {
            platsList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadPlatsList();

        }


        return platsList;
    }

    public MutableLiveData<List<OffresModel>> getOffresShop() {
        if(offres == null)
        {
            offres = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadOffres();

        }


        return offres;
    }


    public MutableLiveData<List<RestaurantModel>> getRestaurantList() {
        if (restaurantList == null) {
            restaurantList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadRestaurantList();
        }
        return restaurantList;

    }

    private void loadRestaurantList() {
        List<RestaurantModel> tempList = new ArrayList<>();
        DatabaseReference popularRef = FirebaseDatabase.getInstance()
                .getReference(Common.RESTAURANT_REF);
        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                {
                    RestaurantModel model = itemSnapShot.getValue(RestaurantModel.class);
                    model.setUid(itemSnapShot.getKey());
                    tempList.add(model);

                }
                restaurantPubCallbackListener.OnRestaurantPubLoadSuccess(tempList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                restaurantPubCallbackListener.OnRestaurantPubLoadFailed(databaseError.getMessage());

            }
        });
    }
    private void loadPlatsList() {
        List<PlatsModel> tempList = new ArrayList<>();
        DatabaseReference BestdealRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.PLATS_JOUR_REF);
        BestdealRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                {
                    PlatsModel model = itemSnapShot.getValue(PlatsModel.class);
                    tempList.add(model);

                }
                platsCallbackListener.OnPlatsLoadSuccess(tempList);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                platsCallbackListener.OnPlatsLoadFailed(databaseError.getMessage());

            }
        });



    }

    private void loadOffres() {
        List<OffresModel> tempList = new ArrayList<>();
        DatabaseReference OffersRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.OFFERS_PUB_REF);
        OffersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                {
                    OffresModel model = itemSnapShot.getValue(OffresModel.class);
                    tempList.add(model);

                }
                offersCallbackListener.OnOffersLoadSuccess(tempList);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                offersCallbackListener.OnOffersLoadFailed(databaseError.getMessage());

            }
        });



    }

    public MutableLiveData<List<OthersModel>> getOthersList() {
        if (popularList == null) {
            popularList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadOthersList();
        }
        return popularList;

    }

    private void loadOthersList() {
        List<OthersModel> tempList = new ArrayList<>();
        DatabaseReference popularRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.OTHERS_REF);
        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                {
                    OthersModel model = itemSnapShot.getValue(OthersModel.class);
                    tempList.add(model);

                }
                othersCallbackListener.OnOthersLoadSuccess(tempList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                othersCallbackListener.OnOthersLoadFailed(databaseError.getMessage());

            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void OnOthersLoadSuccess(List<OthersModel> popular_categoryModels) {
        popularList.setValue(popular_categoryModels);
    }

    @Override
    public void OnOthersLoadFailed(String message) {
        messageError.setValue(message);

    }

    @Override
    public void OnPlatsLoadSuccess(List<PlatsModel> platsModels) {
        platsList.setValue(platsModels);

    }

    @Override
    public void OnPlatsLoadFailed(String message) {
        messageError.setValue(message);

    }


    @Override
    public void OnOffersLoadSuccess(List<OffresModel> offresModels) {
        offres.setValue(offresModels);
    }

    @Override
    public void OnOffersLoadFailed(String message) {
        messageError.setValue(message);
    }

    @Override
    public void OnRestaurantPubLoadSuccess(List<RestaurantModel> restaurantModels) {
        restaurantList.setValue(restaurantModels);
    }

    @Override
    public void OnRestaurantPubLoadFailed(String message) {
        messageError.setValue(message);
    }
}