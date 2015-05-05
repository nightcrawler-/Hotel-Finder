package doreen.com.hotelfinder;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import doreen.com.hotelfinder.content.PlaceContract;

/**
 * Created by Doreen on 3/21/2015.
 */
public class Backbone {
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36";

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyCYv81X3mHekEmVXxy-HvE8iAOOSwITwzA";
    public static final String BASE_URL_PLACE_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyCYv81X3mHekEmVXxy-HvE8iAOOSwITwzA";
    public static final String BASE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyCYv81X3mHekEmVXxy-HvE8iAOOSwITwzA&maxheight=1000&photoreference=";


    public static JSONObject getPlaceDetails(String placeId) throws JSONException {
        Log.i("HOTEL", "fetching details");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String result = null;

        try {

            final String PLACE_ID_PARAM = "placeid";


            Uri builtUri = Uri.parse(BASE_URL_PLACE_DETAILS).buildUpon()
                    .appendQueryParameter(PLACE_ID_PARAM, placeId)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            result = buffer.toString();
        } catch (IOException e) {
            Log.e("HOTEL", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("HOTEL", "Error closing stream", e);
                }
            }
        }
        Log.i("HOTEL", "res - " + result);
        JSONObject resultJson = new JSONObject(result);
        JSONObject actualResult = resultJson.getJSONObject("result");


        return actualResult;


    }

    public static JSONArray getData() throws JSONException {
        Log.i("HOTEL", "fetching");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String result = null;

        String location = "0.047035,37.649803";
        String keyword = "hotel";
        String radius = "50000";

        try {

            final String LOCATION_PARAM = "location";
            final String KEYWORD_PARAM = "keyword";
            final String RADIUS_PARAM = "radius";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(LOCATION_PARAM, location)
                    .appendQueryParameter(KEYWORD_PARAM, keyword)
                    .appendQueryParameter(RADIUS_PARAM, radius)

                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            result = buffer.toString();
        } catch (IOException e) {
            Log.e("HOTEL", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("HOTEL", "Error closing stream", e);
                }
            }
        }
        Log.i("HOTEL", "res - " + result);
        JSONObject resultJson = new JSONObject(result);

        JSONArray data = resultJson.getJSONArray("results");

        return data;

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

    }

    public static void insertData(Context context, JSONArray data)  {

        for (int i = 0; i < data.length(); i++) {
            ContentValues locationValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            try {
                locationValues.put(PlaceContract.PlacesEntry.COLUMN_NAME, data.getJSONObject(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                locationValues.put(PlaceContract.PlacesEntry.COLUMN_PLACE_ID, data.getJSONObject(i).getString("place_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                locationValues.put(PlaceContract.PlacesEntry.COLUMN_PHOTO_REFERENCE, data.getJSONObject(i).getJSONArray("photos").getJSONObject(0).getString("photo_reference"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                locationValues.put(PlaceContract.PlacesEntry.COLUMN_ICON, data.getJSONObject(i).getString("icon"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Finally, insert location data into the database.
            Uri insertedUri = context.getContentResolver().insert(
                    PlaceContract.PlacesEntry.CONTENT_URI,
                    locationValues
            );

            Log.i("BAAM", "uri - " + insertedUri);
        }

    }

    /**
     * POST the JSON payload
     *
     * @param data
     * @return a string representation of the contacts uploaded - to update
     * local cache
     * @throws Exception
     */

    public static String sendPost(String data, String url) throws Exception {
        Log.i("CloudWork", "fetching - " + url + " data - " + data);
        URL obj = new URL(BASE_URL);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        // add reuqest header
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(data);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // print result
        Log.i("Moth", "responseCode - " + responseCode);

        Log.i("MothPSOT", "response - " + response);
        if (responseCode == 200)
            return response.toString();
        else
            return null;

    }
}
