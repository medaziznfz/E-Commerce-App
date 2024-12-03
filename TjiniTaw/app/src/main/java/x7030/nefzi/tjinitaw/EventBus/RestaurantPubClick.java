package x7030.nefzi.tjinitaw.EventBus;

import x7030.nefzi.tjinitaw.Model.RestaurantModel;

public class RestaurantPubClick {

    private RestaurantModel restaurantModel;

    public RestaurantPubClick(RestaurantModel restaurantModel) {
        this.restaurantModel = restaurantModel;
    }

    public RestaurantModel getRestaurantPubModel() {
        return restaurantModel;
    }

    public void setRestaurantPubModel(RestaurantModel restaurantModel) {
        this.restaurantModel = restaurantModel;
    }


}
