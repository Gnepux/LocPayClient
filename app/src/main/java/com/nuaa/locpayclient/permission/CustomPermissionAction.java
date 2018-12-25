package com.nuaa.locpayclient.permission;

import io.reactivex.Observer;

/**
 * Created by xupeng on 2017/12/15.
 */

public class CustomPermissionAction {

    private boolean mGrant;

    private boolean mRationaleGrant;

    Observer<? super Boolean> mObserver;

    PermissionManager mManager;

    public boolean isGrant() {
        return mGrant;
    }

    public void setGrant(boolean grant) {
        this.mGrant = grant;
        if (mObserver != null) {
            mObserver.onNext(grant);
        }
    }

    public boolean isRationaleGrant() {
        return mRationaleGrant;
    }

    public void setRationaleGrant(boolean rationaleGrant) {
        this.mRationaleGrant = rationaleGrant;
        if (mObserver != null) {
            if (rationaleGrant) {
                if (mManager != null) {
                    mManager.goSettingAction(mObserver);
                }
            } else {
                mObserver.onNext(false);
            }
        }
    }
}
