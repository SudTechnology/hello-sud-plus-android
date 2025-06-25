package tech.sud.mgp.hello.common.utils;

import com.blankj.utilcode.util.ThreadUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class FileUploader {

    private final OkHttpClient client;

    public FileUploader() {
        client = new OkHttpClient.Builder().build();
    }

    public interface UploadListener {
        void onProgress(long bytesWritten, long totalBytes);

        void onSuccess(Response response);

        void onFailure(IOException e);
    }

    public UploadTask uploadFile(String url, String filePath, UploadListener listener) {
        File file = new File(filePath);
        if (!file.exists()) {
            listener.onFailure(new IOException("File not found: " + filePath));
            return null;
        }

        RequestBody fileBody = new ProgressRequestBody(file, "application/octet-stream", listener);

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ThreadUtils.runOnUiThread(() -> {
                    listener.onFailure(e);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ThreadUtils.runOnUiThread(() -> {
                    listener.onSuccess(response);
                });
            }
        });

        return new UploadTask(call);
    }

    public static class UploadTask {
        private final Call call;

        public UploadTask(Call call) {
            this.call = call;
        }

        public void cancel() {
            call.cancel();
        }

        public boolean isCanceled() {
            return call.isCanceled();
        }
    }

    static class ProgressRequestBody extends RequestBody {

        private final File file;
        private final String contentType;
        private final UploadListener listener;
        private static final int SEGMENT_SIZE = 2048;

        public ProgressRequestBody(File file, String contentType, UploadListener listener) {
            this.file = file;
            this.contentType = contentType;
            this.listener = listener;
        }

        @Override
        public long contentLength() throws IOException {
            return file.length();
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse(contentType);
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            long fileLength = file.length();
            long uploaded = 0;

            try (okio.Source source = Okio.source(file)) {
                long read;
                while ((read = source.read(sink.getBuffer(), SEGMENT_SIZE)) != -1) {
                    uploaded += read;
                    sink.flush();
                    listener.onProgress(uploaded, fileLength);
                }
            }
        }

    }
}

