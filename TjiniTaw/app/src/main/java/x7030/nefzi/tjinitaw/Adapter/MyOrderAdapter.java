package x7030.nefzi.tjinitaw.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitaw.Callback.IRecyclerClickListener;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Database.CartItem;
import x7030.nefzi.tjinitaw.Model.Order;
import x7030.nefzi.tjinitaw.R;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {
    private Context context;
    private List<Order> orderList;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    public MyOrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    public  Order getItemAtPosition(int pos){
        return  orderList.get(pos);
    }
    public void setItemAtPosition(int pos,Order item)
    {
        orderList.set(pos,item);
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_order_item,parent,false));



    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {



        if (orderList.get(position).getCartItemList() == null)
        {

            holder.order_nrml.setVisibility(View.GONE);
            holder.service_task.setVisibility(View.VISIBLE);
            calendar.setTimeInMillis(orderList.get(position).getCreateDate());
            Date date = new Date(orderList.get(position).getCreateDate());

            Common.setSpanStringColor("",Common.getDateOfWeek(calendar.get(Calendar.DAY_OF_WEEK))+"  "+simpleDateFormat.format(date),
                    holder.txt_service_date, Color.parseColor("#000000"));
            holder.txt_service_source.setText(new StringBuilder(orderList.get(position).getRestaurantName()));
            Common.setSpanStringColor("",orderList.get(position).getOrderNumber(),
                    holder.txt_service_number, Color.parseColor("#0886EA"));

            Common.setSpanStringColor("Status Commande: ",Common.convertStatusToText(orderList.get(position).getOrderStatus()),
                    holder.txt_service_statut, Color.parseColor("#E91E63"));

            if (orderList.get(position).getFees() != 0.0)
                holder.txt_service_fees.setText(new StringBuilder("frais de livraison : "+Common.formatPrice(orderList.get(position).getFees())+" tnd"));
            else
                holder.txt_service_fees.setText(new StringBuilder("frais de livraison : en train de calculer"));


            if (!orderList.get(position).getShipperName().equals("noOne")) {
                holder.txt_service_shipper.setVisibility(View.VISIBLE);
                Common.setSpanStringColor("3and: ", orderList.get(position).getShipperName(),
                        holder.txt_service_shipper, Color.parseColor("#0E38CF"));



            }

            holder.txt_service_service.setText(new StringBuilder(orderList.get(position).getService()));
            
            holder.setRecyclerClickListener(new IRecyclerClickListener() {
                @Override
                public void onItemClickListener(View view, int pos) {
                    Toast.makeText(context, "ma3andouch detail", Toast.LENGTH_SHORT).show();
                }
            });

        }
        else
        {
            holder.order_nrml.setVisibility(View.VISIBLE);
            holder.service_task.setVisibility(View.GONE);
            Glide.with(context).load(orderList.get(position).getCartItemList().get(0).getFoodIamge())
                    .placeholder(R.drawable.load)
                    .into(holder.img_order);

            calendar.setTimeInMillis(orderList.get(position).getCreateDate());
            Date date = new Date(orderList.get(position).getCreateDate());

            Common.setSpanStringColor("",Common.getDateOfWeek(calendar.get(Calendar.DAY_OF_WEEK))+"  "+simpleDateFormat.format(date),
                    holder.txt_order_date, Color.parseColor("#000000"));
            holder.txt_order_source.setText(new StringBuilder(orderList.get(position).getRestaurantName()));

            Common.setSpanStringColor("",orderList.get(position).getOrderNumber(),
                    holder.txt_order_number, Color.parseColor("#0886EA"));






            if (orderList.get(position).getDiscount() == 0) {
                String payment=(new StringBuilder("").append(Common.formatPrice(orderList.get(position).getFinalPayment())).append(" TND")).toString();

                Common.setSpanStringColor("Total: ", payment,
                        holder.txt_order_price, Color.parseColor("#dc143c"));

            }
            else {
                String payment=(new StringBuilder("").append(Common.formatPrice(orderList.get(position).getFinalPayment())).append(" TND").append(" (-")
                        .append(orderList.get(position).getDiscount()).append("%)")).toString();
                Common.setSpanStringColor("Total: ",payment,
                        holder.txt_order_price, Color.parseColor("#C60808"));

            }





            Common.setSpanStringColor("Status Commande: ",Common.convertStatusToText(orderList.get(position).getOrderStatus()),
                    holder.txt_order_status, Color.parseColor("#E91E63"));

            if (orderList.get(position).getFees() != 0.0)
                holder.txt_order_fees.setText(new StringBuilder("frais de livraison : "+Common.formatPrice(orderList.get(position).getFees())+" tnd"));
            else
                holder.txt_order_fees.setText(new StringBuilder("frais de livraison : en train de calculer"));


            if (!orderList.get(position).getShipperName().equals("noOne")) {
                holder.txt_order_shipper.setVisibility(View.VISIBLE);
                Common.setSpanStringColor("3and: ", orderList.get(position).getShipperName(),
                        holder.txt_order_shipper, Color.parseColor("#0E38CF"));

            }
            holder.setRecyclerClickListener((view, pos) ->

                    showDialog(orderList.get(pos).getCartItemList()));
        }


        




    }

    private void showDialog(List<CartItem> cartItemList) {
        View layout_dialog = LayoutInflater.from(context).inflate(R.layout.layout_dialog_order_detail,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout_dialog);

        Button btn_ok = (Button)layout_dialog.findViewById(R.id.btn_ok);
        RecyclerView recycler_order_detail = (RecyclerView)layout_dialog.findViewById(R.id.recycler_order_detail);
        recycler_order_detail.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recycler_order_detail.setLayoutManager(layoutManager);
        recycler_order_detail.addItemDecoration(new DividerItemDecoration(context,layoutManager.getOrientation()));

        MyOrderDetailAdapter myOrderDetailAdapter = new MyOrderDetailAdapter(context,cartItemList);
        recycler_order_detail.setAdapter(myOrderDetailAdapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        btn_ok.setOnClickListener(view -> dialog.dismiss());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_order_statut)
        TextView txt_order_status;
        @BindView(R.id.txt_order_price)
        TextView txt_order_price;
        @BindView(R.id.txt_order_number)
        TextView txt_order_number;
        @BindView(R.id.txt_order_date)
        TextView txt_order_date;
        @BindView(R.id.txt_order_source)
        TextView txt_order_source;
        @BindView(R.id.txt_order_shipper)
        TextView txt_order_shipper;
        @BindView(R.id.img_order)
        ImageView img_order;
        @BindView(R.id.order_nrml)
        LinearLayout order_nrml;
        @BindView(R.id.service_task)
        LinearLayout service_task;
        @BindView(R.id.txt_service_number)
        TextView txt_service_number;
        @BindView(R.id.txt_service_date)
        TextView txt_service_date;
        @BindView(R.id.txt_service_source)
        TextView txt_service_source;
        @BindView(R.id.txt_service_shipper)
        TextView txt_service_shipper;
        @BindView(R.id.txt_service_statut)
        TextView txt_service_statut;
        @BindView(R.id.txt_service_service)
        TextView txt_service_service;
        @BindView(R.id.txt_service_fees)
        TextView txt_service_fees;
        @BindView(R.id.txt_order_fees)
        TextView txt_order_fees;

        Unbinder unbinder;

        IRecyclerClickListener recyclerClickListener;

        public void setRecyclerClickListener(IRecyclerClickListener recyclerClickListener) {
            this.recyclerClickListener = recyclerClickListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            recyclerClickListener.onItemClickListener(view,getAdapterPosition());

        }
    }
}
