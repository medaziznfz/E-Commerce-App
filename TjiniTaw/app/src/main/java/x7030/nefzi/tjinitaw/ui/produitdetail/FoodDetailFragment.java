package x7030.nefzi.tjinitaw.ui.produitdetail;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Database.CartDataSource;
import x7030.nefzi.tjinitaw.Database.CartDatabase;
import x7030.nefzi.tjinitaw.Database.CartItem;
import x7030.nefzi.tjinitaw.Database.LocalCartDataSource;
import x7030.nefzi.tjinitaw.EventBus.CounterCartEvent;
import x7030.nefzi.tjinitaw.EventBus.HideFABCart;
import x7030.nefzi.tjinitaw.Model.AddonModel;
import x7030.nefzi.tjinitaw.Model.CommentModel;
import x7030.nefzi.tjinitaw.Model.FoodModel;
import x7030.nefzi.tjinitaw.Model.SizeModel;
import x7030.nefzi.tjinitaw.R;
import x7030.nefzi.tjinitaw.ui.comments.CommentFragment;

public class FoodDetailFragment extends Fragment implements TextWatcher {

    private FoodDetailViewModel foodDetailViewModel;
    private Unbinder unbinder;
    private android.app.AlertDialog waitingSheetDialog;
    private BottomSheetDialog addonBottomSheetDialog;
    private CartDataSource cartDataSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    ChipGroup chip_group_Addon;
    EditText edt_search;

    @BindView(R.id.img_food)
    ImageView img_food;

    @BindView(R.id.btnCart)
    CounterFab btnCart;


    @BindView(R.id.food_name)
    TextView food_name;

    @BindView(R.id.food_description)
    TextView food_description;

    @BindView(R.id.food_price)
    TextView food_price;

    @BindView(R.id.number_button)
    ElegantNumberButton numberButton;


    @OnClick(R.id.btnCart)
    void onCartItemAdd()
    {
        CartItem cartItem = new CartItem();
        cartItem.setRestaurantId("Magasin");
        cartItem.setUid(Common.currentUser.getName());
        cartItem.setUserPhone(Common.currentUser.getPhone());

        cartItem.setCategoryId(Common.categorySelected.getMenu_id());
        cartItem.setFoodId(Common.selectedFood.getId());
        cartItem.setFoodName(Common.selectedFood.getName());
        cartItem.setFoodIamge(Common.selectedFood.getImage());
        cartItem.setFoodPrice(Double.valueOf(String.valueOf(Common.selectedFood.getPrice())));
        cartItem.setFoodQuantity(Integer.valueOf(numberButton.getNumber()));
        cartItem.setFoodExtraPrice(Common.calculateExtraPrice(Common.selectedFood.getUserSelectedSize(),Common.selectedFood.getUserSelectedAddon()));
        cartItem.setFoodAddon("null");
        cartItem.setFoodSize("null");



        cartDataSource.getItemWithAllOpitionInCart(Common.currentUser.getName()
                ,Common.categorySelected.getMenu_id()
                ,cartItem.getFoodId()
                ,"null"
                ,"null","Magasin").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<CartItem>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {

            }

            @Override
            public void onSuccess(@NotNull CartItem cartItemFromDB) {
                if (cartItemFromDB.equals(cartItem))
                {
                    cartItemFromDB.setFoodExtraPrice(0.0);
                    cartItemFromDB.setFoodAddon("null");
                    cartItemFromDB.setFoodSize("null");
                    cartItemFromDB.setFoodQuantity(cartItemFromDB.getFoodQuantity() + cartItem.getFoodQuantity());

                    cartDataSource.UpdateCartItems(cartItemFromDB).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@NotNull Integer integer) {
                            EventBus.getDefault().postSticky(new CounterCartEvent(true));

                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Toast.makeText(getContext(), "{UPDATE CART}"+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }
                else
                {

                    compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {

                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                    },throwable ->{
                        Toast.makeText(getContext(), "{CART ERROR}"+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                    }));

                }

            }

            @Override
            public void onError( Throwable e) {
                if (e.getMessage().contains("empty"))
                {
                    compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {

                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                    },throwable ->{
                        Toast.makeText(getContext(), "{CART ERROR}"+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                    }));

                }
                else
                    Toast.makeText(getContext(), "{GET CART}"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }






    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        foodDetailViewModel =
                new ViewModelProvider(this).get(FoodDetailViewModel.class);

        View root = inflater.inflate(R.layout.fragment_produit_detail,container,false);
        unbinder = ButterKnife.bind(this,root);View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();
        initViews();
        foodDetailViewModel.getMutableLiveDataFood().observe(getViewLifecycleOwner(), foodModel -> {
            displayInfo(foodModel);
            displayInfo(foodModel);
            loading_dialog.dismiss();

        });

        return root;
    }

    private void initViews() {

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        addonBottomSheetDialog = new BottomSheetDialog(getContext(),R.style.DialogStyle);
        View layout_addon_display = getLayoutInflater().inflate(R.layout.layout_addon_display,null);
        addonBottomSheetDialog.setContentView(layout_addon_display);
        addonBottomSheetDialog.setOnDismissListener(dialogInterface -> {
            calculateTotalPrice();

        });





    }











    private void displayInfo(FoodModel foodModel) {


        Glide.with(getContext()).load(foodModel.getImage()).into(img_food);
        food_name.setText(new StringBuilder(foodModel.getName()));
        food_description.setText(new StringBuilder(foodModel.getDescription()));
        food_price.setText(new StringBuilder(String.valueOf(foodModel.getPrice())));

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(Common.selectedFood.getName());
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {

        double totalPrice = Common.selectedFood.getPrice();
        double displayPrice = 0.0;
        displayPrice = totalPrice;
        displayPrice = displayPrice*100.0/100.0;

        food_price.setText(new StringBuilder("").append(Common.formatPrice(displayPrice)).toString());
    }


    @Override
    public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().postSticky(new HideFABCart(false));
        super.onStart();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        if (Common.selectedFood.getUserSelectedAddon()!=null)
            Common.selectedFood.getUserSelectedAddon().clear();
        super.onDestroy();
    }
}