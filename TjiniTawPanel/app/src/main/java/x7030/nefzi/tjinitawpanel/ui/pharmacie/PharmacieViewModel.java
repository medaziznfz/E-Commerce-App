package x7030.nefzi.tjinitawpanel.ui.pharmacie;

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

import x7030.nefzi.tjinitawpanel.Callback.IPharmacyCallbackListener;
import x7030.nefzi.tjinitawpanel.Common.Common;
import x7030.nefzi.tjinitawpanel.Model.PharmacieModel;

public class PharmacieViewModel extends ViewModel implements IPharmacyCallbackListener {

    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private MutableLiveData<List<PharmacieModel>> pharmacyMutableList;
    private IPharmacyCallbackListener pharmacyCallbackListener;

    public PharmacieViewModel() {
        pharmacyCallbackListener = this;
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    public MutableLiveData<List<PharmacieModel>> getPharmacyMutableList() {
        if (pharmacyMutableList == null)
        {
            pharmacyMutableList = new MutableLiveData<>();
            loadPharmacy();

        }
        return pharmacyMutableList;
    }

    private void loadPharmacy() {
        List<PharmacieModel> templist = new ArrayList<>();
        DatabaseReference pharmacyRef = FirebaseDatabase.getInstance().getReference(Common.PHARMACY_REF);
        pharmacyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pharmacieSnapShot:dataSnapshot.getChildren())
                {
                    PharmacieModel pharmacieModel = pharmacieSnapShot.getValue(PharmacieModel.class);
                    pharmacieModel.setUid(pharmacieSnapShot.getKey());
                    templist.add(pharmacieModel);


                }
                pharmacyCallbackListener.onPharmacyLoadSuccess(templist);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                pharmacyCallbackListener.onPharmacyLoadFailed(databaseError.getMessage());

            }
        });

    }

    @Override
    public void onPharmacyLoadSuccess(List<PharmacieModel> pharmacieModelList) {
        if (pharmacyMutableList != null)
            pharmacyMutableList.setValue(pharmacieModelList);

    }


    @Override
    public void onPharmacyLoadFailed(String message) {
        messageError.setValue(message);

    }
}