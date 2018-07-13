package com.informaticsmatrix.birthdayapp.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.informaticsmatrix.birthdayapp.R;
import com.informaticsmatrix.birthdayapp.greenDao.Birthday;
import com.informaticsmatrix.birthdayapp.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class BirthdayAdapter extends RecyclerView.Adapter<BirthdayAdapter.MyViewHolder> {

    private List<Birthday> birthdayList;
    private OnItemClickListener onItemClickListener;

    public BirthdayAdapter(List<Birthday> birthdayList,OnItemClickListener listener) {
        this.birthdayList = birthdayList;
        this.onItemClickListener=listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_birthday_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Birthday birthday = birthdayList.get(position);
        if(birthday!=null){
            holder.name.setText(birthday.getName());
            holder.dob.setText(birthday.getDobString());
            holder.age.setText("Turns "+birthday.getAge()+" this year");
            holder.cardView.setTag(birthday);
        }

    }

    @Override
    public int getItemCount() {
        if(birthdayList!=null)
        return birthdayList.size();
        else
            return 0;
    }

    public void upldateList(ArrayList<Birthday> list){
        if(birthdayList!=null && list!=null){
            birthdayList.clear();
            birthdayList.addAll(list);
            notifyDataSetChanged();
        }

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name,dob,age;
        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            name =  view.findViewById(R.id.name);
            dob = view.findViewById(R.id.dob);
            age =  view.findViewById(R.id.age);
            cardView=view.findViewById(R.id.card_view);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Birthday birthday= (Birthday) view.getTag();
                    if(birthday!=null && onItemClickListener!=null){
                        onItemClickListener.onItemClick(birthday);
                    }
                }
            });
        }
    }


}