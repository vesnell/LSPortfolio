package vesnell.pl.lsportfolio.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import vesnell.pl.lsportfolio.R;
import vesnell.pl.lsportfolio.model.ProjectDetails;
import vesnell.pl.lsportfolio.utils.Resources;

/**
 * Created by Lenovo on 22.07.2016.
 */
public interface LooksoftDetailsApi {
    String ENDPOINT = Resources.getString(R.string.looksoft_api);

    @GET("/product/{product}")
    Call<ProjectDetails> getDetails(@Path("product") String product);
}
