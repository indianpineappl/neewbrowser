package com.neew.browser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
// Add imports for bitmap decoding
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log; // For logging errors

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.TabViewHolder> {

    private final List<String> tabUrls;
    private final List<String> tabSnapshotStrings; // Add list for snapshot strings
    private final int activeTabIndex;
    private final OnTabClickListener tabClickListener;
    private final OnTabCloseListener tabCloseListener;

    // Interfaces for click handling
    public interface OnTabClickListener {
        void onTabClick(int position);
    }
    public interface OnTabCloseListener {
        void onTabClose(int position);
    }

    public TabAdapter(List<String> tabUrls, List<String> snapshotStrings, int activeTabIndex, 
                      OnTabClickListener clickListener, OnTabCloseListener closeListener) {
        this.tabUrls = tabUrls;
        this.tabSnapshotStrings = snapshotStrings;
        this.activeTabIndex = activeTabIndex;
        this.tabClickListener = clickListener;
        this.tabCloseListener = closeListener;
    }

    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tab, parent, false);
        return new TabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {
        String url = tabUrls.get(position);
        String snapshotString = (position < tabSnapshotStrings.size()) ? tabSnapshotStrings.get(position) : null;
        holder.bind(url, snapshotString, position == activeTabIndex);

        holder.itemView.setOnClickListener(v -> {
             if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) { 
                 tabClickListener.onTabClick(holder.getAdapterPosition());
             }
        });
        holder.closeButton.setOnClickListener(v -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) { 
                 tabCloseListener.onTabClose(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tabUrls.size();
    }

    // ViewHolder class
    static class TabViewHolder extends RecyclerView.ViewHolder {
        ImageView previewImageView;
        TextView urlTextView;
        ImageButton closeButton;

        TabViewHolder(@NonNull View itemView) {
            super(itemView);
            previewImageView = itemView.findViewById(R.id.tabPreviewImageView);
            urlTextView = itemView.findViewById(R.id.tabUrlTextView);
            closeButton = itemView.findViewById(R.id.closeTabButton);
        }

        void bind(String url, String snapshotString, boolean isActive) {
            urlTextView.setText(url != null ? url : ""); // Handle potential null URL
            
            // Decode and set snapshot if available
            if (snapshotString != null && !snapshotString.isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(snapshotString, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (decodedByte != null) {
                        previewImageView.setImageBitmap(decodedByte);
                        previewImageView.setBackgroundColor(0x00000000); // Remove placeholder background
                    } else {
                        // Handle decoding failure - show placeholder
                        Log.w("TabAdapter", "Failed to decode bitmap for URL: " + url);
                        previewImageView.setImageResource(0); // Clear previous image
                         previewImageView.setBackgroundColor(0xFF555555); // Restore placeholder background
                    }
                } catch (IllegalArgumentException e) {
                    Log.e("TabAdapter", "Base64 decoding failed for URL: " + url, e);
                    previewImageView.setImageResource(0);
                    previewImageView.setBackgroundColor(0xFF555555);
                }
            } else {
                // No snapshot string - show placeholder
                previewImageView.setImageResource(0); 
                previewImageView.setBackgroundColor(0xFF555555);
            }
            
            // Active tab indication (optional - adjust as needed)
            if (isActive) {
                 // Example: Add a border or change overall background slightly
                 ((androidx.cardview.widget.CardView)itemView).setCardBackgroundColor(0xFF444444);
            } else {
                 ((androidx.cardview.widget.CardView)itemView).setCardBackgroundColor(0xFF333333); 
            }
        }
    }
} 