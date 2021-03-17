import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class main extends Application {

	static ArrayList<LocalDate> dates = new ArrayList<>();
	static ArrayList<Double> closeValue = new ArrayList<>();

	static ArrayList<LocalDate> sortetDates = new ArrayList<>();
	static ArrayList<Double> sortetCloseValue = new ArrayList<>();
	static ArrayList<Double> sortetAvgCloseValue = new ArrayList<>();
	
	static String aktie;

	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws MalformedURLException, JSONException, IOException, SQLException {
		boolean ok = false;
		System.out.println("Welche Aktie möchten sie bekommen? \n" + "tsla ... Tesla\n" + "AAPL ... Apple.");
		aktie = sc.next();
		final Connection connection = DatabaseManager.getInstance().getDatabaseConnection(3306, "aktien");
		while (ok == false) {

			System.out.println("Möchten sie die Datenbank updaten ...1 \noder den Chart Zeichnen ... 2");
			int a = sc.nextInt();
			if (a == 1) {
				updateDatabase(connection, aktie);
				ok = true;
			}
			if (a == 2) {

				showChart(connection, aktie);
				ok = true;
			}
		}
		sc.close();
	}

	public static void updateDatabase(Connection connection, String aktie)
			throws MalformedURLException, JSONException, IOException, SQLException {
		APIService.getData(aktie, dates, closeValue);

		for (int i = 0; i < closeValue.size(); i++) {
			DatabaseManager.getInstance().insertIntoDatabase(connection, dates.get(i), closeValue.get(i), aktie);
		}
		System.out.println("Datenbank wurde erfolgreich updated.");
	}

	public static void showChart(Connection connection, String aktie) throws SQLException {
		DatabaseManager.selectALL(aktie, connection, sortetCloseValue, sortetDates);
		DatabaseManager.selectAvg(aktie, connection, sortetAvgCloseValue, sortetDates);
		for (int i = 0; i < sortetAvgCloseValue.size(); i++) {
			DatabaseManager.insertAVG(connection, aktie, sortetAvgCloseValue.get(i), sortetDates.get(i));
		}
		Application.launch();
		System.out.println("Chart wurde erfolgreich gezeichnet!");
	}

	// Berechnung des 200er Schnittes (Wird jedoch nicht mehr verwendet)
	static void avgClose() {
		double firstValue;
		double sum = 0;
		int counter = 0;

		for (int i = 0; i < sortetCloseValue.size(); i++) {
			counter++;
			if (counter <= 200) {
				sum = sortetCloseValue.get(i) + sum;
				sortetAvgCloseValue.add(sum / counter);
			}
			if (counter > 200) {
				firstValue = sortetCloseValue.get(i - 200);
				sum = sum - firstValue;
				sum = sum + sortetCloseValue.get(i);
				sortetAvgCloseValue.add(sum / 200);
			}
		}
	}

	public void saveAsPng(Scene scene, String path) {
		WritableImage image = scene.snapshot(null);
		File file = new File(path);
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start(Stage primaryStage) {
		try {
			final CategoryAxis xAxis = new CategoryAxis();
			final NumberAxis yAxis = new NumberAxis();
			xAxis.setLabel("Datum");
			yAxis.setLabel("close-Wert");

			final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
			lineChart.setTitle("Aktienkurs");
			lineChart.setAnimated(false);

			XYChart.Series<String, Number> tatsaechlich = new XYChart.Series();
			tatsaechlich.setName("Close-Werte");
			for (int i = 0; i < sortetCloseValue.size() - 1; i++) {
				tatsaechlich.getData().add(new XYChart.Data(sortetDates.get(i).toString(), sortetCloseValue.get(i)));
			}

			XYChart.Series<String, Number> durchschnitt = new XYChart.Series();
			durchschnitt.setName("AVG Wert");
			for (int i = 0; i < sortetAvgCloseValue.size() - 1; i++) {
				durchschnitt.getData().add(new XYChart.Data(sortetDates.get(i).toString(), sortetAvgCloseValue.get(i)));
			}
			Scene scene = new Scene(lineChart, 800, 600);
			lineChart.getData().add(tatsaechlich);
			lineChart.getData().add(durchschnitt);

			if (sortetCloseValue.get(sortetCloseValue.size() - 1) >= sortetAvgCloseValue
					.get(sortetAvgCloseValue.size() - 1)) {
				scene.getStylesheets().add("green.css");
			}
			if (sortetCloseValue.get(sortetCloseValue.size() - 1) < sortetAvgCloseValue
					.get(sortetAvgCloseValue.size() - 1)) {
				scene.getStylesheets().add("red.css");
			}

			lineChart.setCreateSymbols(false);
			

			primaryStage.setScene(scene);
			primaryStage.show();
			saveAsPng(scene, "C:\\Users\\Maximilian Neuner\\Documents\\chartImages\\chart"+aktie+".png");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
