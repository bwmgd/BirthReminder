package com.example.birthreminder.ui.birth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.birthreminder.R;
import com.example.birthreminder.application.BirthApplication;
import com.example.birthreminder.entity.BirthDate;
import com.example.birthreminder.entity.People;
import com.example.birthreminder.ui.scheme.SchemeActivity;
import com.example.birthreminder.util.BirthUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BirthAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private List<Map<String, Object>> mValues;
    private People people;
    private List<BirthDate> birthDates;
    private final boolean isEmpty;


    public BirthAdapter(People.PeopleWithBirthDates people, Context context) {
        Log.v("BirthAdapter", "peopleWithBirthDates");
        this.mContext = context;
        this.people = people.people;
        this.birthDates = people.birthDates;
        this.isEmpty = false;
    }

    public BirthAdapter(List<Map<String, Object>> values, Context mContext) {
        Log.v("BirthAdapter", "List<Map<String, Object>>");
        this.mValues = values;
        this.mContext = mContext;
        this.isEmpty = values.isEmpty();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isEmpty) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view_tab, parent, false);
            return new RecyclerView.ViewHolder(emptyView) {
            };
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.birth_item, parent, false);
        return new BirthHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holders, int position) {
        if (holders instanceof BirthHolder) {
            BirthHolder holder = (BirthHolder) holders;
            People people;
            BirthDate birthDate;
            if (mValues == null) { //两种载入适配器的方式
                people = this.people;
                birthDate = birthDates.get(position);
            }
            else {
                people = (People) mValues.get(position).get(BirthApplication.PEOPLE);
                birthDate = (BirthDate) mValues.get(position).get(BirthApplication.BIRTH);
            }
            assert people != null;
            holder.nameTextView.setText(people.getName());
            holder.nameTextView.setTextColor(people.getColor());
            assert birthDate != null;
            boolean isLunar = people.getBirthCode() >= BirthUtil.SPECIAL_BIRTHDAY.LUNAR;
            int[] param = BirthUtil.diff(birthDate.getYear(), birthDate.getMonth(), birthDate.getDay(),
                    isLunar);
            Log.v("holder", Arrays.toString(param));
            holder.ageTextView.setText(String.valueOf(
                    BirthUtil.getAge(people.getYear(), people.getMonth(), people.getDay(),
                            birthDate.getYear(), birthDate.getMonth(), birthDate.getDay(), isLunar)));
            holder.alsoTextView.setText(param[3] < 0 ? "过了" : "还有");
            holder.yearTextView.setText(String.valueOf(param[0]));
            holder.monthTextView.setText(String.valueOf(param[1]));
            holder.dayTextView.setText(String.valueOf(param[2]));
            holder.itemView.setClickable(true);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, SchemeActivity.class);
                intent.putExtra(BirthApplication.PEOPLE, people);
                intent.putExtra(BirthApplication.BIRTH, birthDate);
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (isEmpty) return 1;
        return mValues == null ? birthDates.size() : mValues.size();
    }

    public static class BirthHolder extends RecyclerView.ViewHolder {
        public final TextView nameTextView;
        public final TextView ageTextView;
        public final TextView alsoTextView;
        public final TextView yearTextView;
        public final TextView monthTextView;
        public final TextView dayTextView;

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
