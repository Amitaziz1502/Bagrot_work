package com.example.bagrot_work.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bagrot_work.R;
import com.example.bagrot_work.models.Abilities;

import java.util.List;

public class AbilityAdapter extends RecyclerView.Adapter<AbilityAdapter.ViewHolder> {

    private Context context;
    private List<Abilities> abilitiesList;
    private OnAbilityClickListener listener;

    public interface OnAbilityClickListener {
        void onAbilityClick(Abilities ability);
    }

    public AbilityAdapter(Context context, List<Abilities> abilitiesList, OnAbilityClickListener listener) {
        this.context = context;
        this.abilitiesList = abilitiesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemstore, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Abilities ability = abilitiesList.get(position);

        holder.abilityName.setText(ability.name);

        // תמונות לפי סוג היכולת
        switch (ability) {
            case doubleJump:
                holder.abilityImage.setImageResource(R.drawable.doublejump);
                holder.totalPrice.setText("doubleJump");
                break;
            case fastRun:
                holder.abilityImage.setImageResource(R.drawable.race);
                holder.totalPrice.setText("fastRun");
                break;
            default:
                holder.abilityImage.setImageResource(R.drawable.user);
                holder.totalPrice.setText("default");
        }

        holder.totalPrice.setText("100");

        holder.managerButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAbilityClick(ability);
            }
        });
    }

    @Override
    public int getItemCount() {
        return abilitiesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView abilityImage;
        ImageButton managerButton;
        TextView abilityName, totalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            abilityImage = itemView.findViewById(R.id.ability_image);
            managerButton = itemView.findViewById(R.id.manager);
            abilityName = itemView.findViewById(R.id.ability_name);
            totalPrice = itemView.findViewById(R.id.total_price);
        }
    }
}

