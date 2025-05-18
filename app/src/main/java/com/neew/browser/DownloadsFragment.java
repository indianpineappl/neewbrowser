package com.neew.browser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadsFragment extends Fragment {
    // Download item model
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
                obj.getString("fileName"),
                obj.getString("filePath"),
                obj.getString("sourceUrl")
            );
        }
    }

    private static final String PREF_DOWNLOADS = "downloads_list";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        List<DownloadItem> downloads = getDownloads(requireContext());
        if (downloads.isEmpty()) {
            TextView emptyView = new TextView(requireContext());
            emptyView.setText("No downloads yet.");
            emptyView.setTextSize(18);
            emptyView.setPadding(40, 80, 40, 40);
            emptyView.setGravity(android.view.Gravity.CENTER);
            return emptyView;
        }
        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new DownloadsAdapter(downloads, requireContext()));
        return recyclerView;
    }

    // Persistent storage helpers
    public static List<DownloadItem> getDownloads(Context context) {
        ArrayList<DownloadItem> result = new ArrayList<>();
        String json = context.getSharedPreferences("downloads", Context.MODE_PRIVATE).getString(PREF_DOWNLOADS, null);
        if (json != null) {
            try {
                JSONArray arr = new JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    result.add(DownloadItem.fromJson(arr.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }
        }
        context.getSharedPreferences("downloads", Context.MODE_PRIVATE)
            .edit().putString(PREF_DOWNLOADS, arr.toString()).apply();
    }

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
            LinearLayout layout = new LinearLayout(parent.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(40, 40, 40, 40);

            TextView fileNameView = new TextView(parent.getContext());
            fileNameView.setTextSize(18);
            fileNameView.setEllipsize(TextUtils.TruncateAt.END);
            fileNameView.setMaxLines(1);
            layout.addView(fileNameView);

            TextView filePathView = new TextView(parent.getContext());
            filePathView.setTextSize(14);
            filePathView.setTextColor(0xFF888888);
            layout.addView(filePathView);

            TextView sourceUrlView = new TextView(parent.getContext());
            sourceUrlView.setTextSize(14);
            sourceUrlView.setTextColor(0xFF2196F3); // blue
            sourceUrlView.setEllipsize(TextUtils.TruncateAt.END);
            sourceUrlView.setMaxLines(1);
            layout.addView(sourceUrlView);

            // Folder icon/button
            ImageButton folderButton = new ImageButton(parent.getContext());
            folderButton.setImageResource(android.R.drawable.ic_menu_manage); // Use a system folder icon for now
            folderButton.setBackgroundColor(0x00000000); // Transparent
            folderButton.setPadding(0, 20, 0, 0);
            layout.addView(folderButton);

            return new DownloadViewHolder(layout, fileNameView, filePathView, sourceUrlView, folderButton);
        }
        @Override
        public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
            DownloadItem item = downloads.get(position);
            holder.fileNameView.setText(item.fileName);
            holder.filePathView.setText(item.filePath);
            // Truncate source URL if too long
            String url = item.sourceUrl;
            if (url.length() > 40) {
                url = url.substring(0, 37) + "...";
            }
            holder.sourceUrlView.setText(url);

            // Click: open file
            holder.fileNameView.setOnClickListener(v -> {
                try {
                    File file = new File(item.filePath);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(file);
                    intent.setDataAndType(uri, getMimeType(item.fileName));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Cannot open file", Toast.LENGTH_SHORT).show();
                }
            });
            // Click: open source URL
            holder.sourceUrlView.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.sourceUrl));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show();
                }
            });
            // Click: open folder
            holder.folderButton.setOnClickListener(v -> {
                try {
                    File file = new File(item.filePath);
                    File folder = file.getParentFile();
                    if (folder != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(folder);
                        intent.setDataAndType(uri, "resource/folder");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Folder not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Cannot open folder", Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public int getItemCount() {
            return downloads.size();
        }
        static class DownloadViewHolder extends RecyclerView.ViewHolder {
            TextView fileNameView, filePathView, sourceUrlView;
            ImageButton folderButton;
            DownloadViewHolder(@NonNull View itemView, TextView fileNameView, TextView filePathView, TextView sourceUrlView, ImageButton folderButton) {
                super(itemView);
                this.fileNameView = fileNameView;
                this.filePathView = filePathView;
                this.sourceUrlView = sourceUrlView;
                this.folderButton = folderButton;
            }
        }
        // Helper to guess MIME type from file name
        private static String getMimeType(String fileName) {
            String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            if (ext.equals("pdf")) return "application/pdf";
            if (ext.equals("png")) return "image/png";
            if (ext.equals("jpg") || ext.equals("jpeg")) return "image/jpeg";
            if (ext.equals("mp3")) return "audio/mpeg";
            if (ext.equals("mp4")) return "video/mp4";
            if (ext.equals("txt")) return "text/plain";
            return "*/*";
        }
    }
} 