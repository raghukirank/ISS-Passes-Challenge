package task.com.isspasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Renown2 on 1/10/2018.
 */

public  class Request {
    @SerializedName("altitude")
    @Expose
    private Integer altitude;
    @SerializedName("datetime")
    @Expose
    private Integer datetime;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("passes")
    @Expose
    private Integer passes;

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public Integer getDatetime() {
        return datetime;
    }

    public void setDatetime(Integer datetime) {
        this.datetime = datetime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) { this.longitude = longitude;}

    public Integer getPasses() {
        return passes;
    }

    public void setPasses(Integer passes) { this.passes = passes;
    }
}
