package x7030.nefzi.tjinitawpanel.Callback;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.RadioButton;

import java.util.List;

import x7030.nefzi.tjinitawpanel.Model.RestaurantModel;

public interface IRestaurantLoadCallbackListener {
    void onRestaurantLoadSuccess(List<RestaurantModel> restaurantModelList);
    void onRestaurantLoadFailed(String message);
}