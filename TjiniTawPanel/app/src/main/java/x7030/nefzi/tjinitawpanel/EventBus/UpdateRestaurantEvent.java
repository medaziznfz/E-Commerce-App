package x7030.nefzi.tjinitawpanel.EventBus;

import x7030.nefzi.tjinitawpanel.Model.RestaurantModel;

public class UpdateRestaurantEvent {
    private RestaurantModel restaurantModel;
    private boolean active;

    public UpdateRestaurantEvent(RestaurantModel restaurantModel, boolean active) {
        this.restaurantModel = restaurantModel;
        this.active = active;
    }

    public RestaurantModel getRestaurantModel() {
        return restaurantModel;
    }

    public void setRestaurantModel(RestaurantModel restaurantModel) {
        this.restaurantModel = restaurantModel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}