package com.meijun.weextest;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "ExampleInstrumentedTest";
    String baseUrl = "http://192.168.248.220:9080/pansoftdfs/";
    String uploadUrl = "files/file/upload";
    String addUrl = "files/dir/add";
    String queryUrl = "files/dir/66661/nextlevel/query";
    String deleteUrl = "files/dir/%s/delete";

    String downurl = "post /v2/files/%s/download ";
    final String delUrl = String.format(deleteUrl, "b686b660-6ca8-4ad9-9e7f-7137615e2a1b");

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        File file = new File("/sdcard/test.xz");
        Map<String, String> params = new HashMap<>();
        params.put("fileName ","test.xz");
        params.put("createUser ","66661");
        params.put("parentId  ","66661");
        params.put("createUser ","66661");
        params.put("createUser ","66661");
        uploadFile(file, baseUrl + uploadUrl, params, new ProgressListener() {
            @Override
            public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                Log.e(TAG, "onProgress: total "+totalBytes + " remain: "  + remainingBytes + " is done " + done);
            }
        });
    }

    /*   new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "gson-2.2.1.jar")//
       {
           @Override
           public void inProgress(float progress)
           {
               mProgressBar.setProgress((int) (100 * progress));
           }

           @Override
           public void onError(Request request, Exception e)
           {
               Log.e(TAG, "onError :" + e.getMessage());
           }

           @Override
           public void onResponse(File file)
           {
               Log.e(TAG, "onResponse :" + file.getAbsolutePath());
           }
       }*/
    public static void downFile(String path, Callback callback) {

        try {
            OkHttpUtils
                    .post()//
                    .url(path)//
                    .build()//
                    .execute(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 上传文件
     *
     * @param fileData   文件二进制流
     * @param fileName   文件名称，标明文件格式
     * @param createUser 文件拥有者
     * @param parentId   文件所在的目录
     * @param success    返回请求成功的block
     * @param failure    返回请求失败的block
     */
//    public void cloudDiskUploadFileWithFileDataL();
    private static void uploadFile(File file, String uploadPath, Map<String, String> params, ProgressListener listener) {

        if (file == null || !file.exists()) {
            throw new RuntimeException("文件不存在");
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);


        RequestBody customRequestBody = createCustomRequestBody(MultipartBuilder.FORM, file, listener);
        builder.addFormDataPart("file", file.getName(), customRequestBody);
        if (params != null) {

            Set<Map.Entry<String, String>> entries = params.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                builder.addFormDataPart(next.getKey(), next.getValue());
            }
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(uploadPath) //地址
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("response.body().string() = " + response.body().string());

            }
        });
    }

    public static RequestBody createCustomRequestBody(final MediaType contentType, final File file, final ProgressListener listener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    interface ProgressListener {
        void onProgress(long totalBytes, long remainingBytes, boolean done);
    }


}
