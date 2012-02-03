package net.atredroid.gpsbasic;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class gpsbasic extends Activity {
    /** Called when the activity is first created. */
	
	private TextView tv_latitude;
	private TextView tv_longitude;
	private TextView tv_altitude;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
	    tv_latitude = (TextView) findViewById(R.id.id_latitude);
	    tv_longitude = (TextView) findViewById(R.id.id_longitude);
	    tv_altitude = (TextView) findViewById(R.id.id_altitude);
	    
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// called when the location provider status changes.
				//Possible status: OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE or AVAILABLE.
			}
			public void onProviderEnabled(String provider) {
				// called when the location provider is enabled by the user
			}
			public void onProviderDisabled(String provider) {
				// called when the location provider is disabled by the user.
				//If it is already disabled, it's called immediately after requestLocationUpdates
			}
			
			public void onLocationChanged(Location location) {
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				
				
				
				if (location.hasAltitude()) {
					double altitude = location.getAltitude();
					String alt = Location.convert(altitude, Location.FORMAT_SECONDS);
					alt = parse_location(alt);
					tv_altitude.setText(alt);
				
				}
				String lat = Location.convert(latitude, Location.FORMAT_SECONDS);
				lat = parse_location(lat);
				tv_latitude.setText(lat);
				String longi = Location.convert(longitude, Location.FORMAT_SECONDS);
				longi = parse_location(longi);
				tv_longitude.setText(longi);
	
			}
		});
    }
    
    static String parse_location(String loc) {
    	
    	if (loc.contains(":")) {
    		//Regular Expression for char :
    		String array[] = loc.split("\\:"); 
    		for (int i = 0; i < array.length; i++) {
    			if (i==0)
    				loc = array[i] + "º";
    			else if (i==1)
    				loc += " " + array[i] + "'";
    			else
    				loc += " " + array[i] + '"';
    		}
    	}
    	return loc;
    }
    
    
    
}