package x7030nefzi.admin.ui.discount;

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

import x7030nefzi.admin.Callback.IDiscountCallbackListener;
import x7030nefzi.admin.Common.Common;
import x7030nefzi.admin.Model.DiscountModel;


public class DiscountViewModel extends ViewModel implements IDiscountCallbackListener {
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private MutableLiveData<List<DiscountModel>> discountMutableLiveData;
    private IDiscountCallbackListener discountCallbackListener;

    public DiscountViewModel() {
        discountCallbackListener = this;
    }

    public MutableLiveData<List<DiscountModel>> getDiscountMutableLiveData() {
        if (discountMutableLiveData == null) discountMutableLiveData = new MutableLiveData<>();
            loadDiscount();

        return discountMutableLiveData;
    }

    public void loadDiscount() {
        List<DiscountModel> temp = new ArrayList<>();
        DatabaseReference discountRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Common.DISCOUNT_REF);
        discountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getChildren().iterator().hasNext())
                {
                    for (DataSnapshot discountSnapShot:snapshot.getChildren())
                    {
                        DiscountModel discountModel = discountSnapShot.getValue(DiscountModel.class);
                        discountModel.setKey(discountSnapShot.getKey());
                        temp.add(discountModel);
                    }
                    discountCallbackListener.onListDiscountLoadSuccess(temp);
                }
                else
                    discountCallbackListener.onListDiscountLoadFailed("Empty Data");

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                discountCallbackListener.onListDiscountLoadFailed(databaseError.getMessage());


            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onListDiscountLoadSuccess(List<DiscountModel> discountModelList) {
        discountMutableLiveData.setValue(discountModelList);

    }

    @Override
    public void onListDiscountLoadFailed(String message) {
        if (message.equals("Empty Data"))
            discountMutableLiveData.setValue(null);
        messageError.setValue(message);

    }
}