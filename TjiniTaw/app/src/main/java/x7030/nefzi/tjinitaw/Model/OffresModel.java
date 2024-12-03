package x7030.nefzi.tjinitaw.Model;

public class OffresModel {
    private String menu_id,food_id,name,image,restaurant,restaurantTag,source;

    public OffresModel() {
    }

    public OffresModel(String menu_id, String food_id, String name, String image, String restaurant, String restaurantTag, String source) {
        this.menu_id = menu_id;
        this.food_id = food_id;
        this.name = name;
        this.image = image;
        this.restaurant = restaurant;
        this.restaurantTag = restaurantTag;
        this.source = source;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getRestaurantTag() {
        return restaurantTag;
    }

    public void setRestaurantTag(String restaurantTag) {
        this.restaurantTag = restaurantTag;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
