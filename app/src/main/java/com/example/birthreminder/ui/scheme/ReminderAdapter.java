package com.example.birthreminder.ui.scheme;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.camerash.toggleedittextview.ToggleEditButton;
import com.camerash.toggleedittextview.ToggleEditTextView;
import com.example.birthreminder.R;
import com.example.birthreminder.dao.AppDatabase;
import com.example.birthreminder.entity.Reminder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.birthreminder.application.BirthApplication.getContext;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderHolder> {
    private final List<Reminder> mValues;
    private final long birthID;
    private final long peopleID;
    AppDatabase appDatabase;

    public ReminderAdapter(List<Reminder> mValues, long peopleID, long birthID) {
        this.mValues = mValues;
        this.birthID = birthID;
        this.peopleID = peopleID;
        appDatabase = AppDatabase.getInstance(getContext());
    }

    public void addVoidItem() {
        Reminder reminder = new Reminder();
        reminder.setPeopleId(peopleID);
        reminder.setBirthDateId(birthID);
        mValues.add(reminder);
        notifyItemInserted(getItemCount());
        new Thread(() -> appDatabase.getReminderDao().insert(reminder)).start();
    }

    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scheme_item, parent, false);
        return new ReminderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ReminderAdapter.ReminderHolder holder, int position) {

        Reminder reminder = mValues.get(position);
        ToggleEditTextView beforeDaysText = holder.beforeDaysText;
        ToggleEditTextView contentText = holder.contentText;
        ToggleEditButton saveButton = holder.toggleEditButton;
        beforeDaysText.setText(String.valueOf(reminder.getBeforeDay()));
        contentText.setText(reminder.getContent());
        saveButton.bind(beforeDaysText, contentText);
        saveButton.setOnClickListener(v -> {
            Log.v("ETbt", String.valueOf(saveButton.getEditing()));
            if (!saveButton.getEditing()) {
                String content = contentText.getText().trim();
                int beforeDays;
                try {
                    beforeDays = Integer.parseInt(beforeDaysText.getText().trim());
                } catch (NumberFormatException e) {
                    beforeDays = 0;
                }
                if (beforeDays != reminder.getBeforeDay() || !content.equals(reminder.getContent())) {
                    reminder.setBeforeDay(beforeDays);
                    reminder.setContent(content);
                    new Thread(() -> appDatabase.getReminderDao().update(reminder)).start();
                }
            }
        });
        holder.imageView.setOnClickListener(v -> removeReminder(position, reminder));
    }


    private void removeReminder(int position, Reminder reminder) {
        mValues.remove(position);
        new Thread(() -> appDatabase.getReminderDao().delete(reminder)).start();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ReminderHolder extends RecyclerView.ViewHolder {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        public final ToggleEditButton toggleEditButton;
        public final ToggleEditTextView beforeDaysText;
        public final ToggleEditTextView contentText;
        public final ImageView imageView;


        public ReminderHolder(@NotNull View itemView) {
            super(itemView);
            beforeDaysText = itemView.findViewById(R.id.beforeDaysText);
            toggleEditButton = itemView.findViewById(R.id.toggleEditButton);
            contentText = itemView.findViewById(R.id.contentText);
            imageView = itemView.findViewById(R.id.deleteButton);
        }
    }
}
