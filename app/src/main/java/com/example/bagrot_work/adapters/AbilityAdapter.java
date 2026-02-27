package com.example.bagrot_work.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
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
import com.example.bagrot_work.models.User;
import com.example.bagrot_work.screens.LevelActivity;
import com.example.bagrot_work.utils.SharedPreferencesUtil;

import java.util.List;

public class AbilityAdapter extends RecyclerView.Adapter<AbilityAdapter.ViewHolder> {

    private Context context;
    private List<Abilities> abilitiesList;
    private OnAbilityClickListener listener;
    private final User user;

    public interface OnAbilityClickListener {
        void onAbilityClick(Abilities ability);
    }

    public AbilityAdapter(Context context, List<Abilities> abilitiesList, OnAbilityClickListener listener, User user) {
        this.context = context;
        this.abilitiesList = abilitiesList;
        this.listener = listener;
        this.user = user;
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
        boolean userHaveAbility = user.isInAbilityList(ability);

        holder.abilityName.setText(ability.name);
        if(!userHaveAbility){
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

            holder.totalPrice.setText(user.getWalletBalance()+"/100");
        }
        else{
            switch (ability) {
                case doubleJump:
                    holder.abilityImage.setImageResource(R.drawable.doublejump);
                    holder.abilityImage.setImageTintMode(PorterDuff.Mode.DARKEN);
                    holder.totalPrice.setText("doubleJump");
                    break;
                case fastRun:
                    holder.abilityImage.setImageResource(R.drawable.race);
                    holder.abilityImage.setImageTintMode(PorterDuff.Mode.DARKEN);
                    holder.totalPrice.setText("fastRun");
                    break;
                default:
                    holder.abilityImage.setImageResource(R.drawable.user);
                    holder.abilityImage.setImageTintMode(PorterDuff.Mode.DARKEN);
                    holder.totalPrice.setText("default");
            }

            holder.totalPrice.setText("purchased!");
        }



        holder.use_btn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAbilityClick(ability);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return abilitiesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView abilityImage;
        ImageButton use_btn;
        TextView abilityName, totalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            abilityImage = itemView.findViewById(R.id.ability_image);
            use_btn = itemView.findViewById(R.id.use_btn);
            abilityName = itemView.findViewById(R.id.ability_name);
            totalPrice = itemView.findViewById(R.id.total_price);
        }
    }
}

