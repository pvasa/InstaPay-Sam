package matrians.instapaysam;

import java.util.List;

import matrians.instapaysam.Schemas.User;
import matrians.instapaysam.Schemas.Vendor;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 Team Matrians
 **/

interface InstaPayEndpointInterface {

    @GET("vendors")
    Call<List<Vendor>> getVendors();

    @POST("user")
    Call<User> createUser(@Body User user);

    @POST("user")
    Call<User> loginUser(@Body User user);
    /*
    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);

    @GET("group/{id}/users")
    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);

    @POST("users/new")
    Call<User> createUser(@Body User user);
    */
}
