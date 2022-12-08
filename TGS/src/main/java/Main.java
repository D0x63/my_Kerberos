import com.baidu.translate.demo.TransApi;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class Main {

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20200908000561776";
    private static final String SECURITY_KEY = "YyblatNY4G1vFmLULtZJ";

    public static void main(String[] args) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);

        String query = "";
        String str=api.getTransResult(query, "en", "zh");
        
        String[] st=str.split(",");
        String b=st[3];
        String c=b.split(":")[1];
        String d=c.split("\"")[1];
        System.out.println(d);
    }

}
