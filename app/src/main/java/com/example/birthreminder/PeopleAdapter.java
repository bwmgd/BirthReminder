package com.example.birthreminder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.birthreminder.entity.People;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleHolder> {

    private final ArrayList<People> mValues;
    private final Context mContext;

    public PeopleAdapter(List<People> mValues, Context mContext) {
        this.mValues = (ArrayList<People>) mValues;
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public PeopleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.people_item, parent, false);
        return new PeopleHolder(view);
    }

    @Override
    public void onBindViewHolder(final PeopleHolder holder, int position) {
        holder.people = mValues.get(position);
        holder.nameTextView.setText(holder.people.getName());
        holder.nameTextView.setTextColor(holder.people.getColor());
        holder.lunarTextView.setText(
                holder.people.getBirthCode() < BirthUtil.SPECIAL_BIRTHDAY.LUNAR ? "公历" : "农历");
        holder.yearTextView.setText(String.valueOf(holder.people.getYear()));
        holder.monthTextView.setText(String.valueOf(holder.people.getMonth()));
        holder.dayTextView.setText(String.valueOf(holder.people.getDay()));
        holder.itemView.setClickable(true);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PeopleActivity.class);
            intent.putExtra("people", holder.people);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class PeopleHolder extends RecyclerView.ViewHolder {
        public final TextView nameTextView;
        public final TextView lunarTextView;
        public final TextView yearTextView;
        public final TextView monthTextView;
        public final TextView dayTextView;
        public People people;

        public PeopleHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            lunarTextView = itemView.findViewById(R.id.lunarTextView);
            yearTextView = itemView.findViewById(R.id.yearTextView);
            monthTextView = itemView.findViewById(R.id.monthTextView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }
}
