package vesnell.pl.lsportfolio.api;

import retrofit2.Call;
import retrofit2.http.GET;
import vesnell.pl.lsportfolio.model.main.Data;

/**
 * Created by Lenovo on 22.07.2016.
 */
public interface LooksoftMainApi {
    @GET("main")
    Call<Data> loadData();
}
