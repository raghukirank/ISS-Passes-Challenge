package task.com.isspasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Renown2 on 1/10/2018.
 */

public class Response {
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("risetime")
    @Expose
    private Integer risetime;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getRisetime() {
        return risetime;
    }

    public void setRisetime(Integer risetime) {
        this.risetime = risetime;
    }

}
