package swmaestro.lightsoo.hyodolgallery.RestAPI;

import com.squareup.okhttp.RequestBody;

import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.http.Path;
import swmaestro.lightsoo.hyodolgallery.Data.Anni;
import swmaestro.lightsoo.hyodolgallery.Data.Message;

public interface EventAPI {


    @FormUrlEncoded
    @POST("/lover")
    Call<Message> loverMake(@Field("lover_email") String email);

//    @Multipart
//    @POST("/event")
//    Call<Message> addEvent(@Part("event_title")String event_title,
//                           @Part("event_date")String event_date,
//                           @Part("event_place")String event_place,
//                           @Part("event_img\"; filename=\"image.jpg\" ")RequestBody file1,
//                           @Part("event_img\"; filename=\"image.jpg\" ")RequestBody file2);

    @Multipart
    @POST("/event")
    Call<Message> addEvent(@Part("event_title") String event_title,
                           @Part("event_date") String event_date,
                           @Part("event_place") String event_place,
                           @PartMap() Map<String, RequestBody> partMap);


    @GET("/event")
    Call<List<Anni>> getEvents();

    @GET("/event/{event_id}")
    Call<Anni> getEventInfo(@Path("event_id") int event_id);

}
