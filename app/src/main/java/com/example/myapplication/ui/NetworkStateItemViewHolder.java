package com.example.myapplication.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.NetworkStateItemBinding;

public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {
    private final NetworkStateItemBinding binding = NetworkStateItemBinding.bind(itemView);
    private final ProgressBar progressBar = binding.progressBar;
    private final TextView errorMsg = binding.errorMsg;
    private final Button retry = binding.retryButton;

    public NetworkStateItemViewHolder( ViewGroup parent,
                                       RetryCallback retryCallback) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.network_state_item, parent, false));
        retry.setOnClickListener(view -> retryCallback.retryCallback());
    }

    public void bindTo(LoadState loadState) {
        if (loadState instanceof LoadState.Loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }

        if (loadState instanceof LoadState.Error) {
            retry.setVisibility(View.VISIBLE);
        } else {
            retry.setVisibility(View.INVISIBLE);
        }
        if (loadState instanceof LoadState.Error
                && ((LoadState.Error) loadState).getError().getMessage() != null
                && ((LoadState.Error) loadState).getError().getMessage().isEmpty()){
            errorMsg.setVisibility(View.VISIBLE);
            errorMsg.setText(((LoadState.Error) loadState).getError().getMessage());
        } else {
            errorMsg.setVisibility(View.INVISIBLE);
        }
    }
}
