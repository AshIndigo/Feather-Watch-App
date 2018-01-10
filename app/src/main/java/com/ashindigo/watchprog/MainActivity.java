package com.ashindigo.watchprog;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    static BatteryManager bm;
    private ListView listView;
    protected ArrayList<BluetoothDevice> deviceList;
    protected ArrayList<String> deviceNameList;
    private ArrayAdapter<String> adapter;
    private BluetoothLeScanner leScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BLEGattCallback gattCallback;
    private ScanCallback scanCallback;
    public static BluetoothGatt gattD;
    private BroadcastReceiver broadcastReceiver = null;
    private LinearLayout optionsLayout;

    public MainActivity() {
        super();
        deviceList = new ArrayList<>();
        deviceNameList = new ArrayList<>();
        settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        filters = new ArrayList<>();
        gattCallback = new BLEGattCallback(this);
        leScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice btDevice = result.getDevice();
                if (!deviceList.contains(btDevice)) {
                    if (result.getScanRecord() != null) {
                        if (!parseUUIDs(result.getScanRecord().getBytes()).contains(BLEGattCallback.UART_UUID)) {
                            return;
                        }
                    }
                    deviceList.add(btDevice);
                    deviceNameList.add(btDevice.getAddress());
                    adapter.notifyDataSetChanged();
                }
                //Update the overflow menu
                invalidateOptionsMenu();
            }
            @Override
            public void onBatchScanResults(List<ScanResult> results) {
            }

            @Override
            public void onScanFailed(int errorCode) {

            }
        };
    }

    private OnNavigationItemSelectedListener itemListener = new OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    listView.setVisibility(View.VISIBLE);
                    optionsLayout.setVisibility(View.INVISIBLE);
                    scanDevices();
                    adapter.notifyDataSetChanged();
                    return true;
                case R.id.navigation_dashboard:
                    listView.setVisibility(View.INVISIBLE);
                    optionsLayout.setVisibility(View.INVISIBLE);
                    // Dont know what to put here
                    return true;
                case R.id.navigation_notifications:
                    listView.setVisibility(View.INVISIBLE);
                    optionsLayout.setVisibility(View.VISIBLE);
                    // Settings
                    return true;
            }
            return false;
        }
    };

    private void scanDevices() {
        deviceList.clear();
        deviceNameList.clear();
        if (gattD != null) {
            gattD.disconnect();
            gattD.close();
        }
        adapter.notifyDataSetChanged();
        leScanner.startScan(filters, settings, scanCallback);
    }

    // The mess is real
    // It is so real
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        //Calendar cal = Calendar.getInstance();
        //Log.i("WatchProg", "T|" + (cal.get(Calendar.YEAR) - 2000) + "|" + cal.get(Calendar.MONTH) + "|" + cal.get(Calendar.DAY_OF_MONTH) + "|" + cal.get(Calendar.HOUR_OF_DAY) + "|" + cal.get(Calendar.MINUTE) + "|" + cal.get(Calendar.SECOND) + "|" + cal.get(Calendar.DAY_OF_WEEK));
        final ArrayList<AppData> dataModels = new ArrayList<>();
        ListView appList = (ListView) findViewById(R.id.appList);
        optionsLayout = (LinearLayout) findViewById(R.id.options);
        optionsLayout.setVisibility(View.INVISIBLE);
        if(!isNotificationServiceEnabled()) {
            AlertDialog notifListenerDialog = buildNotifAlterDialog();
            notifListenerDialog.show();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.ashindigo.watchprog.notificationlistener");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
        startService(new Intent(this, WatchService.class));
        List<ApplicationInfo> installedApplications = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for (int i = 0; installedApplications.size() > i; i++) {
            try {
                if (getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(installedApplications.get(i).packageName, PackageManager.GET_META_DATA)) != null) {
                    dataModels.add(new AppData(getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(installedApplications.get(i).packageName, PackageManager.GET_META_DATA)).toString(), false));
                    final CheckboxListAdapter adapter0 = new CheckboxListAdapter(dataModels, getApplicationContext());
                    appList.setAdapter(adapter0);
                    appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView parent, View view, int position, long id) {
                            AppData dataModel= dataModels.get(position);
                            dataModel.checked = !dataModel.checked;
                            adapter0.notifyDataSetChanged();
                        }
                    });
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        listView = (ListView) findViewById(R.id.bleList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNameList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                    gattD = deviceList.get(position).connectGatt(getApplicationContext(), false, gattCallback);
                    }
                });
            }
        });
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(itemListener);
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    // Thanks adafruit!
    private List<UUID> parseUUIDs(final byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<>();

        int offset = 0;
        while (offset < (advertisedData.length - 2)) {
            int len = advertisedData[offset++];
            if (len == 0)
                break;

            int type = advertisedData[offset++];
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (len > 1) {
                        int uuid16 = advertisedData[offset++];
                        uuid16 += (advertisedData[offset++] << 8);
                        len -= 2;
                        uuids.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                    }
                    break;
                case 0x06:// Partial list of 128-bit UUIDs
                case 0x07:// Complete list of 128-bit UUIDs
                    // Loop through the advertised 128-bit UUID's.
                    while (len >= 16) {
                        try {
                            // Wrap the advertised bits and order them.
                            ByteBuffer buffer = ByteBuffer.wrap(advertisedData, offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
                            long mostSignificantBit = buffer.getLong();
                            long leastSignificantBit = buffer.getLong();
                            uuids.add(new UUID(leastSignificantBit, mostSignificantBit));
                        } catch (IndexOutOfBoundsException e) {
                            // Defensive programming.
                            //Log.e(LOG_TAG, e.toString());
                        } finally {
                            // Move the offset to read the next uuid.
                            offset += 15;
                            len -= 16;
                        }
                    }
                    break;
                default:
                    offset += (len - 1);
                    break;
            }
        }
        return uuids;
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private AlertDialog buildNotifAlterDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Read Notifications?");
        alertDialogBuilder.setMessage("Allows the app to read notifications");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }


}