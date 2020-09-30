package saioapi.service.utility;

import android.os.Bundle;

import java.io.Serializable;

/**
 *  This class provide update response services
 */
public class UpdateRsp implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String BUNDLE_KEY = "update_rsp";

    private int mTotal;
    private int mFinish;
    private float mPercentage;
    private String mMessage = null;

    /**
     * Initializes a new, existing UpdateRsp object with a Serializable object
     *
     * @param message The update message.
     * @param runfinish The number of finish file counter.
     * @param total The total's number of need file counter.
     * @param progess The updating percentage value.
     */
    public UpdateRsp(String message, int runfinish, int total, float progess) {

        mMessage = message;
        mFinish = runfinish;
        mTotal = total;
        mPercentage = progess;
    }

    /**
     * Returns the update response description.
     *
     * @param b The mapping from String values to various Parcelable types.
     *
     * @return  Return the value is update response description. If the function succeeds else return null.
     */
    public static UpdateRsp getBundleData(Bundle b) {

        if(null == b) {
            return null;
        }

        return (UpdateRsp)b.getSerializable(BUNDLE_KEY);
    }

    /**
     * Sets the update response description.
     *
     * @param b The mapping from String values to various Parcelable types.
     * @param updateInfo The value is UpdateRsp description.
     */
    public static void setBundleData(Bundle b, UpdateRsp updateInfo) {

        if(null == b || null == updateInfo) {
            return;
        }

        b.putSerializable(BUNDLE_KEY, updateInfo);
    }

    /**
     * Returns the update message.
     *
     * @return The return value is the update message.
     */
    public String getMessage (){
        return mMessage;
    }

    /**
     * Returns the number of finish file counter.
     *
     * @return The return value is the number of finish file counter.
     */
    public int getRunFinishCount (){
        return mFinish;
    }

    /**
     * Returns the total's number of need file counter.
     *
     * @return The return value is the total's number of need file counter.
     */
    public int getTotalCount (){
        return mTotal;
    }

    /**
     * Returns the updating percentage value.
     *
     * @return The return value is the updating percentage value.
     */
    public float getUpdatePercentage (){
        return mPercentage;
    }
}
