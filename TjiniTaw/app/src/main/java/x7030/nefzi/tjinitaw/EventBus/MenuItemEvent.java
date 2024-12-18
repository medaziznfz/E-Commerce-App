package x7030.nefzi.tjinitaw.EventBus;


import x7030.nefzi.tjinitaw.Model.RestaurantModel;

public class MenuItemEvent {
    private boolean success;
    private RestaurantModel restaurantModel;

    public MenuItemEvent(boolean success, RestaurantModel restaurantModel) {
        this.success = success;
        this.restaurantModel = restaurantModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public RestaurantModel getRestaurantModel() {
        return restaurantModel;
    }

    public void setRestaurantModel(RestaurantModel restaurantModel) {
        this.restaurantModel = restaurantModel;
    }
}
