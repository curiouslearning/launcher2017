package apihandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import excelsoft.com.cl_launcher.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Alok on 22-02-2017.
 */
public class ApiClient {

    public static final String BASE_URL = BuildConfig.SERVICE_BASE_PATH;

    private static Retrofit retrofit = null;




    public static Retrofit getClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
