package util;

import android.net.Uri;
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

    public void uploadFile(String authorizationKey,
                           String tablet_serial_number,
                           final Uri fileUri) {

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
                            MediaType.parse(type),
                            file
                    );

            // MultipartBody.Part is used to send also the actual file name
            body =
                    MultipartBody.Part.createFormData("CL_Launcher", file.getName(), requestFile);
        }


        // add another part within the multipart request

        Call<String> call = service.callUploadFile(authorizationKey,tablet_serial_number,body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call,
                                   Response<String> response) {

                if (response.isSuccessful()) {
                    Log.e("Upload error:", response.body());
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

}
