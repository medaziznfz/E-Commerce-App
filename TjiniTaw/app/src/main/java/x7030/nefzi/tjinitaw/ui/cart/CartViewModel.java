package x7030.nefzi.tjinitaw.ui.cart;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Database.CartDataSource;
import x7030.nefzi.tjinitaw.Database.CartDatabase;
import x7030.nefzi.tjinitaw.Database.CartItem;
import x7030.nefzi.tjinitaw.Database.LocalCartDataSource;

public class CartViewModel extends ViewModel {
    private MutableLiveData<List<CartItem>> mutableLiveDataCartItems;
    private CompositeDisposable compositeDisposable ;
    private CartDataSource cartDataSource;

    public CartViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void initCartDataSource(Context context)
    {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());

    }

    public void onStop(){
        compositeDisposable.clear();
    }

    public MutableLiveData<List<CartItem>> getMutableLiveDataCartItems() {
        if (mutableLiveDataCartItems == null)
            mutableLiveDataCartItems = new MutableLiveData<>();
        getAllCartItems();
        return mutableLiveDataCartItems;
    }

    private void getAllCartItems() {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getName(),Common.currentRestaurant.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    mutableLiveDataCartItems.setValue(cartItems);

                },throwable -> {

                    mutableLiveDataCartItems.setValue(null);
                }));


    }
}