package com.example.bagrot_work.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bagrot_work.R;
import com.example.bagrot_work.models.User;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    public interface OnUserClickListener {
        void onUserClick(User user);
        void onLongUserClick(User user);

        void onTrashClick(User user);
        void onManagerClick(User user);
    }

    private final List<User> userList;
    private final OnUserClickListener onUserClickListener;
    public UserAdapter(@Nullable final OnUserClickListener onUserClickListener) {
        userList = new ArrayList<>();
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemuser, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null) return;

        holder.tvFullName.setText(user.getFirstname() + " " + user.getLastname());
        holder.tvUsername.setText(user.getUsername());
        holder.tvLevels.setText("Level " + user.getCurrentlevel());

        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onLongUserClick(user);
            }
            return true;
        });

        // Delete
        holder.btnTrash.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onTrashClick(user);
            }
        });

        // Manager
        holder.btnManager.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onManagerClick(user);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> users) {
        userList.clear();
        userList.addAll(users);
        notifyDataSetChanged();
    }

    public void addUser(User user) {
        userList.add(user);
        notifyItemInserted(userList.size() - 1);
    }
    public void updateUser(User user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.set(index, user);
        notifyItemChanged(index);
    }

    public void removeUser(User user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.remove(index);
        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvUsername, tvLevels;
        ImageButton btnTrash, btnManager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.txt_fullname);
            tvUsername = itemView.findViewById(R.id.txt_username);
            tvLevels = itemView.findViewById(R.id.txt_level);
            btnTrash = itemView.findViewById(R.id.trash);
            btnManager = itemView.findViewById(R.id.manager);

        }
    }
}