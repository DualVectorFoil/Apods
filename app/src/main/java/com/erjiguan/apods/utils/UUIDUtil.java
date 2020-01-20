package com.erjiguan.apods.utils;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

public class UUIDUtil {

    private final ParcelUuid[] AIRPODS_UUID_LIST = {
            ParcelUuid.fromString("74ec2172-0bad-4d01-8f77-997b2be0722a"),
            ParcelUuid.fromString("2a72e02b-7b99-778f-014d-ad0b7221ec74")
    };

    public boolean verifyUUID(BluetoothDevice device) {
        ParcelUuid[] uuidList = device.getUuids();
        if (uuidList == null) {
            return false;
        }

        for (ParcelUuid uuid : uuidList) {
            for (ParcelUuid airpodsUuid : AIRPODS_UUID_LIST) {
                if (uuid.equals(airpodsUuid)) {
                    return true;
                }
            }
        }
        return false;
    }
}
