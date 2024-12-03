package x7030.nefzi.tjinitawpanel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import x7030.nefzi.tjinitawpanel.EventBus.UpdatePharmacyEvent;
import x7030.nefzi.tjinitawpanel.Model.PharmacieModel;
import x7030.nefzi.tjinitawpanel.R;

public class MyPharmacieAdapter extends RecyclerView.Adapter<MyPharmacieAdapter.MyViewHolder> {
    Context context;
    List<PharmacieModel> pharmacieModelList;

    public MyPharmacieAdapter(Context context, List<PharmacieModel> pharmacieModelList) {
        this.context = context;
        this.pharmacieModelList = pharmacieModelList;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_pharmacy_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.txt_name.setText(new StringBuilder(pharmacieModelList.get(position).getName()));
        holder.txt_phone.setText(new StringBuilder(pharmacieModelList.get(position).getPhone()));
        holder.btn_enable.setChecked(pharmacieModelList.get(position).isActive());

        holder.btn_enable.setOnCheckedChangeListener((compoundButton, b) -> {
            EventBus.getDefault().postSticky(new UpdatePharmacyEvent(pharmacieModelList.get(position),b));

        });

    }

    @Override
    public int getItemCount() {
        return pharmacieModelList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        private Unbinder unbinder;

        @BindView(R.id.txt_name)
        TextView txt_name;
        @BindView(R.id.txt_phone)
        TextView txt_phone;
        @BindView(R.id.btn_enable)
        SwitchCompat btn_enable;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }


}
