package com.example.alireza.empreints;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by alireza on 14/11/15.
 */
public class Records implements Serializable {
    String recordsName, contactUsername, commnets,lng, lat,date,ranking;

    public void setRecordsName(String st){this.recordsName=st;}
    public String getRecordsName(){return this.recordsName;}

    public void setCommnets(String cm){this.commnets=cm;}
    public String getCommnets(){return this.commnets;}

    public void setContactUsername(String cn){this.contactUsername=cn;}
    public String getContactUsername(){return this.contactUsername;}

    public void setLng(String lng){this.lng=lng;}
    public String getLng(){return this.lng;}

    public void setLat(String lat){this.lat=lat;}
    public String getLat(){return this.lat;}

    public void setDate(String dt){this.date=dt;}
    public String getDate(){return this.date;}

    public void setRanking(String ra){this.ranking=ra;}
    public String getRanking(){return this.ranking;}

    public String printRecord(){
        return "Name is : "+this.recordsName+"\nDate is :"+this.date+"\nFor contact : "+this.contactUsername
                +"\nlng : "+this.lng+ "and lat : "+this.lat+"\nComment : "+this.commnets + "\nRanking : "+this.ranking;
    }
}
