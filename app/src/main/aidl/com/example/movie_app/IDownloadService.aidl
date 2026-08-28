package com.example.movie_app;

import com.example.movie_app.IDownloadCallback;

interface IDownloadService {
    void downloadPoster(int movieId, String posterUrl, String title);
    void cancelDownload(int movieId);
    boolean isDownloaded(int movieId);
    String getLocalPath(int movieId);
    void registerCallback(IDownloadCallback callback);
    void unregisterCallback(IDownloadCallback callback);
}
