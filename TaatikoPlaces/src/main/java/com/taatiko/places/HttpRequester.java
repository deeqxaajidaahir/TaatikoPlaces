package com.taatiko.places;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;

public class HttpRequester {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_TYPE = "application/json;charset=UTF-8";
    private static final int CONNECTION_TIMEOUT = 60000; //milliseconds
    private static final int DATA_RETRIEVAL_TIMEOUT = 50000; //milliseconds
    private static final String TAG = "HttpReuester";
    private JSONObject jsonObject;
    private AsyncTaskCompleteListener asyncTaskCompleteListener;
    private int serviceCode;
    private int statusCode;
    private Context context;

    private String requestType;

    public HttpRequester(Context context, String webServiceUrl, JSONObject jsonObject, int
            serviceCode,
                         AsyncTaskCompleteListener asyncTaskCompleteListener
            , String requestType) {

        this.context = context;
        String url = webServiceUrl;
        this.jsonObject = jsonObject;
        this.serviceCode = serviceCode;
        this.requestType = requestType;

        if (TaatikoLog.isInternetConnected(context)) {
            this.asyncTaskCompleteListener = asyncTaskCompleteListener;
            new AsyncHttpTask().executeOnExecutor(Executors
                    .newSingleThreadExecutor(), url);
        }
    }

    private class AsyncHttpTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            try {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                URL serviceUrl = new URL(urls[0]);
                TaatikoLog.Log(TAG, urls[0]);
                urlConnection = (HttpURLConnection) serviceUrl.openConnection();
                urlConnection.setReadTimeout(DATA_RETRIEVAL_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);

                // add package name to request header
                urlConnection.setRequestProperty("X-Android-Package", context.getPackageName());
                // add SHA certificate to request header
                urlConnection.setRequestProperty("X-Android-Cert", getSignature(context.getPackageManager(), context.getPackageName()));

                urlConnection.setRequestMethod(requestType);
                if (TextUtils.equals(requestType, HttpRequester.POST) || TextUtils.equals(requestType, HttpRequester.PUT)) {
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    String urlParameters = jsonObject.toString();
                    TaatikoLog.Log(TAG, "Parameters=" + urlParameters);
                    urlConnection.setFixedLengthStreamingMode(urlParameters.getBytes().length);
                    urlConnection.setRequestProperty(CONTENT_TYPE,
                            APPLICATION_TYPE);
                    urlConnection.setUseCaches(false);
                    urlConnection.connect();
                    //send the POST out
                    DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                    outputStream.write(urlParameters.getBytes("utf-8"));
                    outputStream.flush();
                    outputStream.close();
                }
                // handle issues
                statusCode = urlConnection.getResponseCode();
                TaatikoLog.Log(TAG, "StatusCode= " + statusCode);
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = urlConnection.getInputStream();
                    return getResponse(in);
                } else {
                    throw new AppRuntimeException("Your HTTP Connection is not OK");
                }
            } catch (MalformedURLException | ProtocolException | UnsupportedEncodingException e) {
                TaatikoLog.handleException(TAG + "_1", e);
//                Utils.hideCustomProgressDialog();
            } catch (SocketTimeoutException e) {
                TaatikoLog.handleException(TAG + "_2", e);
                publishProgress(408);
//                Utils.hideCustomProgressDialog();
            } catch (IOException e) {
                TaatikoLog.handleException(TAG + "_3", e);
//                Utils.hideCustomProgressDialog();
            } catch (RuntimeException e) {
                TaatikoLog.handleException(TAG + "_4", e);
                publishProgress(statusCode);
//                Utils.hideCustomProgressDialog();
            } catch (OutOfMemoryError oume) {
                TaatikoLog.Log(TAG + "_5", oume.getMessage());
//                Utils.hideCustomProgressDialog();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]) {
                case HttpURLConnection.HTTP_NOT_FOUND:
                    Toast.makeText(context, "Url not found\\nPlease check your internet connection", Toast.LENGTH_LONG).show();
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    Toast.makeText(context, "Internal server error", Toast.LENGTH_LONG).show();
                    break;
                case HttpURLConnection.HTTP_BAD_GATEWAY:
                    Toast.makeText(context, "Low internet connection", Toast.LENGTH_LONG).show();
                    break;
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    Toast.makeText(context, "Service unavailable", Toast.LENGTH_LONG).show();
                    break;
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    Toast.makeText(context, "Gateway timeout", Toast.LENGTH_LONG).show();
                    break;
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                    Toast.makeText(context, "Please try again", Toast.LENGTH_LONG).show();
                    break;
                case HttpURLConnection.HTTP_ENTITY_TOO_LARGE:
                    Toast.makeText(context, "Request entity to large", Toast.LENGTH_LONG).show();
                    break;
                default:
                    //Do with default
                    break;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (asyncTaskCompleteListener != null) {
                asyncTaskCompleteListener.onTaskCompleted(response, serviceCode);
            }
        }

        private String getResponse(InputStream inputStream) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder response = new StringBuilder();
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    TaatikoLog.handleException(TAG, e);
                }
            }
            return response.toString();
        }
    }

    /**
     * Gets the SHA1 signature, hex encoded for inclusion with Google Cloud Platform API requests
     *
     * @param packageName Identifies the APK whose signature should be extracted.
     * @return a lowercase, hex-encoded
     */
    public static String getSignature(@NonNull PackageManager pm, @NonNull String packageName) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo == null
                    || packageInfo.signatures == null
                    || packageInfo.signatures.length == 0
                    || packageInfo.signatures[0] == null) {
                return null;
            }
            return signatureDigest(packageInfo.signatures[0]);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private static String signatureDigest(Signature sig) {
        byte[] signature = sig.toByteArray();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(signature);
            return "";
//            return BaseEncoding.base16().lowerCase().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

}
