package x7030.nefzi.tjinitaw.Callback;


import x7030.nefzi.tjinitaw.Model.Order;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(Order order , long estimateTimeTnMs);
    void onLoadTimeFailed(String message);
}
