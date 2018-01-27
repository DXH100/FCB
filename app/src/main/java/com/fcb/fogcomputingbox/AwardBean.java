package com.fcb.fogcomputingbox;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * author: denghx
 * date  : 2018/1/22
 * desc  :
 */

public class AwardBean implements Parcelable {
    public String reward;

    /**
     * dailyDate : 2018-01-24
     * dailyReward : 0.001
     */

    public List<DailysBean> dailys;


    public static class DailysBean implements Parcelable {
        public String dailyDate;
        public String dailyReward;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.dailyDate);
            dest.writeString(this.dailyReward);
        }

        public DailysBean() {
        }

        protected DailysBean(Parcel in) {
            this.dailyDate = in.readString();
            this.dailyReward = in.readString();
        }

        public static final Creator<DailysBean> CREATOR = new Creator<DailysBean>() {
            @Override
            public DailysBean createFromParcel(Parcel source) {
                return new DailysBean(source);
            }

            @Override
            public DailysBean[] newArray(int size) {
                return new DailysBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.reward);
        dest.writeList(this.dailys);
    }

    public AwardBean() {
    }

    protected AwardBean(Parcel in) {
        this.reward = in.readString();
        this.dailys = new ArrayList<DailysBean>();
        in.readList(this.dailys, DailysBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<AwardBean> CREATOR = new Parcelable.Creator<AwardBean>() {
        @Override
        public AwardBean createFromParcel(Parcel source) {
            return new AwardBean(source);
        }

        @Override
        public AwardBean[] newArray(int size) {
            return new AwardBean[size];
        }
    };
}
