package matrians.instapaysam;

import java.util.List;

import matrians.instapaysam.schemas.Card;
import matrians.instapaysam.schemas.Product;
import matrians.instapaysam.schemas.User;
import matrians.instapaysam.schemas.Vendor;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Team Matrians
 */
interface InstaPayEndpointInterface {

    @GET("vendors")
    Call<List<Vendor>> getVendors();

    @GET("product/{vid}/{pid}")
    Call<Product> getProduct(@Path("vid") String vid, @Path("pid") String pid);

    @POST("user")
    Call<User> createUser(@Body User user);

    @POST("user")
    Call<User> loginUser(@Body User user);

    @POST("card")
    Call<List<Card>> getCards(@Body String _id, @Body String email);
    /*
    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);

    @GET("group/{id}/users")
    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);

    @POST("users/new")
    Call<User> createUser(@Body User user);
    */
}
