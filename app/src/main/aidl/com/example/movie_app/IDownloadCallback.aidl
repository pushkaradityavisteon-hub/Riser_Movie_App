package com.example.movie_app;

oneway interface IDownloadCallback {
    void onProgress(int movieId, int percent);
    void onComplete(int movieId, String localPath);
    void onFailed(int movieId, String reason);
}
