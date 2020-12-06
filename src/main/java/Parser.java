import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;


public class Parser {

    public static void main(String[] args) throws Exception
    {
//        JSONObject data = getCurrentData();
//        String message = retrieveCurrentData(data);
//
//        System.out.println(data.toString());


        JSONObject data = getWeeklyData("Nalchik");
        retrieveWeeklyData(data);
//        System.out.println(data.toString(4));
    }


    public static JSONObject getCurrentData(String city) throws Exception{
        HttpResponse<JsonNode> response = Unirest.get("https://community-open-weather-map.p.rapidapi.com/weather?" +
                "q=" + city + "%2Cru&lat=0&lon=0&lang=ru&units=metric&mode=xml%2C%20html")
                .header("x-rapidapi-key", "af19471729msh67370b1f5c7efdfp1f26f0jsnf82d973c1c62")
                .header("x-rapidapi-host", "community-open-weather-map.p.rapidapi.com")
                .asJson();

        return response.getBody().getObject();
    }


    public static String retrieveCurrentData(JSONObject data) throws Exception{
        int answerCode = data.getInt("cod");
        String answer;

        if (answerCode == 200) {
            String city_name = data.getString("name");
            double temperature = data.getJSONObject("main").getDouble("temp");
            String description = data.getJSONArray("weather").getJSONObject(0).getString("description");

            answer = city_name + "\n" +
                     temperature + " C^\n" +
                     description + "\n";

        } else answer = "Город не найден";

        return answer;
    }


    public static JSONObject getWeeklyData(String city) throws Exception{
        HttpResponse<JsonNode> response = Unirest.get("https://community-open-weather-map.p.rapidapi.com/forecast?" +
                "q=" + city + "%2Cru&units=metric&lang=ru")
                .header("x-rapidapi-key", "af19471729msh67370b1f5c7efdfp1f26f0jsnf82d973c1c62")
                .header("x-rapidapi-host", "community-open-weather-map.p.rapidapi.com")
                .asJson();

        return response.getBody().getObject();
    }


    public static String retrieveWeeklyData(JSONObject data) throws Exception{
        int answerCode = data.getInt("cod");
        String answer;

        if (answerCode == 200) {
            String city_name = data.getJSONObject("city").getString("name");
            JSONArray forecast = data.getJSONArray("list");
            int curDay = LocalDate.now().getDayOfMonth();
            int curMonth = LocalDate.now().getMonthValue();
            int curYear = LocalDate.now().getYear();

            for (int day = 0; day < 40; day++){
                int fcDateDay = Integer.parseInt(forecast.getJSONObject(day).getString("dt_txt").substring(8, 10));
                int fcDateMonth = Integer.parseInt(forecast.getJSONObject(day).getString("dt_txt").substring(5, 7));
                int fcDateYear = Integer.parseInt(forecast.getJSONObject(day).getString("dt_txt").substring(0, 4));

                if (fcDateDay > curDay || fcDateMonth > curMonth || fcDateYear > curYear) {
                    System.out.println("True");
                } else System.out.println("False");
            }

            answer = city_name + "\n" +
                        " ";

        } else answer = "Город не найден";

        return answer;
    }


}