package com.example.birthreminder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.People;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BirthAdapter extends RecyclerView.Adapter<BirthAdapter.BirthHolder> {

    private final Context mContext;
    private List<Map<String, Object>> mValues;
    private People people;
    private List<BirthDate> birthDates;


    public BirthAdapter(People.PeopleWithBirthDates people, Context context) {
        Log.v("BirthAdapter", "peopleWithBirthDates");
        this.mContext = context;
        this.people = people.people;
        this.birthDates = people.birthDates;
    }

    public BirthAdapter(List<Map<String, Object>> values, Context mContext) {
        Log.v("BirthAdapter", "List<Map<String, Object>>");
        this.mValues = values;
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public BirthHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.birth_info_item, parent, false);
        return new BirthHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull final BirthHolder holder, int position) {
        if (mValues == null) {
            holder.people = people;
            holder.birthDate = birthDates.get(position);
        }
        else {
            holder.people = (People) mValues.get(position).get("people");
            holder.birthDate = (BirthDate) mValues.get(position).get("birth");
        }
        holder.nameTextView.setText(holder.people.getName());
        holder.nameTextView.setTextColor(holder.people.getColor());
        int[] param = BirthUtil.diff(holder.birthDate.getYear(), holder.birthDate.getMonth(), holder.birthDate.getDay());
        Log.v("holder", Arrays.toString(param));
        holder.ageTextView.setText(String.valueOf(holder.birthDate.getYear() - holder.people.getYear()));
        holder.alsoTextView.setText(param[3] < 0 ? "过了" : "还有");
        holder.yearTextView.setText(String.valueOf(param[0]));
        holder.monthTextView.setText(String.valueOf(param[1]));
        holder.dayTextView.setText(String.valueOf(param[2]));
        holder.itemView.setClickable(true);
        holder.itemView.setOnClickListener(v -> {
            //TODO
            Intent intent = new Intent(mContext, SchemeActivity.class);
            intent.putExtra("people", holder.people);
            intent.putExtra("birth", holder.birthDate);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mValues == null ? birthDates.size() : mValues.size();
    }

    public static class BirthHolder extends RecyclerView.ViewHolder {
        public final TextView nameTextView;
        public final TextView ageTextView;
        public final TextView alsoTextView;
        public final TextView yearTextView;
        public final TextView monthTextView;
        public final TextView dayTextView;
        public BirthDate birthDate;
        public People people;

        public BirthHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            ageTextView = itemView.findViewById(R.id.ageTextView);
            alsoTextView = itemView.findViewById(R.id.alsoTextView);
            yearTextView = itemView.findViewById(R.id.yearTextView);
            monthTextView = itemView.findViewById(R.id.monthTextView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }
}
