package ma.ensa.locateme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Import TextView
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private double latitude;
    private double longitude;
    private Button btn;
    private TextView locationInfo; // Declare TextView
    private RequestQueue requestQueue;
    String insertUrl = "http://10.0.2.2/tpmap/createPosition.php";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize the TextView
        locationInfo = findViewById(R.id.location_info);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        // Request necessary permissions
        requestPermissions();
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 150, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                // Update the TextView with the latitude and longitude
                String locationText = String.format("Latitude: %.5f, Longitude: %.5f", latitude, longitude);
                locationInfo.setText(locationText); // Set the text in the TextView

                // Optionally, you can still display a toast
                Toast.makeText(getApplicationContext(), locationText, Toast.LENGTH_LONG).show();

                addPosition(latitude, longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                String newStatus = "";
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        newStatus = "OUT_OF_SERVICE";
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        newStatus = "TEMPORARILY_UNAVAILABLE";
                        break;
                    case LocationProvider.AVAILABLE:
                        newStatus = "AVAILABLE";
                        break;
                }
                String msg = String.format(getResources().getString(R.string.provider_new_status), provider, newStatus);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(),
                        String.format(getResources().getString(R.string.provider_enabled), provider),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(),
                        String.format(getResources().getString(R.string.provider_disabled), provider),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    void addPosition(final double lat, final double lon) {
        StringRequest request = new StringRequest(Request.Method.POST, insertUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Position enregistrée avec succès: " + response);
                        Toast.makeText(getApplicationContext(), "Position enregistrée", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Erreur lors de l'enregistrement: " + error.toString());
                        Toast.makeText(getApplicationContext(),
                                "Erreur lors de l'enregistrement de la position",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lon));
                params.put("date", sdf.format(new Date()));
                params.put("imei", Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID)); // Using ANDROID_ID instead of IMEI

                Log.d(TAG, "Sending parameters: " + params.toString()); // Log parameters
                return params;
            }
        };
        requestQueue.add(request);
    }

}
