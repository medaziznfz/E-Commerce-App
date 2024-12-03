package x7030.nefzi.tjinitawpanel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitawpanel.Common.Common;
import x7030.nefzi.tjinitawpanel.EventBus.UpdateRestaurantEvent;
import x7030.nefzi.tjinitawpanel.Model.RestaurantModel;
import x7030.nefzi.tjinitawpanel.R;

public class MyRestaurantActiveAdapter extends RecyclerView.Adapter<MyRestaurantActiveAdapter.MyViewHolder> {
    Context context;
    List<RestaurantModel> restaurantModelList;

    public MyRestaurantActiveAdapter(Context context, List<RestaurantModel> restaurantModelList) {
        this.context = context;
        this.restaurantModelList = restaurantModelList;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_restaurant,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.txt_name.setText(new StringBuilder(restaurantModelList.get(position).getName()));
        holder.txt_phone.setText(new StringBuilder(restaurantModelList.get(position).getPhone()));
        holder.btn_enable.setChecked(restaurantModelList.get(position).getStatus());
        if(Common.RESTAURANT_REF == "Patisseries")
            holder.icon_status.setImageResource(R.drawable.cake);

        holder.btn_enable.setOnCheckedChangeListener((compoundButton, b) -> {
            EventBus.getDefault().postSticky(new UpdateRestaurantEvent(restaurantModelList.get(position),b));

        });

    }

    @Override
    public int getItemCount() {
        return restaurantModelList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        private Unbinder unbinder;

        @BindView(R.id.txt_name)
        TextView txt_name;
        @BindView(R.id.txt_phone)
        TextView txt_phone;
        @BindView(R.id.icon_status)
        ImageView icon_status;
        @BindView(R.id.btn_enable)
        SwitchCompat btn_enable;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }


}