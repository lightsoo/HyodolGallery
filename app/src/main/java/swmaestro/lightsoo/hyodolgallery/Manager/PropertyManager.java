package swmaestro.lightsoo.hyodolgallery.Manager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;

import swmaestro.lightsoo.hyodolgallery.MyApplication;


public class PropertyManager {

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    private static final String KEY_LOGIN_TYPE = "key_login_type";
    public static final String LOGIN_TYPE_FACEBOOK = "login_type_facebook";
    public static final String LOGIN_TYPE_LOCAL = "login_type_local";

//   case : facebook, accesstoken,
//    case: local, email
    private static final String FILED_USER_ID ="user_id";
//    case : facebook, non
//    case : local, pwd
private static final String FILED_USER_PWD ="user_pwd";


    public static final String KEY_COOKIE = "kie_cookie";

//    for GCM
    private static final String REG_TOKEN = "regToken";

    private PropertyManager() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        mEditor = mPrefs.edit();
    }
    // singleton holder pattern : thread safe, lazy class initialization, memory saving.
    public static class InstanceHolder{ private static final PropertyManager INSTANCE = new PropertyManager();}
    public static PropertyManager getInstance(){ return InstanceHolder.INSTANCE; }


    public void setUserLoginId(String id){
        mEditor.putString(FILED_USER_ID, id);
        mEditor.commit();
    }
    public void setUserLoginPwd(String pwd){
        mEditor.putString(FILED_USER_PWD, pwd);
        mEditor.commit();
    }
    public void setLoginType(String loginType){
        mEditor.putString(KEY_LOGIN_TYPE, loginType);
        mEditor.commit();
    }

    public void setRegistrationToken(String regId) {
        mEditor.putString(REG_TOKEN, regId);
        mEditor.commit();
    }

    public void setCookie(HashSet cookie){
        mEditor.putStringSet(KEY_COOKIE, cookie);
        mEditor.commit();
    }


    public HashSet getCookie(){
        return (HashSet)mPrefs.getStringSet(KEY_COOKIE, new HashSet());
    }


    public String getRegistrationToken() {
        return mPrefs.getString(REG_TOKEN, "");
    }

    public String getUserLoginId(){
        return mPrefs.getString(FILED_USER_ID, "");
    }

    public String getUserLoginPwd(){
        return mPrefs.getString(FILED_USER_PWD, "");
    }



    public String getLoginType(){
        return mPrefs.getString(KEY_LOGIN_TYPE, "");
    }

}