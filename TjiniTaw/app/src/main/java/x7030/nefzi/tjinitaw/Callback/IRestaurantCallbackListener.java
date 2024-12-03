package x7030.nefzi.tjinitaw.Callback;

import java.util.List;

import x7030.nefzi.tjinitaw.Model.RestaurantModel;


public interface IRestaurantCallbackListener {
    void OnRestaurantLoadSuccess(List<RestaurantModel> restaurantModelList);
    void OnRestaurantLoadFailed(String message);
}
