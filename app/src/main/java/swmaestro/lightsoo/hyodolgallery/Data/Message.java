package swmaestro.lightsoo.hyodolgallery.Data;

/**
 * Created by LG on 2016-04-18.
 */
public class Message {

    public String msg;
    public int code;

    public Message(){}
    public Message(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        String str = "code : " + code + ", msg : " + msg;
        return str;
    }
}
