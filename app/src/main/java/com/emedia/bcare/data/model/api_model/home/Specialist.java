
package com.emedia.bcare.data.model.api_model.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Specialist {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("specialist_rate")
    @Expose
    private String specialistRate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSpecialistRate() {
        return specialistRate;
    }

    public void setSpecialistRate(String specialistRate) {
        this.specialistRate = specialistRate;
    }

}
