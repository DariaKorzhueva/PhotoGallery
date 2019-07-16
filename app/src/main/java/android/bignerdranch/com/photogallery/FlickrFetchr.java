package android.bignerdranch.com.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String PAGE = "page";
    private static final String API_KEY = "4f721bbafa75bf6d2cb5af54f937bb70";

    private static int maxPages;
    private static int itemsPerPage;
    private static int totalItems;

    /* Отправка запроса серверу на получение данных о фотографиях и чтение информации */
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            /* Чтение ответа */
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /* Запрос к серверу */
    public List<GalleryItem> fetchItems(Integer page) {
        List<GalleryItem> items = new ArrayList<>();

        try {
            /* Построение запроса к серверу */
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();

            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);

            /* Извлечение инормации из JSON при помощи средств библиотеки GSON */
            Gson gson = new Gson();
            PhotoRequestResult result = gson.fromJson(jsonString, PhotoRequestResult.class);
            maxPages = result.getPageCount();
            itemsPerPage = result.getItemsPerPage();
            totalItems = result.getItemCount();
            items = result.getResults();
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return items;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public int getTotalItems() {
        return totalItems;
    }
}
