package swmaestro.lightsoo.hyodolgallery.Manager;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class NetworkManager {

    //aws 아이피주소
    private static final String serverURL ="http://52.69.253.226:3000/";

    //피타사무실 아이피주소
//    private static final String serverURL ="http://192.168.0.13:3333/";

    //소마
//    private static final String serverURL ="http://172.16.101.116:3000/";

    //효돌 아이피주소
//    private static final String serverURL ="http://192.168.0.3:3000/";
    //local
//        private static final String serverURL ="http://127.0.0.1:3333/";
    Retrofit client;
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    private NetworkManager(){
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        //Retrofit설정, req를 인터셉트한다
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response;
            }
        });

        // add custom interceptor to manipulate the cookie value in header
        okHttpClient.interceptors().add(new RequestInterceptor());
        okHttpClient.interceptors().add(new ResponseInterceptor());

        client = new Retrofit.Builder()
                .baseUrl(serverURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }


    //싱글톤 패턴, 프로그램 종료시점까지 하나의 인스턴스만을 생성해서 관리한다.
//    thread safe, lazy class initialization, memory saving
    public static class InstanceHolder{
        public static final NetworkManager INSTANCE = new NetworkManager();
    }
    public static NetworkManager getInstance(){return InstanceHolder.INSTANCE;}
    //나의 restAPI를 호출
    public <T> T getAPI(Class<T> serviceClass){
        return client.create(serviceClass);
    }

    // custom req, res interceptors
    public class ResponseInterceptor implements Interceptor{
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            if(!response.headers("Set-Cookie").isEmpty()){
                HashSet cookies = new HashSet();
                for (String header : response.headers("Set-Cookie")) {
                    cookies.add(header);
                }
                PropertyManager.getInstance().setCookie(cookies);
            }
            return response;
        }
    }
    public class RequestInterceptor implements  Interceptor{
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            HashSet<String> preferences = PropertyManager.getInstance().getCookie();
            for (String cookie : preferences) {
                builder.addHeader("Cookie", cookie);
                Log.v("NetworkManager", "Cookie : " + cookie);
            }
            return chain.proceed(builder.build());
        }
    }
}
