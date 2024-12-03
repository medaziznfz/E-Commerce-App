package x7030.nefzi.tjinitaw.Callback;

import java.util.List;

import x7030.nefzi.tjinitaw.Model.RestaurantModel;


public interface IRestaurantPubCallbackListener {
    void OnRestaurantPubLoadSuccess(List<RestaurantModel> restaurantModels);
    void OnRestaurantPubLoadFailed(String message);
}
