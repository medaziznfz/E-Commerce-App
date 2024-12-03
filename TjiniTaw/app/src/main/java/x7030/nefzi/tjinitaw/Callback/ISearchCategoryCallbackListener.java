package x7030.nefzi.tjinitaw.Callback;


import x7030.nefzi.tjinitaw.Database.CartItem;
import x7030.nefzi.tjinitaw.Model.CategoryModel;

public interface ISearchCategoryCallbackListener {
    void onSearchCategoryFound(CategoryModel categoryModel, CartItem cartItem);
    void onSearchCategoryNotFound(String message);

}
