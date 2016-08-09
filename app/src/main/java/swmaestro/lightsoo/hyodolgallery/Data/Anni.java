package swmaestro.lightsoo.hyodolgallery.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * anni 테이블 컬럼
 * 기념일을 저장한다.
 */


public class Anni implements Serializable {

    private int event_id;
    private String event_title;
    private String event_date;
    private String event_place;
//이것 때문에 데이터 파싱이 제대로 안되...따로 빼서 해야될것도 같다...
    public List<String> event_img;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//    private String event_dday;

    public Anni(int event_id, String event_title, String event_date){
        this.event_id = event_id;
        this.event_title = event_title;
        this.event_date = event_date;
    }

    public Anni(int event_id, String event_title, String event_date, String event_place){
        this.event_id = event_id;
        this.event_title = event_title;
        this.event_date = event_date;
        this.event_place = event_place;
    }



    public void setTitle(String event_title){
        this.event_title = event_title;
    }

    public void setDate(String event_date){
        this.event_date = event_date;
    }

    public void setId(int event_id){
        this.event_id = event_id;
    }

    public void setPlace(String event_place){
        this.event_place = event_place;
    }
    public String getPlace(){return event_place;}
    public String getTitle(){
        return event_title;
    }
    public String getDate(){

        return event_date;

    }
    public int getId(){
        return event_id;
    }

}
