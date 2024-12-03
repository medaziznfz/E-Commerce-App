package x7030.nefzi.tjinitaw.ui.pharmacie;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import x7030.nefzi.tjinitaw.Callback.IPharmacieCallbackListener;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Model.PharmacieModel;

public class PharmacieViewModel extends ViewModel implements IPharmacieCallbackListener {
    private MutableLiveData<List<PharmacieModel>> pharmacieListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IPharmacieCallbackListener listener;


    public PharmacieViewModel() {
        listener = this;
    }

    public MutableLiveData<List<PharmacieModel>> getPharmacieListMutable() {
        if (pharmacieListMutable == null)
        {
            pharmacieListMutable = new MutableLiveData<>();
            loadPharmacieFromFirebase();
        }
        return pharmacieListMutable;
    }

    private void loadPharmacieFromFirebase() {
        List<PharmacieModel> pharmacieModels = new ArrayList<>();
        DatabaseReference pharmacieRef = FirebaseDatabase.getInstance()
                .getReference(Common.PHARMACIE_REF);
        pharmacieRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot pharmacieSnapShot : dataSnapshot.getChildren())
                    {
                        PharmacieModel pharmacieModel = pharmacieSnapShot.getValue(PharmacieModel.class);
                        pharmacieModel.setUid(pharmacieSnapShot.getKey());
                        pharmacieModels.add(pharmacieModel);


                    }
                    if (pharmacieModels.size() > 0)
                        listener.OnPharmacieLoadSuccess(pharmacieModels);
                    else
                        listener.OnPharmacieLoadFailed("Pharmacie list empty");

                }
                else
                    listener.OnPharmacieLoadFailed("Pharmacie List Doesn't exists!");

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
    public void OnPharmacieLoadSuccess(List<PharmacieModel> pharmacieModelList) {
        pharmacieListMutable.setValue(pharmacieModelList);

    }

    @Override
    public void OnPharmacieLoadFailed(String message) {
        messageError.setValue(message);

    }
}