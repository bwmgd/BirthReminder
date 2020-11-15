package com.example.birthreminder.ui.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.birthreminder.R;
import com.example.birthreminder.application.BirthApplication;
import com.example.birthreminder.entity.People;
import com.example.birthreminder.ui.birth.BirthActivity;
import com.example.birthreminder.util.BirthUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleHolder> {

    private final List<People> mValues;
    private final Context mContext;

    public PeopleAdapter(List<People> mValues, Context mContext) {
        this.mValues = mValues;
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
        People people = mValues.get(position);
        holder.nameTextView.setText(people.getName());
        holder.nameTextView.setTextColor(people.getColor());
        holder.lunarTextView.setText(
                people.getBirthCode() < BirthUtil.SPECIAL_BIRTHDAY.LUNAR ? "公历" : "农历");
        holder.yearTextView.setText(String.valueOf(people.getYear()));
        int month = people.getMonth();
        holder.monthTextView.setText(month < 0 ? "闰" + month : String.valueOf(month));
        holder.dayTextView.setText(String.valueOf(people.getDay()));
        holder.itemView.setClickable(true);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, BirthActivity.class);
            intent.putExtra(BirthApplication.PEOPLE, people);
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
