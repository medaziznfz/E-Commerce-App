package x7030.nefzi.tjinitaw.EventBus;

import x7030.nefzi.tjinitaw.Model.FoodModel;

public class ProduitItemClick {
    private boolean success;
    private FoodModel foodModel;

    public ProduitItemClick(boolean success, FoodModel foodModel) {
        this.success = success;
        this.foodModel = foodModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public FoodModel getFoodModel() {
        return foodModel;
    }

    public void setFoodModel(FoodModel foodModel) {
        this.foodModel = foodModel;
    }
}
