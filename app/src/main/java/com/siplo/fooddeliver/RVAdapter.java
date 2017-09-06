package com.siplo.fooddeliver;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asela on 6/15/17.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{
    List<ActiveOrder> orders;

    private static RecyclerView horizontalList;
    private final Context mContext;

    RVAdapter(Context context,List<ActiveOrder> orders){
        this.mContext = context;
        this.orders = orders;
    }
    HorizontalRVAdapter horizontalAdapter;
    public void notifyChild(){
        horizontalAdapter.notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        holder.table.setText(orders.get(position).tableId);
        DecimalFormat decim = new DecimalFormat("0.00");
        holder.total.setText("Total:   Rs."+decim.format(orders.get(position).total));
        horizontalAdapter = holder.horizontalAdapter;
        holder.horizontalAdapter.setData(orders.get(position).getItemList()); // List of Strings
        holder.horizontalAdapter.setRowIndex(position);

        holder.cv.setTag(position);

        if(orders.get(position).isCompleted){
            holder.button.setText("Completed");
            holder.button.setBackgroundColor(Color.argb(255,50,150,50));

        }
        holder.button.setTag(orders.get(position).getId());
        holder.callButton.setTag(orders.get(position).phoneNumber);
        holder.locationButton.setTag(orders.get(position).location);


//        personViewHolder.personAge.setText(persons.get(i).age);
//        personViewHolder.personPhoto.setImageResource(persons.get(i).photoId);
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_view, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView table;
        TextView total;
        Button button;
        Button callButton;
        Button locationButton;
        private HorizontalRVAdapter horizontalAdapter;
//        TextView personAge;
//        ImageView personPhoto;





        PersonViewHolder(View itemView) {
            super(itemView);
            Context context = itemView.getContext();

            cv = (CardView)itemView.findViewById(R.id.cv);
            table = (TextView)itemView.findViewById(R.id.table_text);
            total = (TextView)itemView.findViewById(R.id.total_text);
            button = (Button) itemView.findViewById(R.id.delivered_button);
            callButton = (Button) itemView.findViewById(R.id.call_button);

            locationButton = (Button) itemView.findViewById(R.id.location_button);
            horizontalList = (RecyclerView) itemView.findViewById(R.id.horizontal_list);
            horizontalList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            horizontalAdapter = new HorizontalRVAdapter();
            horizontalList.setAdapter(horizontalAdapter);
        }
//            personAge = (TextView)itemView.findViewById(R.id.person_age);
//            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }

    }



