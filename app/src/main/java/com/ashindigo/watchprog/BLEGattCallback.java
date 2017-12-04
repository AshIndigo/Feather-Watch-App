package com.ashindigo.watchprog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;
import android.util.Log;

import java.util.UUID;


class BLEGattCallback extends BluetoothGattCallback implements BluetoothAdapter.LeScanCallback {

    private final MainActivity mainActivity;
    static BluetoothGattCharacteristic chara;
    // UUIDs for UART service and associated characteristics.
    static UUID UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    private static UUID TX_UUID   = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    //public static UUID RX_UUID   = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    // UUID for the UART BTLE client characteristic which is necessary for notifications.
    //public static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    BLEGattCallback(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        mainActivity.deviceList.add(device);
        mainActivity.deviceNameList.add(device.getName());
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothGatt.STATE_CONNECTED) {
          // if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt.discoverServices();
                //Log.i("WatchProg", TextUtils.join(", ", gatt.getServices()));
           // }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_FAILURE) {
                return;
            }

        Log.i("WatchProg", TextUtils.join(", ", gatt.getServices()));
        chara = gatt.getService(UART_UUID).getCharacteristic(TX_UUID);
        chara.setValue("S|test".getBytes());
        gatt.writeCharacteristic(chara);

    }

}
