import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class Parser {

    public static void main(String[] args) throws Exception
    {
    //    JSONObject data = getCurrentData("Moscow");

    //    System.out.println(data.toString());

    //    String message = retrieveCurrentData(data);
    //    System.out.println(message.toString());
    //    System.out.println(message);


    //    JSONObject data = getTodayData("Moscow");
    //    retrieveTodayData(data);
    //    System.out.println(data.toString(4));
    }


    public static JSONObject getCurrentData(String city) throws Exception{
        HttpResponse<JsonNode> response = Unirest.get("https://community-open-weather-map.p.rapidapi.com/weather?" +
                "q=" + city + "%2Cru&lat=0&lon=0&lang=ru&units=metric&mode=xml%2C%20html")
                // .header("x-rapidapi-key", "27dd8f83f622cbdab5d947ec528fcc25")
                .header("x-rapidapi-key", "f9ec3da22f3997c8018981ab4fbea7d8")
                .header("x-rapidapi-host", "community-open-weather-map.p.rapidapi.com")
                .asJson();

        return response.getBody().getObject();
    }


    public static String retrieveCurrentData(JSONObject data) throws Exception{
        int answerCode = data.getInt("cod");
        String answer;

        if (answerCode == 200) {
            String city = data.getString("name");
            String temp = String.valueOf(Math.round(data.getJSONObject("main").getDouble("temp"))) + " C°";
            String desc = data.getJSONArray("weather").getJSONObject(0).getString("description");
            desc = beautify(desc);

            answer = city + "\n" +
                    temp + "\n" +
                    desc + "\n";

        } else answer = "Город не найден, укажите его заново";

        return answer;
    }


    public static JSONObject getTodayData(String city) throws Exception{
        HttpResponse<JsonNode> response = Unirest.get("https://community-open-weather-map.p.rapidapi.com/forecast?" +
                "q=" + city + "%2Cru&units=metric&lang=ru")
                .header("x-rapidapi-key", "af19471729msh67370b1f5c7efdfp1f26f0jsnf82d973c1c62")
                .header("x-rapidapi-host", "community-open-weather-map.p.rapidapi.com")
                .asJson();

        return response.getBody().getObject();
    }


    public static String retrieveTodayData(JSONObject data) throws Exception{
        int answerCode = data.getInt("cod");
        String answer;

        if (answerCode == 200) {
            String city_name = data.getJSONObject("city").getString("name");
            int currentHour = LocalTime.now().getHour();
            int tics = ((24 - currentHour) / 3) + 1;
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - "
                    + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

            answer = city_name + "\n" +
                    currentDate + "\n" +
                    "\n";

            for (int day = 0; day < tics; day++){
                JSONObject forecast = data.getJSONArray("list").getJSONObject(day);
                String time = forecast.getString("dt_txt").substring(11, 16);
                String temp = String.valueOf(Math.round(forecast.getJSONObject("main").getDouble("temp"))) + " C°";
                String desc = forecast.getJSONArray("weather").getJSONObject(0).getString("description");
                desc = beautify(desc);

                answer += time + " -> " + temp + "\n" +
                        desc + "\n\n";
            }


        } else answer = "Город не найден, укажите его заново";

        return answer;
    }

    public static JSONObject getTomorrowData(String city) throws Exception{
        HttpResponse<JsonNode> response = Unirest.get("https://community-open-weather-map.p.rapidapi.com/forecast?" +
                "q=" + city + "%2Cru&units=metric&lang=ru")
                .header("x-rapidapi-key", "af19471729msh67370b1f5c7efdfp1f26f0jsnf82d973c1c62")
                .header("x-rapidapi-host", "community-open-weather-map.p.rapidapi.com")
                .asJson();

        return response.getBody().getObject();
    }


    public static String retrieveTomorrowData(JSONObject data) throws Exception{
        int answerCode = data.getInt("cod");
        String answer;

        if (answerCode == 200) {
            String city_name = data.getJSONObject("city").getString("name");
            int currentHour = LocalTime.now().getHour();
            int tics = ((24 - currentHour) / 3) + 1;
            String tomorrowDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString();

            answer = city_name + "\n" +
                    tomorrowDate + "\n" +
                    "\n";

            for (int day = tics; day < tics + 8; day++){
                JSONObject forecast = data.getJSONArray("list").getJSONObject(day);
                String time = forecast.getString("dt_txt").substring(11, 16);
                String temp = String.valueOf(Math.round(forecast.getJSONObject("main").getDouble("temp"))) + " C°";
                String desc = forecast.getJSONArray("weather").getJSONObject(0).getString("description");
                desc = beautify(desc);

                answer += time + " -> " + temp + "\n" +
                        desc + "\n\n";
            }


        } else answer = "Город не найден, укажите его заново";

        return answer;
    }


    public static String beautify(String string){
        string = string.toLowerCase();
//        "⚡☀🌤🌥⛅☁🌦🌧⛈🌩🌨❄🌫🌙"
        if (string.contains("небольшая облачность"))
            string += " \uD83C\uDF24";
        else if (string.contains("облачно"))
            string += " ⛅";
        else if (string.contains("ясно"))
            string += " \uD83C\uDF15 \uD83C\uDF19";
        else if (string.contains("небольшой дождь"))
            string += " \uD83C\uDF26";
        else if (string.contains("гроз") || string.contains("молн"))
            string += " ⛈";
        else if (string.contains("пасмурно"))
            string += " ☁";
        else if (string.contains("снег"))
            string += " \uD83C\uDF28";
        else if (string.contains("туман"))
            string += " \uD83C\uDF2B";

        return string;
    }

}