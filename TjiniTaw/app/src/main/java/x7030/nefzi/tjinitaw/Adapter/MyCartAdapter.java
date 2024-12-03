package x7030.nefzi.tjinitaw.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Database.CartItem;
import x7030.nefzi.tjinitaw.EventBus.UpdateItemInCart;
import x7030.nefzi.tjinitaw.Model.AddonModel;
import x7030.nefzi.tjinitaw.Model.SizeModel;
import x7030.nefzi.tjinitaw.R;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {
    Context context;
    List<CartItem> cartItemList;
    Gson gson;

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.gson = new Gson();
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getFoodIamge())
                .placeholder(R.drawable.load)
                .into(holder.img_cart);
        holder.txt_food_name.setText(new StringBuilder(cartItemList.get(position).getFoodName()));


        holder.txt_food_price.setText(new StringBuilder("").append(Common.formatPrice(cartItemList.get(position).getFoodPrice()+cartItemList.get(position).getFoodExtraPrice())).append(" TND"));
        holder.numberButton.setNumber(String.valueOf(cartItemList.get(position).getFoodQuantity()));

        try {
            if (cartItemList.get(position).getFoodSize() != null) {
                if (cartItemList.get(position).getFoodSize().equals("3adi"))
                    holder.txt_food_size.setText(new StringBuilder("Taille: ").append("3adi"));
                else {
                    SizeModel sizeModel = gson.fromJson(cartItemList.get(position).getFoodSize(), new TypeToken<SizeModel>() {
                    }.getType());
                    holder.txt_food_size.setText(new StringBuilder("Taille: ").append(sizeModel.getName()));
                }

            }

            if (cartItemList.get(position).getFoodAddon() != null) {
                if (cartItemList.get(position).getFoodAddon().equals("7ata shy"))
                    holder.txt_food_addon.setText(new StringBuilder("Supplement: ").append("7ata shy"));
                else {
                    List<AddonModel> addonModels = gson.fromJson(cartItemList.get(position).getFoodAddon(),
                            new TypeToken<List<AddonModel>>() {
                            }.getType());
                    if (!Common.getListAddon(addonModels).isEmpty())
                        holder.txt_food_addon.setText(new StringBuilder("Supplement: ").append(Common.getListAddon(addonModels)));
                    else
                        holder.txt_food_addon.setText(new StringBuilder("Supplement: ").append("7ata shy"));


                }


            }

        }
        catch (Exception ex)
        {
            holder.txt_food_size.setVisibility(View.GONE);
            holder.txt_food_addon.setVisibility(View.GONE);

        }


        holder.numberButton.setOnValueChangeListener((view, oldValue, newValue) -> {
            cartItemList.get(position).setFoodQuantity(newValue);
            EventBus.getDefault().postSticky(new UpdateItemInCart(cartItemList.get(position)));

        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public CartItem getItemAtPosition(int pos) {
        return cartItemList.get(pos);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private Unbinder unbinder;
        @BindView(R.id.img_cart)
        ImageView img_cart;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.txt_food_size)
        TextView txt_food_size;
        @BindView(R.id.txt_food_addon)
        TextView txt_food_addon;
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.number_button)
        ElegantNumberButton numberButton;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}
