package bef.rest.neshast;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by hojjatimani on 1/20/2016 AD.
 */
public interface RetrofitService {
    @FormUrlEncoded
    @POST("register")
    Call<POJOs.RegisterResponse> registerUser(@Field("name") String name, @Field("company") String company);

    @Multipart
    @POST("update/{uId}")
    Call<POJOs.UpdateResponse> uploadImage(@Path("uId") long uId, @Part("image") RequestBody image);

    @FormUrlEncoded
    @POST("ask-question/{uId}")
    Call<POJOs.AskResponse> sendQuestion(@Path("uId") long uId, @Field("question") String question);

    @POST("show-me/{uId}")
    Call<POJOs.AskResponse> showMe(@Path("uId") long uId);
}