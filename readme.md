Sinn des Programms: Dieses Programm soll dazu dienen Aktien close-Werte und das Datum des jeweiligen Wertes aus einer API auszulesen und in einer SQL Datenbank zu speichern. 
Das Programm soll die verwendeten Aktien aus einer .txt Datei auslesen.
Des weiteren soll der 200er Schnitt für jeden Tag berechnet werden und ebenfalls in der Datenbank abgespeichert werden. Am Ende soll noch mit Java fx ein Chart gezeichnet werden wo
die Close Werte und der 200er Schnitt abgebildet sein. Das Programm soll darauf achten, ob der letzte Close Wert über den Mittelwert liegt, und den Chart Hintergrund dann dementsprechend Grün färben.
Falls er drunter liegt soll der Chart Rot sein. Dieser Chart wird dann als eine .png Datei abgespeichert.

![image](https://user-images.githubusercontent.com/55537941/106002885-ead15f80-60b1-11eb-9b1d-d9fcded9f116.png)

Verwendete API:
https://www.alphavantage.co/ 

Verwendete Libarys:
https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
https://mvnrepository.com/artifact/commons-io/commons-ior
https://mvnrepository.com/artifact/org.json/json/20140107
https://gluonhq.com/products/javafx/
