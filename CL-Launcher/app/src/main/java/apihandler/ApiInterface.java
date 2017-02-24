package apihandler;



import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Alok on 222-02-2017.
 */
public interface ApiInterface {


    /**Registration
     * @param tablet_serial_number
     * @param launcher_version
     * @param android_version
     * @param cl_serial_number
     * @return
     */
    @FormUrlEncoded
    @POST("/register")
    Call<JsonObject> registerTabletCall(@Field("tablet_serial_number") String tablet_serial_number,
                                        @Field("launcher_version") String launcher_version,
                                        @Field("android_version") String android_version,
                                        @Field("cl_serial_number") String cl_serial_number);




    @FormUrlEncoded
    @POST("/oauth/access_token")
    Call<JsonObject> getAccessTokenCall(
            @Header("Authorization") String authorizationHeader,
            @Field("grant_type") String grant_type);



    @GET("/manifest/{cl_serial_number}")
    Call<JsonObject> getManifestCall(
            @Header("Authorization") String authorizationHeader,
            @Path("cl_serial_number") String cl_serial_number);

}
