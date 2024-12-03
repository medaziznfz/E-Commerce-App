package x7030nefzi.admin.ui.shippers;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
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

import x7030nefzi.admin.Callback.IShippersCallbackListener;
import x7030nefzi.admin.Common.Common;
import x7030nefzi.admin.Model.ShipperModel;

public class ShippersViewModel extends ViewModel implements IShippersCallbackListener {
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private MutableLiveData<List<ShipperModel>> shippersMutableLiveData;
    private IShippersCallbackListener shippersCallbackListener;

    public ShippersViewModel() {
        shippersCallbackListener = this;
    }

    public MutableLiveData<List<ShipperModel>> getShippersMutableLiveData() {
        if (shippersMutableLiveData == null) shippersMutableLiveData = new MutableLiveData<>();
        loadShippers();

        return shippersMutableLiveData;
    }

    public void loadShippers() {
        List<ShipperModel> temp = new ArrayList<>();
        DatabaseReference discountRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.SHIPPERS_REF);
        discountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getChildren().iterator().hasNext())
                {
                    for (DataSnapshot shuppersSnapShot:snapshot.getChildren())
                    {
                        ShipperModel shipperModel = shuppersSnapShot.getValue(ShipperModel.class);
                        temp.add(shipperModel);
                    }
                    shippersCallbackListener.onListShippersLoadSuccess(temp);
                }
                else
                    shippersCallbackListener.onListShippersLoadFailed("Empty Data");

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                shippersCallbackListener.onListShippersLoadFailed(databaseError.getMessage());


            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onListShippersLoadSuccess(List<ShipperModel> discountModelList) {
        shippersMutableLiveData.setValue(discountModelList);

    }

    @Override
    public void onListShippersLoadFailed(String message) {
        if (message.equals("Empty Data"))
            shippersMutableLiveData.setValue(null);
        messageError.setValue(message);

    }
}