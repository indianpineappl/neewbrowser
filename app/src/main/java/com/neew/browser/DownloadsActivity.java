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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;

public class DownloadsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDownloads;
    private TextView textViewNoDownloads;
    private DownloadsAdapter adapter;
    private List<DownloadItem> downloadsList = new ArrayList<>();
    
    private final android.os.Handler handler = new android.os.Handler();
    private Runnable refreshRunnable;
    
    private TorrentService torrentService;
    private boolean isBound = false;
    
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            TorrentService.LocalBinder binder = (TorrentService.LocalBinder) service;
            torrentService = binder.getService();
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

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
        
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                updateDownloadProgress();
                handler.postDelayed(this, 1000);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, TorrentService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    // Extract the info-hash from a magnet URI (xt=urn:btih:...)
    private static String extractMagnetInfoHash(String magnet) {
        if (magnet == null) return null;
        try {
            int xtIndex = magnet.indexOf("xt=urn:btih:");
            if (xtIndex == -1) return null;
            int start = xtIndex + "xt=urn:btih:".length();
            int end = magnet.indexOf('&', start);
            if (end == -1) end = magnet.length();
            if (start >= end) return null;
            return magnet.substring(start, end);
        } catch (Exception e) {
            android.util.Log.w("DownloadsActivity", "Failed to parse magnet info-hash", e);
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDownloads();
        handler.post(refreshRunnable);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
    }
    
    private void updateDownloadProgress() {
        boolean changed = false;
        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        // We continue even if dm is null to handle torrents

        for (DownloadItem item : downloadsList) {
            // Handle Torrents
            if (item.isTorrent) {
                 if (isBound && torrentService != null) {
                     String currentMagnet = torrentService.getCurrentMagnetUrl();
                     
                     // Debug Log
                     if (currentMagnet != null) {
                        android.util.Log.d("DownloadsActivity", "Active: " + currentMagnet.substring(0, Math.min(20, currentMagnet.length())) + "...");
                        android.util.Log.d("DownloadsActivity", "Checking Item: " + (item.sourceUrl != null ? item.sourceUrl.substring(0, Math.min(20, item.sourceUrl.length())) : "null"));
                     }

                     boolean isMatch = false;
                     if (currentMagnet != null && item.sourceUrl != null) {
                         // Prefer matching on magnet info-hash (xt=urn:btih:...) to ignore tracker differences
                         String activeHash = extractMagnetInfoHash(currentMagnet);
                         String itemHash = extractMagnetInfoHash(item.sourceUrl);
                         if (!TextUtils.isEmpty(activeHash) && !TextUtils.isEmpty(itemHash)) {
                             isMatch = activeHash.equalsIgnoreCase(itemHash);
                         } else {
                             // Fallback to full string comparison (handles file:// URIs, etc.)
                             isMatch = currentMagnet.equals(item.sourceUrl);
                         }
                     }

                     if (isMatch) {
                         // Update Metadata (Filename & Path) once available
                         File videoFile = torrentService.getCurrentVideoFile();
                         if (videoFile != null) {
                             boolean needsSave = false;
                             if (TextUtils.isEmpty(item.filePath)) {
                                 item.filePath = videoFile.getAbsolutePath();
                                 needsSave = true;
                             }
                             // Update name if it's the default placeholder
                             if ("Torrent Download".equals(item.fileName) && videoFile.getName() != null) {
                                 item.fileName = videoFile.getName();
                                 needsSave = true;
                             }
                             
                             if (needsSave) {
                                 saveDownloadsToPrefs();
                                 changed = true;
                             }
                         }

                         if (torrentService.hasLastStatus()) {
                            float progress = torrentService.getLastProgress();
                            long speedBytes = torrentService.getLastDownloadSpeedBytes();
                            item.status = DownloadManager.STATUS_RUNNING;
                            item.progress = (int) progress; // 0-100 scale
                            
                            float speedKb = speedBytes / 1024f;
                            String speedStr = (speedKb > 1024) ? String.format("%.1f MB/s", speedKb / 1024f) : String.format("%.0f KB/s", speedKb);
                            item.statusText = String.format("%.1f%% | %s", progress, speedStr);
                            
                            changed = true;
                        } else {
                             // Status is null, meaning it's initializing
                             item.status = DownloadManager.STATUS_RUNNING;
                             item.statusText = (videoFile != null) ? "Connecting..." : "Fetching Metadata...";
                             changed = true;
                         }
                     } else {
                         // Not active torrent. Check if file exists -> Success?
                         // Or if it was just stopped.
                         // For now, if file exists, mark successful.
                         if (item.filePath != null && new File(item.filePath).exists()) {
                             // Assuming completed if not running? 
                             // But maybe it's just paused/stopped incomplete.
                             // Without metadata we don't know if incomplete.
                             // Leave status as is (likely -1 or previous).
                             // Or mark PAUSED if file exists but not fully downloaded?
                             // We don't have progress persistence for stopped torrents in Service yet.
                         }
                     }
                 }
                 continue; // Skip DM logic for torrents
            }

            // Handle Normal Downloads
            if (dm != null && item.downloadId != -1 && item.status != DownloadManager.STATUS_SUCCESSFUL && item.status != DownloadManager.STATUS_FAILED) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(item.downloadId);
                try (android.database.Cursor c = dm.query(query)) {
                    if (c != null && c.moveToFirst()) {
                        int statusIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                        int totalIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                        
                        if (statusIndex != -1) item.status = c.getInt(statusIndex);
                        
                        if (downloadedIndex != -1 && totalIndex != -1) {
                            long downloaded = c.getLong(downloadedIndex);
                            long total = c.getLong(totalIndex);
                            if (total > 0) {
                                item.progress = (int) ((downloaded * 100) / total);
                                item.statusText = String.format("%d%% (%s / %s)", item.progress, 
                                        android.text.format.Formatter.formatFileSize(this, downloaded),
                                        android.text.format.Formatter.formatFileSize(this, total));
                            } else {
                                item.progress = 0;
                                item.statusText = android.text.format.Formatter.formatFileSize(this, downloaded);
                            }
                        }
                        changed = true;
                    } else {
                         // ID exists but not found in DM. It might be cancelled or completed and cleared.
                         // We don't update status here to avoid hiding it if it was just completed.
                         // Or we can mark it as failed/unknown?
                    }
                } catch (Exception e) {
                    android.util.Log.e("DownloadsActivity", "Error querying download progress", e);
                }
            }
        }
        if (changed) {
            adapter.notifyDataSetChanged();
        }
    }

    void saveDownloadsToPrefs() {
        JSONArray arr = new JSONArray();
        for (DownloadItem d : downloadsList) {
            try {
                arr.put(d.toJson());
            } catch (JSONException e) {
                 android.util.Log.e("DownloadsActivity", "Error converting download item to JSON", e);
            }
        }
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_DOWNLOADS, arr.toString()).apply();
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
        long downloadId = -1;
        boolean isTorrent = false;
        
        // Transient UI state
        int progress = 0;
        int status = -1;
        String statusText = null; // Added for UI text

        DownloadItem(String fileName, String filePath, String sourceUrl) {
            this(fileName, filePath, sourceUrl, -1, false);
        }
        
        DownloadItem(String fileName, String filePath, String sourceUrl, long downloadId) {
            this(fileName, filePath, sourceUrl, downloadId, false);
        }

        DownloadItem(String fileName, String filePath, String sourceUrl, long downloadId, boolean isTorrent) {
            this.fileName = fileName;
            this.filePath = filePath;
            this.sourceUrl = sourceUrl;
            this.downloadId = downloadId;
            this.isTorrent = isTorrent;
        }

        JSONObject toJson() throws JSONException {
            JSONObject obj = new JSONObject();
            obj.put("fileName", fileName);
            obj.put("filePath", filePath);
            obj.put("sourceUrl", sourceUrl);
            obj.put("downloadId", downloadId);
            obj.put("isTorrent", isTorrent);
            return obj;
        }

        static DownloadItem fromJson(JSONObject obj) throws JSONException {
            return new DownloadItem(
                    obj.optString("fileName"),
                    obj.optString("filePath"),
                    obj.optString("sourceUrl"),
                    obj.optLong("downloadId", -1),
                    obj.optBoolean("isTorrent", false)
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
            
            // Progress Bar Logic
            boolean isActive = (item.status == DownloadManager.STATUS_RUNNING || 
                                item.status == DownloadManager.STATUS_PENDING || 
                                item.status == DownloadManager.STATUS_PAUSED);

            if (isActive) {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.textViewStatus.setVisibility(View.VISIBLE);
                
                if (item.statusText != null) {
                    holder.textViewStatus.setText(item.statusText);
                } else {
                    holder.textViewStatus.setText(item.progress + "%");
                }

                if (item.status == DownloadManager.STATUS_PENDING) {
                    holder.progressBar.setIndeterminate(true);
                } else {
                    holder.progressBar.setIndeterminate(false);
                    holder.progressBar.setProgress(item.progress);
                }
            } else {
                holder.progressBar.setVisibility(View.GONE);
                holder.textViewStatus.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> openFile(item));
            holder.sourceUrlView.setOnClickListener(v -> openSourceUrl(item));
            holder.folderButton.setOnClickListener(v -> openFileLocation(item));
            holder.deleteButton.setOnClickListener(v -> deleteDownload(item, position));
        }
        
        private void deleteDownload(DownloadItem item, int position) {
            // 1. Remove from SharedPreferences
            List<DownloadItem> currentDownloads = getDownloads(context);
            Iterator<DownloadItem> iterator = currentDownloads.iterator();
            while (iterator.hasNext()) {
                DownloadItem d = iterator.next();
                if (d.filePath != null && d.filePath.equals(item.filePath)) {
                    iterator.remove();
                }
            }
            
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
                if (item.filePath == null) {
                    Toast.makeText(context, "File path missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                File file = new File(item.filePath);
                if (!file.exists()) {
                    // Try fallback to Public Downloads for torrents (moved after completion)
                    boolean recovered = false;
                    if (item.isTorrent && item.fileName != null) {
                         File publicDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS);
                         File publicFile = new File(publicDir, item.fileName);
                         if (publicFile.exists()) {
                             item.filePath = publicFile.getAbsolutePath();
                             file = publicFile;
                             recovered = true;
                             // Persist the new path
                             if (context instanceof DownloadsActivity) {
                                 ((DownloadsActivity) context).saveDownloadsToPrefs();
                             }
                         }
                    }
                    
                    if (!recovered) {
                        Toast.makeText(context, "File not found at: " + item.filePath, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                String mimeType = getMimeType(item.fileName);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, mimeType);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Unable to open file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            TextView fileNameView, filePathView, sourceUrlView, textViewStatus;
            ImageButton folderButton, deleteButton; // Added deleteButton
            android.widget.ProgressBar progressBar;

            DownloadViewHolder(@NonNull View itemView) {
                super(itemView);
                fileNameView = itemView.findViewById(R.id.textViewFileName);
                filePathView = itemView.findViewById(R.id.textViewFilePath);
                sourceUrlView = itemView.findViewById(R.id.textViewSourceUrl);
                textViewStatus = itemView.findViewById(R.id.textViewStatus);
                folderButton = itemView.findViewById(R.id.buttonOpenFileLocation);
                deleteButton = itemView.findViewById(R.id.buttonDeleteDownload);
                progressBar = itemView.findViewById(R.id.progressBarDownload);
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