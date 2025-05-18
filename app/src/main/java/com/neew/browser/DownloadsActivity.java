package com.neew.browser;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DownloadsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDownloads;
    private TextView textViewNoDownloads;
    private DownloadsAdapter adapter;
    private List<DownloadItem> downloadsList = new ArrayList<>();

    private static final String PREFS_NAME = "downloads_prefs"; // Consistent prefs name
    private static final String PREF_DOWNLOADS = "downloads_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        Toolbar toolbar = findViewById(R.id.toolbar_downloads);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Downloads");
        }

        recyclerViewDownloads = findViewById(R.id.recyclerViewDownloads);
        textViewNoDownloads = findViewById(R.id.textViewNoDownloads);

        recyclerViewDownloads.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DownloadsAdapter(downloadsList, this);
        recyclerViewDownloads.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDownloads();
    }

    private void loadDownloads() {
        downloadsList.clear();
        downloadsList.addAll(getDownloads(this));
        adapter.notifyDataSetChanged();

        if (downloadsList.isEmpty()) {
            recyclerViewDownloads.setVisibility(View.GONE);
            textViewNoDownloads.setVisibility(View.VISIBLE);
        } else {
            recyclerViewDownloads.setVisibility(View.VISIBLE);
            textViewNoDownloads.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- Static DownloadItem and SharedPreferences logic ---
    static class DownloadItem {
        String fileName;
        String filePath;
        String sourceUrl;

        DownloadItem(String fileName, String filePath, String sourceUrl) {
            this.fileName = fileName;
            this.filePath = filePath;
            this.sourceUrl = sourceUrl;
        }

        JSONObject toJson() throws JSONException {
            JSONObject obj = new JSONObject();
            obj.put("fileName", fileName);
            obj.put("filePath", filePath);
            obj.put("sourceUrl", sourceUrl);
            return obj;
        }

        static DownloadItem fromJson(JSONObject obj) throws JSONException {
            return new DownloadItem(
                    obj.optString("fileName"),
                    obj.optString("filePath"),
                    obj.optString("sourceUrl")
            );
        }
    }

    public static List<DownloadItem> getDownloads(Context context) {
        ArrayList<DownloadItem> result = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(PREF_DOWNLOADS, null);
        if (json != null) {
            try {
                JSONArray arr = new JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    result.add(DownloadItem.fromJson(arr.getJSONObject(i)));
                }
            } catch (JSONException e) {
                android.util.Log.e("DownloadsActivity", "Error parsing downloads from JSON", e);
            }
        }
        return result;
    }

    public static void addDownload(Context context, DownloadItem item) {
        List<DownloadItem> downloads = getDownloads(context);
        downloads.add(0, item); // Add to top
        JSONArray arr = new JSONArray();
        for (DownloadItem d : downloads) {
            try {
                arr.put(d.toJson());
            } catch (JSONException e) {
                 android.util.Log.e("DownloadsActivity", "Error converting download item to JSON", e);
            }
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_DOWNLOADS, arr.toString()).apply();
    }

    // --- DownloadsAdapter ---
    private static class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadViewHolder> {
        private final List<DownloadItem> downloads;
        private final Context context;

        DownloadsAdapter(List<DownloadItem> downloads, Context context) {
            this.downloads = downloads;
            this.context = context;
        }

        @NonNull
        @Override
        public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
            return new DownloadViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
            DownloadItem item = downloads.get(position);
            holder.fileNameView.setText(item.fileName);
            holder.filePathView.setText(item.filePath);

            String url = item.sourceUrl;
            if (url != null && url.length() > 60) { // Slightly longer for activity view
                url = url.substring(0, 57) + "...";
            }
            holder.sourceUrlView.setText(url);

            holder.itemView.setOnClickListener(v -> openFile(item));
            holder.sourceUrlView.setOnClickListener(v -> openSourceUrl(item));
            holder.folderButton.setOnClickListener(v -> openFileLocation(item));
            holder.deleteButton.setOnClickListener(v -> deleteDownload(item, position));
        }
        
        private void deleteDownload(DownloadItem item, int position) {
            // 1. Remove from SharedPreferences
            List<DownloadItem> currentDownloads = getDownloads(context);
            currentDownloads.removeIf(d -> d.filePath != null && d.filePath.equals(item.filePath)); // Java 8+
            
            JSONArray arr = new JSONArray();
            for (DownloadItem d : currentDownloads) {
                try {
                    arr.put(d.toJson());
                } catch (JSONException e) {
                    android.util.Log.e("DownloadsAdapter", "Error converting download item to JSON for delete", e);
                }
            }
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(PREF_DOWNLOADS, arr.toString()).apply();

            // 2. Remove from current list and notify adapter
            if (position >= 0 && position < downloads.size()) {
                downloads.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, downloads.size());
                Toast.makeText(context, "Download deleted: " + item.fileName, Toast.LENGTH_SHORT).show();

                // Check if list is now empty
                if (downloads.isEmpty() && context instanceof DownloadsActivity) {
                    ((DownloadsActivity) context).showEmptyView(true);
                }
            }
        }


        private void openFile(DownloadItem item) {
            try {
                File file = new File(item.filePath);
                if (!file.exists()) {
                    Toast.makeText(context, "File not found: " + item.fileName, Toast.LENGTH_SHORT).show();
                    return;
                }
                // Use FileProvider for greater security and compatibility with API 24+
                Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, getMimeType(item.fileName));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Important if context is not activity

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "No app can open this file type.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                android.util.Log.e("DownloadsAdapter", "Error opening file: " + item.filePath, e);
                Toast.makeText(context, "Cannot open file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        private void openSourceUrl(DownloadItem item) {
            if (item.sourceUrl == null || item.sourceUrl.isEmpty()) {
                Toast.makeText(context, "Source URL is not available.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.sourceUrl));
                context.startActivity(intent);
            } catch (Exception e) {
                android.util.Log.e("DownloadsAdapter", "Error opening source URL: " + item.sourceUrl, e);
                Toast.makeText(context, "Cannot open URL.", Toast.LENGTH_SHORT).show();
            }
        }

        private void openFileLocation(DownloadItem item) {
            if (TextUtils.isEmpty(item.filePath)) {
                Toast.makeText(context, "File path is not available.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                File file = new File(item.filePath);
                if (!file.exists()) {
                    Toast.makeText(context, "File not found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Use FileProvider to get a content URI for the file itself
                Uri fileUri = FileProvider.getUriForFile(context, 
                                                        context.getApplicationContext().getPackageName() + ".provider", 
                                                        file);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                // Setting data and a generic type. Some file explorers might pick this up to show the location.
                // For a more direct "open folder" experience, a different approach might be needed if this isn't sufficient,
                // but that's often less standard across Android versions.
                intent.setDataAndType(fileUri, "*/*"); 
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Try to resolve and start activity
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "No app found to open file location.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                android.util.Log.e("DownloadsAdapter", "Error opening file location for: " + item.filePath, e);
                Toast.makeText(context, "Error opening location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public int getItemCount() {
            return downloads.size();
        }

        static class DownloadViewHolder extends RecyclerView.ViewHolder {
            TextView fileNameView, filePathView, sourceUrlView;
            ImageButton folderButton, deleteButton; // Added deleteButton

            DownloadViewHolder(@NonNull View itemView) {
                super(itemView);
                fileNameView = itemView.findViewById(R.id.textViewFileName);
                filePathView = itemView.findViewById(R.id.textViewFilePath);
                sourceUrlView = itemView.findViewById(R.id.textViewSourceUrl);
                folderButton = itemView.findViewById(R.id.buttonOpenFileLocation);
                deleteButton = itemView.findViewById(R.id.buttonDeleteDownload);
            }
        }

        private static String getMimeType(String fileName) {
            if (fileName == null) return "*/*";
            String ext = "";
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot >= 0 && lastDot < fileName.length() - 1) {
                ext = fileName.substring(lastDot + 1).toLowerCase();
            }
            
            if (ext.equals("pdf")) return "application/pdf";
            if (ext.equals("png")) return "image/png";
            if (ext.equals("jpg") || ext.equals("jpeg")) return "image/jpeg";
            if (ext.equals("gif")) return "image/gif";
            if (ext.equals("mp3")) return "audio/mpeg";
            if (ext.equals("wav")) return "audio/wav";
            if (ext.equals("mp4")) return "video/mp4";
            if (ext.equals("mov")) return "video/quicktime";
            if (ext.equals("avi")) return "video/x-msvideo";
            if (ext.equals("txt")) return "text/plain";
            if (ext.equals("html") || ext.equals("htm")) return "text/html";
            if (ext.equals("zip")) return "application/zip";
            if (ext.equals("apk")) return "application/vnd.android.package-archive";
            return "*/*";
        }
    }
     public void showEmptyView(boolean show) {
        if (show) {
            recyclerViewDownloads.setVisibility(View.GONE);
            textViewNoDownloads.setVisibility(View.VISIBLE);
        } else {
            recyclerViewDownloads.setVisibility(View.VISIBLE);
            textViewNoDownloads.setVisibility(View.GONE);
        }
    }
} 