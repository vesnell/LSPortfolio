package vesnell.pl.lsportfolio.api;

import retrofit2.Call;
import retrofit2.http.GET;
import vesnell.pl.lsportfolio.model.Data;

/**
 * Created by Lenovo on 22.07.2016.
 */
public interface LooksoftMainApi {
    String ENDPOINT = "http://www.looksoft.pl/api/";

    @GET("main")
    Call<Data> loadData();
}
