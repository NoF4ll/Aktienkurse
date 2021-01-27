import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class APIService {

	public static void getData(String aktie,ArrayList<LocalDate> dates,ArrayList<Double> closeValue ) throws MalformedURLException, JSONException, IOException {

		String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+ aktie + "&outputsize=full&apikey=VTRNJJSSMYW3MPD6";
		JSONObject json = new JSONObject(IOUtils.toString(new URL(url), Charset.forName("UTF-8")));
		json = json.getJSONObject("Time Series (Daily)");

		for (int i = 0; i < json.length(); i++) {
			dates.add(LocalDate.parse((CharSequence) json.names().get(i)));
			closeValue.add(json.getJSONObject(LocalDate.parse((CharSequence) json.names().get(i)).toString())
					.getDouble("4. close"));
		}
	}
}
