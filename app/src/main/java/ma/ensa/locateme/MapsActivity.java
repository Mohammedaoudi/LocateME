package ma.ensa.locateme;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String showUrl = "http://10.0.2.2/tpmap/showPositions.php"; // Ensure this URL is reachable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); // Make sure this layout file exists

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fetchAndRefreshMap(); // Fetch and refresh markers when the map is ready
    }

    private void fetchAndRefreshMap() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, // Use POST method if needed
                showUrl, // URL
                null, // No JSON request body
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MapsActivity", "Response: " + response.toString()); // Log the response
                        try {
                            mMap.clear(); // Clear existing markers

                            JSONArray positions = response.getJSONArray("positions");
                            if (positions.length() == 0) {
                                Toast.makeText(getApplicationContext(), "No positions found.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            LatLng firstLocation = null;

                            for (int i = 0; i < positions.length(); i++) {
                                JSONObject position = positions.getJSONObject(i);
                                double latitude = position.getDouble("latitude");
                                double longitude = position.getDouble("longitude");
                                LatLng location = new LatLng(latitude, longitude);

                                // Log the marker position
                                Log.d("MapsActivity", "Adding marker at: " + latitude + ", " + longitude);

                                mMap.addMarker(new MarkerOptions()
                                        .position(location)
                                        .title("Marker " + position.getInt("id")));

                                // Save the first marker's position to move the camera later
                                if (i == 0) {
                                    firstLocation = location;
                                }
                            }

                            // Move camera to the first marker
                            if (firstLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10)); // Adjust zoom level as needed
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "JSON parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}
