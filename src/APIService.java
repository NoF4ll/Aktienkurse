import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class APIService {

	public static void getData(String aktie, ArrayList<LocalDate> dates, ArrayList<Double> closeValue,
			HashMap<LocalDate, Integer> splitCoeffecient) throws MalformedURLException, JSONException, IOException {

		double coefficient = 1.0;
		String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + aktie
				+ "&outputsize=full&apikey=VTRNJJSSMYW3MPD6";
		JSONObject json = new JSONObject(IOUtils.toString(new URL(url), Charset.forName("UTF-8")));
		json = json.getJSONObject("Time Series (Daily)");

		for (int i = 0; i < json.length(); i++) {
			dates.add(LocalDate.parse((CharSequence) json.names().get(i)));
			closeValue.add(json.getJSONObject(LocalDate.parse((CharSequence) json.names().get(i)).toString())
					.getDouble("4. close"));
			if (json.getJSONObject(LocalDate.parse((CharSequence) json.names().get(i)).toString())
					.getDouble("8. split coefficient") > coefficient) {
				coefficient = json.getJSONObject(LocalDate.parse((CharSequence) json.names().get(i)).toString())
						.getDouble("8. split coefficient");
				splitCoeffecient.put(LocalDate.parse((CharSequence) json.names().get(i)), (int) coefficient);
			}
		}
	}
}
