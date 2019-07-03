
package com.emedia.bcare.data.model.api_model.my_event;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NextAppointment {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("reservation_date")
    @Expose
    private String reservationDate;
    @SerializedName("client_name")
    @Expose
    private String clientName;
    @SerializedName("client_mobile")
    @Expose
    private String clientMobile;
    @SerializedName("salon_id")
    @Expose
    private String salonId;
    @SerializedName("salon")
    @Expose
    private Salon_ salon;
    @SerializedName("salon_service_details")
    @Expose
    private List<SalonServiceDetail_> salonServiceDetails = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientMobile() {
        return clientMobile;
    }

    public void setClientMobile(String clientMobile) {
        this.clientMobile = clientMobile;
    }

    public String getSalonId() {
        return salonId;
    }

    public void setSalonId(String salonId) {
        this.salonId = salonId;
    }

    public Salon_ getSalon() {
        return salon;
    }

    public void setSalon(Salon_ salon) {
        this.salon = salon;
    }

    public List<SalonServiceDetail_> getSalonServiceDetails() {
        return salonServiceDetails;
    }

    public void setSalonServiceDetails(List<SalonServiceDetail_> salonServiceDetails) {
        this.salonServiceDetails = salonServiceDetails;
    }

}