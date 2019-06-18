import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String ID = "";
    private static final String password = "";
    private static LinkedHashMap<String, String> grades = new LinkedHashMap<>();
    private static final int number = 10000;
    private static String senderAddress = ID + "@mail.sustech.edu.cn";
    private static String senderPassword = "";
    private static String semester = "2018-2019-2";

    public static void main(String args[]) throws Exception {
        int i = 0;
        while ((i++) < number) {
            System.out.println(i);
            LinkedHashMap<String, String> data = getdata(ID, password);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if (!grades.containsKey(entry.getKey())) {
                    grades.put(entry.getKey(), entry.getValue());
                    System.out.println(entry.getKey() + " " + entry.getValue());
                       Email.sendEmail(entry.getKey() + " " + entry.getValue(), senderAddress, senderPassword);
                }
            }
            Thread.sleep(1000 * 60 * 5);
        }
    }

    private static LinkedHashMap<String, String> getdata(String username, String password) throws Exception {
        LinkedHashMap<String, String> gradedatas = new LinkedHashMap<>();
        Connection jwxt = Jsoup
                .connect("http://jwxt.sustc.edu.cn/jsxsd/");// 获取连接
        jwxt.header("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362");
        Connection.Response JSESSIONID = jwxt.method(Connection.Method.GET).execute();
        Document d1 = Jsoup.parse(JSESSIONID.body());
        List<Element> et = d1.select("#fm1");
        LinkedHashMap<String, String> datas = new LinkedHashMap<>();
        datas.put("_eventId", "submit");
        for (Element e : et.get(0).getAllElements()) {
            if (e.attr("name").equals("execution")) {
                datas.put("execution", e.attr("value"));
            }
        }
        datas.put("geolocation", "");
        datas.put("password", password);
        datas.put("username", username);
        Connection con2 = Jsoup
                .connect("https://cas.sustech.edu.cn/cas/login?service=http%3A%2F%2Fjwxt.sustech.edu.cn%2Fjsxsd%2F");
        con2.header("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362");
        try {
            Connection.Response login = con2.ignoreContentType(true).method(Connection.Method.POST)
                    .data(datas).cookies(JSESSIONID.cookies()).execute();

            if (login.statusCode() == 200) {
                gradedatas.put("change", "false");

                Connection con3 = Jsoup.connect("http://jwxt.sustech.edu.cn/jsxsd/kscj/cjcx_list");
                con3.header("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362");
                Map<String, String> datas1 = new HashMap<>();
                datas1.put("kksj", semester);
                datas1.put("kcxz", "");
                datas1.put("kcmc", "");
                datas1.put("xsfs", "all");

                Connection.Response cjlb = con3.ignoreContentType(true).method(Connection.Method.POST)
                        .data(datas1).cookies(JSESSIONID.cookies()).execute();
                Document kccj = Jsoup.parse(cjlb.body());
                Element table = kccj.selectFirst("#dataList").selectFirst("tbody");
                List<Element> list = table.select("tr");
                for (Element e : list) {
                    List<Element> td = e.select("td");
                    if (!td.isEmpty()) {
                        String classname = (td.get(3).toString().replaceAll("<td align=\"left\">", "").replaceAll("</td>", ""));
                        String grade = (td.get(5).toString().replaceAll("<td>", "").replaceAll("</td>", "")
                        );

                        gradedatas.put(classname, grade);
                    }
                }
                Connection logout = Jsoup.connect("https://cas.sustc.edu.cn/cas/logout");
                logout.header("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362");
                Connection.Response tcdl = logout.method(Connection.Method.GET).execute();
                if (tcdl.statusCode() == 200) System.out.println("logout succeed");
            } else {

                gradedatas.put("change", "true");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gradedatas;
    }

}
