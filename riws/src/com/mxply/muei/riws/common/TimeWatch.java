//author: GHad - http://stackoverflow.com/users/11705/ghad
//url: http://stackoverflow.com/a/1770038
package com.mxply.muei.riws.common;

import java.util.concurrent.TimeUnit;

public class TimeWatch {

    long starts;

    public static TimeWatch start() {
    	return new TimeWatch();
    }

    private TimeWatch() {
    	reset();
    }

    public TimeWatch reset() {
    	starts = System.currentTimeMillis();
    	return this;
    }

    public long time() {
    	long ends = System.currentTimeMillis();
    	return ends - starts;
    }

	public long time(TimeUnit unit) {
		return unit.convert(time(), TimeUnit.MILLISECONDS);
	}

}
