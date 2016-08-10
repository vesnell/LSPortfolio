package vesnell.pl.lsportfolio.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import vesnell.pl.lsportfolio.model.details.Data;

/**
 * Created by Lenovo on 22.07.2016.
 */
public interface LooksoftDetailsApi {
    @GET("product/{product}")
    Call<Data> getDetails(@Path("product") String product);
}
