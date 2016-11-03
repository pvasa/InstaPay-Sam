package matrians.instapaysam;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

interface InstaPayEndpointInterface {

    @GET("vendors")
    Call<List<Vendor>> getVendors();

    /*
    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);

    @GET("group/{id}/users")
    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);

    @POST("users/new")
    Call<User> createUser(@Body User user);
    */
}
