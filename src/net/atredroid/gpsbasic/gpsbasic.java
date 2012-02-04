package net.atredroid.gpsbasic;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class gpsbasic extends Activity {
    /** Called when the activity is first created. */
	
	private TextView tv_latitude;
	private TextView tv_longitude;
	private TextView tv_altitude;
	private Button bt_gps;
	private boolean tieneGps = false;
	private boolean statusGps = false; 
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tv_latitude = (TextView) findViewById(R.id.id_latitude);
	    tv_longitude = (TextView) findViewById(R.id.id_longitude);
	    tv_altitude = (TextView) findViewById(R.id.id_altitude);
	    bt_gps = (Button) findViewById(R.id.id_button_gps);

        
	    final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> lista = lm.getAllProviders();
        
        Iterator<String> iter = lista.iterator();
        while (iter.hasNext()) {
        	if (((String) iter.next()).contains("gps")) {
        		tieneGps = true;
        		Toast.makeText(getApplicationContext(),"GPS detected" ,Toast.LENGTH_SHORT).show();
        	}
        }
        
        if ((tieneGps) && (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))){
        	statusGps = true;
        	Toast.makeText(getApplicationContext(),R.string.toast_enable ,Toast.LENGTH_SHORT).show();
        	setLocationListener(LocationManager.GPS_PROVIDER,0 , 0);
        }
        else {
        	Toast.makeText(getApplicationContext(),R.string.toast_disable ,Toast.LENGTH_SHORT).show();
        	setLocationListener(LocationManager.NETWORK_PROVIDER,0 , 0);
        }
        
        
        /*
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        
	    
	    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
	    	bt_gps.setText(R.string.button_gps_off);
	    else
	    	bt_gps.setText(R.string.button_gps_on);
	    */
	    bt_gps.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View arg0) {
	        	startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
	        }
	    });
		
		
    }
    
    private void setLocationListener(String type, long minTime, long minDist) {
    	LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	lm.requestLocationUpdates(type, minTime, minDist, new LocationListener() {
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// called when the location provider status changes.
				//Possible status: OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE or AVAILABLE.
			}
			public void onProviderEnabled(String provider) {
				Toast.makeText(getApplicationContext(),"gps on" ,Toast.LENGTH_SHORT).show();
			}
			public void onProviderDisabled(String provider) {
				Toast.makeText(getApplicationContext(),"gps off" ,Toast.LENGTH_SHORT).show();
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
				
				Toast.makeText(getApplicationContext(),R.string.toast_update ,Toast.LENGTH_SHORT).show();
	
			}
		});
    }
    
    private static String parse_location(String loc) {
    	
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