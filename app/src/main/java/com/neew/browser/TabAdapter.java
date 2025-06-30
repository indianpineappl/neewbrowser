package com.neew.browser;

import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.view.KeyEvent; // For KeyEvent handling
import androidx.recyclerview.widget.GridLayoutManager; // For LayoutManager access
import android.content.Context;
import android.content.pm.PackageManager;

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.TabViewHolder> {

    private final List<String> tabUrls;
    private final List<String> tabSnapshotStrings; // Add list for snapshot strings
    private final int activeTabIndex;
    private final OnTabClickListener tabClickListener;
    private final OnTabCloseListener tabCloseListener;
    private final RecyclerView parentRecyclerView; // Reference to the RecyclerView
    private final Context mContext; // To check device type

    // Interfaces for click handling
    public interface OnTabClickListener {
        void onTabClick(int position);
    }
    public interface OnTabCloseListener {
        void onTabClose(int position);
    }

    public TabAdapter(List<String> tabUrls, List<String> snapshotStrings, int activeTabIndex, 
                      OnTabClickListener clickListener, OnTabCloseListener closeListener,
                      RecyclerView recyclerView) { // Add RecyclerView parameter
        this.tabUrls = tabUrls;
        this.tabSnapshotStrings = snapshotStrings;
        this.activeTabIndex = activeTabIndex;
        this.tabClickListener = clickListener;
        this.tabCloseListener = closeListener;
        this.parentRecyclerView = recyclerView; // Initialize RecyclerView reference
        this.mContext = recyclerView.getContext();
    }

    private boolean isTvDevice() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK);
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

        // Set RecyclerView reference in ViewHolder
        holder.setParentRecyclerView(parentRecyclerView);

        // --- DUAL INPUT HANDLING ---
        // 1. Set standard OnClickListeners for touch devices
        holder.itemView.setOnClickListener(v -> {
            if (tabClickListener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                tabClickListener.onTabClick(holder.getAdapterPosition());
            }
        });

        holder.closeButton.setOnClickListener(v -> {
            if (tabCloseListener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                tabCloseListener.onTabClose(holder.getAdapterPosition());
            }
        });

        // 2. Set OnKeyListeners for TV D-pad navigation
        if (isTvDevice()) {
            // Create a hover listener to provide visual feedback for the TV cursor.
            View.OnHoverListener hoverListener = (v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        // Make the view slightly transparent to indicate hover
                        v.setAlpha(0.7f);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        // Restore original appearance
                        v.setAlpha(1.0f);
                        break;
                }
                return false; // Allow other listeners to process the event
            };

            // Apply the listener to the whole tab and the close button
            holder.itemView.setOnHoverListener(hoverListener);
            holder.closeButton.setOnHoverListener(hoverListener);

        holder.itemView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.d("TabAdapter", "ItemView KeyDown: " + keyCode + " Pos: " + holder.getAdapterPosition());

                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    // Select the tab
                    Log.d("TabAdapter", "DPAD_CENTER/ENTER on tab: " + holder.getAdapterPosition());
             if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) { 
                 tabClickListener.onTabClick(holder.getAdapterPosition());
             }
                    return true; // Consume the event
                } else if (keyCode == holder.lastDpadDirection && holder.lastFocusedDirectionalView == holder.itemView) {
                    // Second consecutive directional press on the same tab
                    Log.d("TabAdapter", "Second consecutive DPAD press on tab, moving focus to close button.");
                    holder.closeButton.requestFocus();
                    holder.lastDpadDirection = 0; // Reset for next interaction
                    holder.lastFocusedDirectionalView = null;
                    return true; // Consume the event
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
                           keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    // First directional press, or a new direction/tab - let RecyclerView handle focus movement
                    Log.d("TabAdapter", "First/new directional DPAD press on tab, letting RecyclerView handle.");
                    holder.lastDpadDirection = keyCode;
                    holder.lastFocusedDirectionalView = holder.itemView;
                    return false; // Do not consume, allow RecyclerView to move focus
                }
            }
            return false; // Don't consume other events
        });

        holder.closeButton.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.d("TabAdapter", "CloseButton KeyDown: " + keyCode + " Pos: " + holder.getAdapterPosition());

                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    // Close the tab
                    Log.d("TabAdapter", "DPAD_CENTER/ENTER on close button: " + holder.getAdapterPosition());
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) { 
                 tabCloseListener.onTabClose(holder.getAdapterPosition());
            }
                    return true; // Consume the event
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
                           keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    // Navigate out of the close button to the next tab
                    Log.d("TabAdapter", "Directional DPAD on close button, moving focus to next tab.");
                    holder.lastDpadDirection = 0; // Reset acceleration state
                    holder.lastFocusedDirectionalView = null;

                    if (holder.parentRecyclerView != null) {
                        RecyclerView.LayoutManager layoutManager = holder.parentRecyclerView.getLayoutManager();
                        if (layoutManager instanceof GridLayoutManager) {
                            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                            int spanCount = gridLayoutManager.getSpanCount();
                            int currentPosition = holder.getAdapterPosition();
                            int nextPosition = -1;

                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                    nextPosition = currentPosition - 1;
                                    break;
                                case KeyEvent.KEYCODE_DPAD_RIGHT:
                                    nextPosition = currentPosition + 1;
                                    break;
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    nextPosition = currentPosition - spanCount;
                                    break;
                                case KeyEvent.KEYCODE_DPAD_DOWN:
                                    nextPosition = currentPosition + spanCount;
                                    break;
                            }

                            if (nextPosition >= 0 && nextPosition < getItemCount()) {
                                // Find the ViewHolder for the next position and request focus on its itemView
                                RecyclerView.ViewHolder nextViewHolder = holder.parentRecyclerView.findViewHolderForAdapterPosition(nextPosition);
                                if (nextViewHolder != null) {
                                    nextViewHolder.itemView.requestFocus();
                                    return true; // Consume if focus moved successfully
                                } else {
                                    // If ViewHolder is null (not visible), scroll to it and then request focus
                                    holder.parentRecyclerView.scrollToPosition(nextPosition);
                                    // Request focus after scroll is complete (post is a simple way, might need ViewTreeObserver for reliability)
                                    final int finalNextPosition = nextPosition;
                                    holder.parentRecyclerView.post(() -> {
                                        RecyclerView.ViewHolder vh = holder.parentRecyclerView.findViewHolderForAdapterPosition(finalNextPosition);
                                        if (vh != null) {
                                            vh.itemView.requestFocus();
                                        }
                                    });
                                    return true; // Consume, as we're handling the navigation
                                }
                            }
                        }
                    }
                    return false; // If no custom handling, let system try (e.g. for Add/Clear buttons)
                }
            }
            return false; // Don't consume other events
        });
        }
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
        int lastDpadDirection = 0; // Track last directional D-pad key pressed
        View lastFocusedDirectionalView = null; // Track last view focused by directional D-pad
        RecyclerView parentRecyclerView; // Reference to the parent RecyclerView

        TabViewHolder(@NonNull View itemView) {
            super(itemView);
            previewImageView = itemView.findViewById(R.id.tabPreviewImageView);
            urlTextView = itemView.findViewById(R.id.tabUrlTextView);
            closeButton = itemView.findViewById(R.id.closeTabButton);
        }

        void setParentRecyclerView(RecyclerView recyclerView) {
            this.parentRecyclerView = recyclerView;
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