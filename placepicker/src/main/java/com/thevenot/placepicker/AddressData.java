package com.thevenot.placepicker;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddressData implements Parcelable {

    private double latitude;
    private double longitude;
    private List<Address> addressList;

    public AddressData() {
    }

    public AddressData(double latitude, double longitude, List addressList) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressList = addressList;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public String getAddressString() {
        if (this.addressList != null && !addressList.isEmpty()) {
            return addressList.get(0).getAddressLine(0);
        }

        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressData that = (AddressData) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        return addressList != null ? addressList.equals(that.addressList) : that.addressList == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (addressList != null ? addressList.hashCode() : 0);
        return result;
    }

    private AddressData(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        if (in.readInt() != 0) {
            int listSize = in.readInt();
            this.addressList = new ArrayList<>(listSize);

            for (int i = 0; i < listSize; i++) {
                this.addressList.add((Address) in.readParcelable(AddressData.class.getClassLoader()));
            }
        } else {
            this.addressList = null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        if (this.addressList != null) {
            dest.writeInt(1);
            dest.writeInt(this.addressList.size());
            Iterator iterator = this.addressList.iterator();

            while (iterator.hasNext()) {
                dest.writeParcelable((Address) iterator.next(), flags);
            }
        } else {
            dest.writeInt(0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AddressData> CREATOR = new Creator<AddressData>() {
        @Override
        public AddressData createFromParcel(Parcel in) {
            return new AddressData(in);
        }

        @Override
        public AddressData[] newArray(int size) {
            return new AddressData[size];
        }
    };

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AddressData{");
        sb.append("latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", addressList=").append(addressList);
        sb.append('}');
        return sb.toString();
    }
}
