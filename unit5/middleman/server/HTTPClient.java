package server;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HTTPClient {

    public static String getRequest(String endpoint) {
        try {
            System.out.println("Fetching...");
            URL url = new URL("http://localhost:8080/" + endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            String result = content.toString();

            System.out.print("Received: ");
            System.out.println(result + "\n");
            return result;
        } catch (ProtocolException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void postRequest(JSONObject payload, String endpoint) {
        System.out.println("Sending payload...");
        System.out.println(payload.toString());
        try {
            URL url = new URL("http://localhost:8080/" + endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("charset", "utf-8");

            con.setDoOutput(true);
            try( DataOutputStream out = new DataOutputStream( con.getOutputStream())) {
                byte[] bytePayload = payload.toString().getBytes("utf-8");
                out.write(bytePayload, 0, bytePayload.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.print("Response: ");
                System.out.println(response.toString());
                System.out.println("Added!\n");
            }
        } catch (ProtocolException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
