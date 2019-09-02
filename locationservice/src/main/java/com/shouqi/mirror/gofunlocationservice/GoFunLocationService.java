package com.shouqi.mirror.gofunlocationservice;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.shouqi.mirror.gofunlocationservice.dao.LocationBean;
import com.shouqi.mirror.gofunlocationservice.dao.LocationDaoUtils;
import java.math.BigDecimal;
import java.util.List;

public class GoFunLocationService extends Service {

  static final long MIN_TIME = 1 * 1000;
  static final float MIN_DISTANCE = 1.0f;
  static final int TWO_MINUTES = 1000 * 60 * 2;
  static final String ACTION_START_LOCATION = "com.shouqi.mirror.location.service.start";
  static final String ACTION_STOP_LOCATION = "com.shouqi.mirror.location.service.stop";
  static final String ACTION_DELETE_LOCATION = "com.shouqi.mirror.location.service.delete";

  private LocationManager locationManager;
  private Location curLocation;

  private LocationDaoUtils locationDaoUtils;

  private String orderId;

  @Override public void onCreate() {
    super.onCreate();
    initLocation();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    String action = null;
    try {
      action = intent.getAction();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    if (ACTION_START_LOCATION.equals(action)) {
      orderId = intent.getStringExtra("orderId");
      requestLocation();
    } else if (ACTION_STOP_LOCATION.equals(action)) {
      stopLocation();
    } else if (ACTION_DELETE_LOCATION.equals(action)) {
      orderId = intent.getStringExtra("orderId");
      deleteDB();
    } else {
      // TODO: 2018/12/28
    }

    return super.onStartCommand(intent, flags, startId);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  private void initLocation() {
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
  }

  private void insertDB(Location location) {
    LocationBean locationBean = LocationBean.paseLocation(location, orderId);
    locationDaoUtils.insert(locationBean);
  }

  private void deleteDB() {
    locationDaoUtils.deleteByOrderId(orderId);
  }

  private void stopLocation() {
    if (locationManager != null) {
      locationManager.removeUpdates(gpsLocationListener);
      locationManager.removeUpdates(networkListener);
    }
  }

  private void requestLocation() {
    if (locationDaoUtils == null) {
      locationDaoUtils = new LocationDaoUtils(this);
    }
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, gpsLocationListener);
    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, networkListener);
  }

  LocationListener networkListener = new LocationListener() {

    @Override public void onLocationChanged(Location location) {
      if (locationEquals(curLocation, location)) {
        locationManager.removeUpdates(this);
        List<LocationBean> locationBeans = locationDaoUtils.queryBeans();
        Log.e("location", "net location not change :  db count is :" + locationBeans.size());
        return;
      }
      if (isBetterLocation(location, curLocation)) {
        curLocation = location;
      }

      if (curLocation != null) {
        Log.e("location", "net location " + curLocation.getLatitude() + "===" + curLocation.getLongitude());

        insertDB(curLocation);
        locationManager.removeUpdates(this);
      }
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override public void onProviderEnabled(String provider) {
    }

    @Override public void onProviderDisabled(String provider) {
    }
  };
  LocationListener gpsLocationListener = new LocationListener() {

    @Override public void onLocationChanged(Location location) {
      if (locationEquals(curLocation, location)) {
        List<LocationBean> locationBeans = locationDaoUtils.queryBeans();
        locationManager.removeUpdates(this);
        Log.e("location", "gps location not change :  db count is :" + locationBeans.size());
        return;
      }
      if (isBetterLocation(location, curLocation)) {
        locationManager.removeUpdates(networkListener);
        curLocation = location;
      }
      if (curLocation != null) {
        Log.e("location", "gps location " + curLocation.getLatitude() + "===" + curLocation.getLongitude());
        insertDB(curLocation);
        locationManager.removeUpdates(this);
      }
    }

    @Override public void onProviderDisabled(String provider) {
    }

    @Override public void onProviderEnabled(String provider) {
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {

    }
  };

  protected boolean isBetterLocation(Location location, Location currentBestLocation) {
    if (currentBestLocation == null) {
      // A new location is always better than no location
      return true;
    }

    // Check whether the new location fix is newer or older
    long timeDelta = location.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    boolean isNewer = timeDelta > 0;

    // If it's been more than two minutes since the current location, use the new location
    // because the user has likely moved
    if (isSignificantlyNewer) {
      return true;
      // If the new location is more than two minutes older, it must be worse
    } else if (isSignificantlyOlder) {
      return false;
    }

    // Check whether the new location fix is more or less accurate
    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
    boolean isLessAccurate = accuracyDelta > 0;
    boolean isMoreAccurate = accuracyDelta < 0;
    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

    // Check if the old and new location are from the same provider
    boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

    // Determine location quality using a combination of timeliness and accuracy
    if (isMoreAccurate) {
      return true;
    } else if (isNewer && !isLessAccurate) {
      return true;
    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
      return true;
    }
    return false;
  }

  /** Checks whether two providers are the same */
  private boolean isSameProvider(String provider1, String provider2) {
    if (provider1 == null) {
      return provider2 == null;
    }
    return provider1.equals(provider2);
  }

  private boolean locationEquals(Location oldLocation, Location curLocation) {
    if (oldLocation == null || curLocation == null) {
      return false;
    }

    if (!doubleEquals(oldLocation.getLatitude(), curLocation.getLatitude())) {
      return false;
    }

    if (!doubleEquals(oldLocation.getLongitude(), curLocation.getLongitude())) {
      return false;
    }
    return true;
  }

  private boolean doubleEquals(double d1, double d2) {
    BigDecimal bda = new BigDecimal(d1);
    BigDecimal bdb = new BigDecimal(d2);
    if (bda.compareTo(bdb) == 0) {
      return true;
    } else {
      return false;
    }
  }
}
