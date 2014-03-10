package com.mxply.muei.apm.pr3;

import android.location.Location;

public class LocationResult
{
	 String address;
		Location location;
		 String method;
		 public String getAddress() {
				return address;
			}
			public void setAddress(String address) {
				this.address = address;
			}
			 public Location getLocation() {
					return location;
				}
				public void setLocation(Location location) {
					this.location = location;
				}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	@Override
	public String toString() {
		if (location!=null)
			return method + " - (" + location.getLatitude() + ", " + location.getLongitude() + ")";
		else
			return super.toString();
	}
}