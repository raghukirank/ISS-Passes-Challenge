package task.com.isspasses;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

/**
 * Created by Renown2 on 1/10/2018.
 */

public interface GetPasses{
    @Headers("Content-Type: text/plain")
    @GET()
    Call<Example> getjson(@Url String url);
}
