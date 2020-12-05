import java.net.URLEncoder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;


public class Parser {

    public static void main(String[] args) throws Exception
    {
//        JSONObject data = getCurrentData();
//        String message = retrieveCurrentData(data);
//
//        System.out.println(data.toString());


        JSONObject data = getWeeklyData("Nalchik");
//        String message = retrieveWeeklyData(data);

        System.out.println(data.toString(4));
//        System.out.println(message);
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
        HttpResponse<JsonNode> response = Unirest.get("https://weatherapi-com.p.rapidapi.com/forecast.json?q=" + city +"&lang=ru&days=7")
                .header("x-rapidapi-key", "af19471729msh67370b1f5c7efdfp1f26f0jsnf82d973c1c62")
                .header("x-rapidapi-host", "weatherapi-com.p.rapidapi.com")
                .asJson();

        return response.getBody().getObject();
    }


    public static String retrieveWeeklyData(JSONObject data) throws Exception{
        int answerCode = data.getJSONObject("current").getJSONObject("condition").getInt("code");
        System.out.println(answerCode);
        String answer;

        if (answerCode == 1147) {
            String city_name = data.getJSONObject("location").getString("name");


            answer = city_name + "\n" +
                        " ";

        } else answer = "Город не найден";

        return answer;
    }


}