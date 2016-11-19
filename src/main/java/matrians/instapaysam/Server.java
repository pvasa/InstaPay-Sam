package matrians.instapaysam;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 Team Matrians
 */

class Server {

    //private static final String BASE_URL = "http://192.168.0.100:8080/";
    private static final String BASE_URL = "https://instapay-animus.rhcloud.com/";

    private Server(){}

    static InstaPayEndpointInterface connect() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(InstaPayEndpointInterface.class);
    }
}
