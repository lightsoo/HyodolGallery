package swmaestro.lightsoo.hyodolgallery.RestAPI;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import swmaestro.lightsoo.hyodolgallery.Data.Message;

/**
 * Created by LG on 2016-07-20.
 */
public interface PushService {


    @FormUrlEncoded
    @POST("/regtoken")
    Call<Message> regtoken(@Field("token") String token);


    @FormUrlEncoded
    @POST("/pushmessage")
    Call<Message> pushmessage(@Field("message") String message);



}
