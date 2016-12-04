package matrians.instapaysam.recyclerview;

import org.json.JSONObject;

import java.util.List;

import matrians.instapaysam.pojo.EncryptedMCard;
import matrians.instapaysam.pojo.Payment;
import matrians.instapaysam.pojo.Product;
import matrians.instapaysam.pojo.User;
import matrians.instapaysam.pojo.Vendor;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Team Matrians
 */
public interface InstaPayEndpointInterface {

    @GET("vendors")
    Call<List<Vendor>> getVendors();

    @GET("products/{vid}/{pid}")
    Call<Product> getProduct(@Path("vid") String vid, @Path("pid") String pid);

    @POST("users")
    Call<User> createUser(@Body User user);

    @POST("users")
    Call<User> loginUser(@Body User user);

    @POST("cards")
    Call<List<EncryptedMCard>> getCards(@Body EncryptedMCard eMCard);

    @POST("cards")
    Call<JSONObject> addCard(@Body EncryptedMCard eMCard);

    @POST("pay")
    Call<Payment> pay(@Body Payment payment);

    /*
    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);

    @GET("group/{id}/users")
    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);

    @POST("users/new")
    Call<User> createUser(@Body User user);
    */
}
