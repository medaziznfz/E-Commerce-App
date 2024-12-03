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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitaw.Database.CartItem;
import x7030.nefzi.tjinitaw.Model.AddonModel;
import x7030.nefzi.tjinitaw.Model.SizeModel;
import x7030.nefzi.tjinitaw.R;

public class MyOrderDetailAdapter extends RecyclerView.Adapter<MyOrderDetailAdapter.MyViewHolder> {

    Context context;
    List<CartItem> cartItemList;
    Gson gson;

    public MyOrderDetailAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        gson = new Gson();
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_order_detail_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getFoodIamge())
                .into(holder.img_food_image);
        holder.txt_food_name.setText(new StringBuilder().append(cartItemList.get(position).getFoodName()));
        holder.txt_food_quantity.setText(new StringBuilder("9adeh: ").append(cartItemList.get(position).getFoodQuantity()));

        try {
            if (!cartItemList.get(position).getFoodSize().equals("3adi")) {
                SizeModel sizeModel = gson.fromJson(cartItemList.get(position).getFoodSize(), new TypeToken<SizeModel>() {
                }.getType());
                if (sizeModel != null)
                    holder.txt_size.setText(new StringBuilder("Taille: ").append(sizeModel.getName()));
            } else
                holder.txt_size.setText(new StringBuilder("Taille: ").append("3adi"));


            if (!cartItemList.get(position).getFoodAddon().equals("7ata shy")) {
                List<AddonModel> addonModels = gson.fromJson(cartItemList.get(position).getFoodAddon(), new TypeToken<List<AddonModel>>() {
                }.getType());
                StringBuilder addonString = new StringBuilder();
                if (addonModels != null) {
                    for (AddonModel addonModel : addonModels)
                        addonString.append(addonModel.getName()).append(",");
                    addonString.delete(addonString.length() - 1, addonString.length());
                    holder.txt_food_addon_on.setText(new StringBuilder("Supplement: ").append(addonString));
                }

            } else {
                holder.txt_food_addon_on.setText(new StringBuilder("Supplement: 7ata shy"));
            }
        }
        catch (Exception exception)
        {
            holder.txt_food_addon_on.setVisibility(View.GONE);
            holder.txt_size.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() { return cartItemList.size(); }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_addon_on)
        TextView txt_food_addon_on;
        @BindView(R.id.txt_food_quantity)
        TextView txt_food_quantity;
        @BindView(R.id.txt_size)
        TextView txt_size;
        @BindView(R.id.img_food_image)
        ImageView img_food_image;

        private Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}