package util;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import apihandler.ApiClient;
import apihandler.ApiInterface;
import database.BackgroundDataCollectionDB;
import database.DBAdapter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by IMFCORP\alok.acharya on 7/3/17.
 */

public class UploadDBFile {

    private DBAdapter mDbAdapter;
    private BackgroundDataCollectionDB backgroundDataCollectionDB;

    public static void uploadFile(String authorizationKey,
                           String tablet_serial_number,
                           final Uri fileUri) {
        authorizationKey = "Bearer "+authorizationKey;

        // create upload service client
        ApiInterface service =
                ApiClient.getClient().create(ApiInterface.class);
        MultipartBody.Part body = null;
        if(!fileUri.toString().equals("")) {
            File file = FileUtils.getFile(fileUri.toString());
            String type = Utils.getMimeType(fileUri.toString());
            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse("application/file"),
                            file
                    );

            // MultipartBody.Part is used to send also the actual file name
            body =
                    MultipartBody.Part.createFormData("file","CL_Launcher.db", requestFile);
        }


        // add another part within the multipart request

        Call<String> call = service.callUploadFile(authorizationKey,tablet_serial_number,body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call,
                                   Response<String> response) {

                if (response.isSuccessful()) {
                    Log.e("Upload success:", response.body());
                    FileUtils.deleteQuietly(FileUtils.getFile(fileUri.toString()));

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }



    public static boolean copyFileUsingStream(File source, File dest) throws IOException {
        if(!dest.exists()){
            dest = new File(dest.getPath());
        }
        InputStream is = null;
        OutputStream os = null;
        boolean result = false;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            result = true;
        }catch (IOException e){
            e.printStackTrace();
            result = false;
        }catch (Exception e){
            e.printStackTrace();
            result = false;
        }finally {
            is.close();
            os.close();
        }
        return  result;
    }



    public void doFlushBackgroundData(final Context _Context){

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(mDbAdapter==null){
                    mDbAdapter = new DBAdapter(_Context);
                    mDbAdapter.open();
                }
                if(!mDbAdapter.isOpen()){
                    mDbAdapter.open();
                }

                if(backgroundDataCollectionDB==null){
                    backgroundDataCollectionDB = new BackgroundDataCollectionDB(_Context);
                    backgroundDataCollectionDB.open();

                }
                if(!backgroundDataCollectionDB.isOpen()){
                    backgroundDataCollectionDB.open();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {

                if(backgroundDataCollectionDB!=null){
                    backgroundDataCollectionDB.deleteAllDetails();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(mDbAdapter.isOpen()){
                    mDbAdapter.close();
                }
                if(backgroundDataCollectionDB.isOpen()){
                    backgroundDataCollectionDB.close();
                }
            }
        }.execute();
    }

}
