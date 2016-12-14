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

    @GET("vendors/{uid}")
    Call<List<Vendor>> getVendors(@Path("uid") String uid);

    @GET("vendors/{vid}/products/{pid}")
    Call<Product> getProduct(@Path("vid") String vid, @Path("pid") String pid);

    @POST("users")
    Call<User> createUser(@Body User user);

    @POST("users")
    Call<User> loginUser(@Body User user);

    @GET("users/{uid}/cards")
    Call<List<EncryptedMCard>> getCards(@Path("uid") String _id);

    @POST("users/{uid}/cards")
    Call<JSONObject> addCard(@Path("uid") String _id, @Body EncryptedMCard eMCard);

    @POST("users/{uid}/cards")
    Call<JSONObject> deleteCard(@Path("uid") String _id, @Body EncryptedMCard eMCard);

    @POST("payments")
    Call<Payment> pay(@Body Payment payment);
}
