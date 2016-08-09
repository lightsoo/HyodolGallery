package swmaestro.lightsoo.hyodolgallery.RestAPI;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import swmaestro.lightsoo.hyodolgallery.Data.Message;


/**
 * Created by LG on 2016-07-24.
 */
public interface LoginAPI {

    @FormUrlEncoded
    @POST("/auth/facebook/login")
    Call<Message> authFacebookLogin(@Field("access_token") String accessToken);

    @FormUrlEncoded
    @POST("/auth/local/login")
    Call<Message> authLocalLogin(@Field("email") String email,
                                 @Field("pwd") String pwd);


}
