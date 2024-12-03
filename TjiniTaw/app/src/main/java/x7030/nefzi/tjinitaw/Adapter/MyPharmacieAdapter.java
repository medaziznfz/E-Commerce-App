package x7030.nefzi.tjinitaw.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import x7030.nefzi.tjinitaw.Callback.IRecyclerClickListener;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.EventBus.PharmacieSelectEvent;
import x7030.nefzi.tjinitaw.Model.PharmacieModel;
import x7030.nefzi.tjinitaw.R;

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
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_pharmacie,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Glide.with(context)
                .load(pharmacieModelList.get(position).getImageUrl())
                .placeholder(R.drawable.load)
                .into(holder.img_pharmacie);
        holder.txt_pharmacie_name.setText(new StringBuilder(pharmacieModelList.get(position).getName()));
        holder.txt_pharmacie_Address.setText(new StringBuilder(pharmacieModelList.get(position).getAddress()));
        holder.txt_pharmacie_number.setText(new StringBuilder(pharmacieModelList.get(position).getPhone()));
        if (!pharmacieModelList.get(position).isActive())
        {
            Glide.with(context)
                    .load(R.drawable.rouge)
                    .into(holder.pharmacie_status);
        }

        holder.setListener((view, pos) -> {
            Common.currentPharmacie = pharmacieModelList.get(pos);
            if (Common.currentPharmacie.isActive())
                EventBus.getDefault().postSticky(new PharmacieSelectEvent(pharmacieModelList.get(pos)));
            else
                Toast.makeText(context, "Pharmacie msakra", Toast.LENGTH_SHORT).show();


        });
    }

    @Override
    public int getItemCount() {
        return pharmacieModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_pharmacie_name)
        TextView txt_pharmacie_name;
        @BindView(R.id.txt_pharmacie_Address)
        TextView txt_pharmacie_Address;
        @BindView(R.id.img_pharmacie)
        ImageView img_pharmacie;
        @BindView(R.id.pharmacie_status)
        CircleImageView pharmacie_status;
        @BindView(R.id.txt_pharmacie_number)
        TextView txt_pharmacie_number;

        Unbinder unbinder;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view,getAdapterPosition());

        }
    }
}
