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
	private TextView tv_bearing;
	private TextView tv_speed;
	private TextView tv_sats;
	private TextView tv_accuracy;
	private TextView tv_name;
	private TextView tv_status;
	private Button bt_gps;
	
	private LocationManager locManager;
	private LocationListener locListener;
	
	private boolean tieneGps = false;
	private boolean statusGps = false; 
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tv_latitude = (TextView) findViewById(R.id.id_latitude);
	    tv_longitude = (TextView) findViewById(R.id.id_longitude);
	    tv_altitude = (TextView) findViewById(R.id.id_altitude);
	    tv_bearing = (TextView) findViewById(R.id.id_bearing);
	    tv_speed = (TextView) findViewById(R.id.id_speed);
	    tv_sats = (TextView) findViewById(R.id.id_num_sat);
	    tv_accuracy = (TextView) findViewById(R.id.id_accuracy);
	    tv_name = (TextView) findViewById(R.id.id_name);
	    tv_status = (TextView) findViewById(R.id.id_status);
	    bt_gps = (Button) findViewById(R.id.id_button_gps);
	    

        
	    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> lista = locManager.getAllProviders();
        
        Iterator<String> iter = lista.iterator();
        while (iter.hasNext()) {
        	if (((String) iter.next()).contains("gps")) {
        		tieneGps = true;
        		Toast.makeText(getApplicationContext(),"GPS detected" ,Toast.LENGTH_SHORT).show();
        	}
        }
        
        if ((tieneGps) && (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))){
        	statusGps = true;
        	Toast.makeText(getApplicationContext(),R.string.toast_enable ,Toast.LENGTH_SHORT).show();
        	Location aux = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        	setLocationListener(LocationManager.GPS_PROVIDER,0 , 0);
        	showPosition(aux);
        	
        }
        else {
        	if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	        	Toast.makeText(getApplicationContext(),R.string.toast_disable ,Toast.LENGTH_SHORT).show();
	        	setLocationListener(LocationManager.NETWORK_PROVIDER,0 , 0);
        	}
        }

	    bt_gps.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View arg0) {
	        	startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
	        }
	    });
		
		
    }
    
    private void showPosition(Location location) {
    	
    	if (location != null) {
	    	
			String name = location.getProvider();
			if (name != null) {
				tv_name.setText(name);
			}
			else
				tv_name.setText(R.string.label_not_aval);
			
			Bundle extras = location.getExtras();
			int num_sats = extras.getInt("satellites");
			tv_sats.setText(String.valueOf(num_sats));
			
			if (location.hasBearing()) {
				float bearing = location.getBearing();
				tv_bearing.setText(String.valueOf(bearing)+"º");
			}
			else 
				tv_bearing.setText(R.string.label_not_aval);
			
			if (location.hasSpeed()) {
				float speed = location.getSpeed();
				tv_speed.setText(String.valueOf(speed)+" m/s");
			}
			else
				tv_speed.setText(R.string.label_not_aval);
			
			if (location.hasAccuracy()) {
				float accuracy = location.getAccuracy();
				tv_accuracy.setText(String.valueOf(accuracy));
			}
			else
				tv_accuracy.setText(R.string.label_not_aval);
			
			if (location.hasAltitude()) {
				double altitude = location.getAltitude();
				tv_altitude.setText(String.valueOf(altitude));
			}
			else
				tv_altitude.setText(R.string.label_not_aval);
			
			
			String lat = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
			lat = parse_location(lat);
			tv_latitude.setText(lat);
			String longi = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
			longi = parse_location(longi);
			tv_longitude.setText(longi);
			
			Toast.makeText(getApplicationContext(),R.string.toast_update ,Toast.LENGTH_SHORT).show();
    	}
    }
    
    private void setLocationListener(String type, long minTime, long minDist) {
    	
    	
    	locListener = new LocationListener() {
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// called when the location provider status changes.
				//Possible status: OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE or AVAILABLE.
				switch(status) {
					case 0:
						tv_status.setText("off");
					case 1:
						tv_status.setText("medio");
					case 2:
						tv_status.setText("on");
				}
				int num = extras.getInt("satellites");
				tv_sats.setText(String.valueOf(num));
			}
			public void onProviderEnabled(String provider) {
				Toast.makeText(getApplicationContext(),provider+" on" ,Toast.LENGTH_SHORT).show();
			}
			public void onProviderDisabled(String provider) {
				if (provider != LocationManager.GPS_PROVIDER) {
					if ((tieneGps) && (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
						 locManager.removeUpdates(locListener);
						 setLocationListener(LocationManager.GPS_PROVIDER,0,0);
					}
				}
				else {
					if(locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
						locManager.removeUpdates(locListener);
						setLocationListener(LocationManager.NETWORK_PROVIDER,0,0);
					}
				}
				Toast.makeText(getApplicationContext(),provider+" off" ,Toast.LENGTH_SHORT).show();
			}
			
			public void onLocationChanged(Location location) {
				showPosition(location);
	
			}
    	
    	};
    	locManager.requestLocationUpdates(type, minTime, minDist, locListener);
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