package apihandler;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

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
    Call<String> registerTabletCall(@Field("tablet_serial_number") String tablet_serial_number,
                                    @Field("launcher_version") String launcher_version,
                                    @Field("android_version") String android_version,
                                    @Field("cl_serial_number") String cl_serial_number);




    @FormUrlEncoded
    @POST("/oauth/access_token")
    Call<String> getAccessTokenCall(
            @Header("Authorization") String authorizationHeader,
            @Field("grant_type") String grant_type);

}
