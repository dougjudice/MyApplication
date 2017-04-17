package com.example.dougjudice.uncharted;

/**
 * Created by dougjudice on 4/9/17.
 */

// Put any Service-critical functions here and have the class requiring the service's timer implement this class
public interface ServiceCallback {
    void forceLocationUpdate();
}
