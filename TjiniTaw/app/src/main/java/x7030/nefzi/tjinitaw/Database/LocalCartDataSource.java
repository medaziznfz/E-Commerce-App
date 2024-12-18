package x7030.nefzi.tjinitaw.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource{

    private CartDAO cartDAO;

    public LocalCartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    @Override
    public Flowable<List<CartItem>> getAllCart(String uid,String restaurantId) {

        return cartDAO.getAllCart(uid,restaurantId);
    }

    @Override
    public Single<Integer> countItemInCart(String uid,String restaurantId) {
        return cartDAO.countItemInCart(uid,restaurantId);
    }

    @Override
    public Single<Double> sumPriceInCart(String uid,String restaurantId) {
        return cartDAO.sumPriceInCart(uid,restaurantId);
    }

    @Override
    public Single<CartItem> getItemInCart(String foodId, String uid,String restaurantId) {
        return cartDAO.getItemInCart(foodId,uid,restaurantId);
    }

    @Override
    public Completable insertOrReplaceAll(CartItem... cartItems) {
        return cartDAO.insertOrReplaceAll(cartItems);
    }

    @Override
    public Single<Integer> UpdateCartItems(CartItem cartItem) {
        return cartDAO.UpdateCartItems(cartItem);
    }

    @Override
    public Single<Integer> deleteCartItem(CartItem cartItem) {
        return cartDAO.deleteCartItem(cartItem);
    }

    @Override
    public Single<Integer> clearCart(String uid,String restaurantId) {
        return cartDAO.clearCart(uid,restaurantId);
    }

    @Override
    public Single<CartItem> getItemWithAllOpitionInCart(String uid,String categoryId , String foodId, String foodSize, String foodAddon,String restaurantId) {
        return cartDAO.getItemWithAllOpitionInCart(uid,categoryId , foodId, foodSize, foodAddon,restaurantId);
    }
}
