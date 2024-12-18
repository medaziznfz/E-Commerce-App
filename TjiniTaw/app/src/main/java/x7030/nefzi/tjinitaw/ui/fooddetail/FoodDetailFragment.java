package x7030.nefzi.tjinitaw.ui.fooddetail;

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

    @BindView(R.id.btn_ratting)
    FloatingActionButton btn_rating;

    @BindView(R.id.food_name)
    TextView food_name;

    @BindView(R.id.food_description)
    TextView food_description;

    @BindView(R.id.food_price)
    TextView food_price;

    @BindView(R.id.number_button)
    ElegantNumberButton numberButton;

    @BindView(R.id.ratting_Bar)
    RatingBar ratingBar;

    @BindView(R.id.btnShowComment)
    Button btnShowComment;

    @BindView(R.id.rdi_groupe_size)
    RadioGroup rdi_groupe_size;

    @BindView(R.id.img_add_addon)
    ImageView img_add_on;

    @BindView(R.id.chip_group_user_selected_addon)
    ChipGroup chip_group_user_selected_addon;





    @OnClick(R.id.img_add_addon)
    void onAddonClick()
    {
        if (Common.selectedFood.getAddon() != null)
        {

            displayAddonList();
            addonBottomSheetDialog.show();
        }
    }

    @OnClick(R.id.btnCart)
    void onCartItemAdd()
    {
        CartItem cartItem = new CartItem();
        cartItem.setRestaurantId(Common.currentRestaurant.getUid());
        cartItem.setUid(Common.currentUser.getName());
        cartItem.setUserPhone(Common.currentUser.getPhone());

        cartItem.setCategoryId(Common.categorySelected.getMenu_id());
        cartItem.setFoodId(Common.selectedFood.getId());
        cartItem.setFoodName(Common.selectedFood.getName());
        cartItem.setFoodIamge(Common.selectedFood.getImage());
        cartItem.setFoodPrice(Double.valueOf(String.valueOf(Common.selectedFood.getPrice())));
        cartItem.setFoodQuantity(Integer.valueOf(numberButton.getNumber()));
        cartItem.setFoodExtraPrice(Common.calculateExtraPrice(Common.selectedFood.getUserSelectedSize(),Common.selectedFood.getUserSelectedAddon()));

        if (Common.selectedFood.getUserSelectedAddon() != null && !Common.selectedFood.getUserSelectedAddon().isEmpty())
            cartItem.setFoodAddon(new Gson().toJson(Common.selectedFood.getUserSelectedAddon()));
        else
            cartItem.setFoodAddon("7ata shy");
        if (Common.selectedFood.getUserSelectedSize() != null)
            cartItem.setFoodSize(new Gson().toJson(Common.selectedFood.getUserSelectedSize()));
        else
            cartItem.setFoodSize("3adi");


        cartDataSource.getItemWithAllOpitionInCart(Common.currentUser.getName()
                ,Common.categorySelected.getMenu_id()
                ,cartItem.getFoodId()
                ,cartItem.getFoodSize()
                ,cartItem.getFoodAddon(),Common.currentRestaurant.getUid()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<CartItem>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {

            }

            @Override
            public void onSuccess(@NotNull CartItem cartItemFromDB) {
                if (cartItemFromDB.equals(cartItem))
                {
                    cartItemFromDB.setFoodExtraPrice(cartItem.getFoodExtraPrice());
                    cartItemFromDB.setFoodAddon(cartItem.getFoodAddon());
                    cartItemFromDB.setFoodSize(cartItem.getFoodSize());
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

    private void displayAddonList() {
        if (Common.selectedFood.getAddon().size()>0)
        {
            chip_group_Addon.clearCheck();
            chip_group_Addon.removeAllViews();
            edt_search.addTextChangedListener(this);
            for (AddonModel addonModel : Common.selectedFood.getAddon())
            {
                Chip chip = (Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+").append(addonModel.getPrice()).append(" TND)"));
                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b)
                    {
                        if (Common.selectedFood.getUserSelectedAddon() == null)
                            Common.selectedFood.setUserSelectedAddon(new ArrayList<>());
                        Common.selectedFood.getUserSelectedAddon().add(addonModel);

                    }


                });
                chip_group_Addon.addView(chip);



            }
        }

    }


    @OnClick(R.id.btn_ratting)
    void onRatingButtomClick() {
        showDialogRating();
    }

    @OnClick(R.id.btnShowComment)
    void onShowCommentButtonClick(){
        CommentFragment commentFragment = CommentFragment.getInstance();
        commentFragment.show(getActivity().getSupportFragmentManager(),"CommentFragment");
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        foodDetailViewModel =
                new ViewModelProvider(this).get(FoodDetailViewModel.class);

        View root = inflater.inflate(R.layout.fragment_food_detail,container,false);
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
            loading_dialog.dismiss();

        });
        foodDetailViewModel.getMutableLiveDataComment().observe(getViewLifecycleOwner(),commentModel -> {
            submitRatingToFirebase(commentModel);
            //loading_dialog.dismiss();
        } );

        return root;
    }

    private void initViews() {

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        addonBottomSheetDialog = new BottomSheetDialog(getContext(),R.style.DialogStyle);
        View layout_addon_display = getLayoutInflater().inflate(R.layout.layout_addon_display,null);
        chip_group_Addon = (ChipGroup) layout_addon_display.findViewById(R.id.chip_group_addon);
        edt_search = (EditText)layout_addon_display.findViewById(R.id.edt_search);
        addonBottomSheetDialog.setContentView(layout_addon_display);
        addonBottomSheetDialog.setOnDismissListener(dialogInterface -> {
            displayUserSelectedAddon();
            calculateTotalPrice();

        });





    }

    private void displayUserSelectedAddon() {
        if (Common.selectedFood.getUserSelectedAddon() != null && Common.selectedFood.getUserSelectedAddon().size()>0)
        {
            chip_group_user_selected_addon.removeAllViews();
            for (AddonModel addonModel : Common.selectedFood.getUserSelectedAddon())
            {
                Chip chip = (Chip)getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+").append(addonModel.getPrice()).append(" TND)"));
                chip.setClickable(false);
                chip.setOnCloseIconClickListener(view -> {

                    chip_group_user_selected_addon.removeView(view);
                    Common.selectedFood.getUserSelectedAddon().remove(addonModel);
                    calculateTotalPrice();

                });
                chip_group_user_selected_addon.addView(chip);
            }}
        else
            chip_group_user_selected_addon.removeAllViews();

    }




    private void showDialogRating() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("A3TINA RAYEK");
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_rating,null);
        RatingBar ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
        EditText edt_comment = (EditText)itemView.findViewById(R.id.edt_comment);
        builder.setView(itemView);
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {

            dialogInterface.dismiss();

        });
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            CommentModel commentModel = new CommentModel();
            commentModel.setName(Common.currentUser.getName());
            commentModel.setUid(Common.currentUser.getPhone());
            commentModel.setComment(edt_comment.getText().toString());
            commentModel.setRatingValue(ratingBar.getRating());
            Map<String,Object> serverTimeStamp = new HashMap<>();
            serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
            commentModel.setCommentTimeStamp(serverTimeStamp);

            foodDetailViewModel.setCommentModel(commentModel);


        });
        AlertDialog dialog  = builder.create();
        dialog.show();

    }





    private void submitRatingToFirebase(CommentModel commentModel) {

        FirebaseDatabase.getInstance()
                .getReference(Common.RESTAURANT_REF)
                .child(Common.currentRestaurant.getUid())
                .child(Common.COMMENT_REF)
                .child(Common.selectedFood.getId())
                .push()
                .setValue(commentModel).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                addRatingToFood(commentModel.getRatingValue());

            }
        });
    }

    private void addRatingToFood(float ratingValue) {
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();
        FirebaseDatabase.getInstance()
                .getReference(Common.RESTAURANT_REF)
                .child(Common.currentRestaurant.getUid())
                .child(Common.CATEGORY_REF)
                .child(Common.categorySelected.getMenu_id())
                .child("foods").child(Common.selectedFood.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            FoodModel foodModel = dataSnapshot.getValue(FoodModel.class);
                            foodModel.setKey(Common.selectedFood.getKey());
                            if (foodModel.getRatingValue() == null)
                                foodModel.setRatingValue(0d);
                            if (foodModel.getRatingCount() == null)
                                foodModel.setRatingCount(0l);


                            double sumRating = foodModel.getRatingValue()+ratingValue;
                            long ratingCount  = foodModel.getRatingCount()+1;

                            Map<String,Object> updateData = new HashMap<>();
                            updateData.put("ratingValue",sumRating);
                            updateData.put("ratingCount",ratingCount);

                            foodModel.setRatingValue(sumRating);
                            foodModel.setRatingCount(ratingCount);

                            dataSnapshot.getRef().updateChildren(updateData).addOnCompleteListener(task -> {
                                if (task.isSuccessful())
                                {

                                    Toast.makeText(getContext(), "Ya3tik e sa7a !", Toast.LENGTH_SHORT).show();
                                    //Common.selectedFood = foodModel;
                                    rdi_groupe_size.removeAllViews();
                                    foodDetailViewModel.setFoodModel(foodModel);




                                }
                                loading_dialog.dismiss();

                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void displayInfo(FoodModel foodModel) {


        Glide.with(getContext()).load(foodModel.getImage()).into(img_food);
        food_name.setText(new StringBuilder(foodModel.getName()));
        food_description.setText(new StringBuilder(foodModel.getDescription()));
        food_price.setText(new StringBuilder(String.valueOf(foodModel.getPrice())));
        if (foodModel.getRatingValue() != null)
            ratingBar.setRating(foodModel.getRatingValue().floatValue() / foodModel.getRatingCount());

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(Common.selectedFood.getName());

        for (SizeModel sizeModel : Common.selectedFood.getSize())
        {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b)
                    Common.selectedFood.setUserSelectedSize(sizeModel);
                calculateTotalPrice();

            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1.0f);
            radioButton.setLayoutParams(params);
            radioButton.setText(sizeModel.getName());
            radioButton.setTag(sizeModel.getPrice());
            //rdi_groupe_size.removeAllViews();
            rdi_groupe_size.addView(radioButton);


        }
        if (rdi_groupe_size.getChildCount()>0)
        {
            RadioButton radioButton = (RadioButton)rdi_groupe_size.getChildAt(0);
            radioButton.setChecked(true);

        }
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {

        double totalPrice = Common.selectedFood.getPrice();
        double displayPrice = 0.0;


        if (Common.selectedFood.getUserSelectedAddon() != null && Common.selectedFood.getUserSelectedAddon().size()>0)
            for (AddonModel addonModel : Common.selectedFood.getUserSelectedAddon())
                totalPrice+=addonModel.getPrice();

        if(Common.selectedFood.getUserSelectedSize() != null)
            totalPrice += Double.parseDouble(String.valueOf(Common.selectedFood.getUserSelectedSize().getPrice()));

        displayPrice = totalPrice;
        displayPrice = displayPrice*100.0/100.0;

        food_price.setText(new StringBuilder("").append(Common.formatPrice(displayPrice)).toString());
    }


    @Override
    public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        chip_group_Addon.clearCheck();
        chip_group_Addon.removeAllViews();

        for (AddonModel addonModel : Common.selectedFood.getAddon())
        {
            if (addonModel.getName().toLowerCase().contains(charSequence.toString().toLowerCase()))
            {
                Chip chip = (Chip)getLayoutInflater().inflate(R.layout.layout_addon_item,null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+").append(addonModel.getPrice()).append(" TND)"));
                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b)
                    {
                        if (Common.selectedFood.getUserSelectedAddon() == null) {
                            Common.selectedFood.setUserSelectedAddon(new ArrayList<>());
                            food_price.setText(food_price.toString());

                        }
                        Common.selectedFood.getUserSelectedAddon().add(addonModel); // thabt lahne ya nefzi

                    }


                });
                chip_group_Addon.addView(chip);

            }

        }
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