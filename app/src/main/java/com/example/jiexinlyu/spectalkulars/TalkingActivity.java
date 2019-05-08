package com.example.jiexinlyu.spectalkulars;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TalkingActivity extends AppCompatActivity {

    private static final String TAG = TalkingActivity.class.getSimpleName();
    private String topic;
    private TextView topic_txt;
    private String username;
    private String matchUsername;

    // Bluetooth related
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;
    BluetoothManager bluetoothManager;
    BluetoothScanCallback bluetoothScanCallback;
    BluetoothGatt gattClient;

    BluetoothGattCharacteristic characteristicID;

    HashMap<String, String> topic_table;

    // UUID's
    final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talking);

        topic = getIntent().getStringExtra("TOPIC");
        topic_txt = findViewById(R.id.talking_topic);
        topic_table = new HashMap<String, String>() {{
            put("Game of Thrones", "B11");
            put("Cat", "B21");
        }};

        topic_txt.setText(topic);
        username = getIntent().getStringExtra("USERNAME");
        matchUsername = getIntent().getStringExtra("MATCHUSER");

        // Bluetooth things
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) return;
        startScan();
    }

    @Override
    protected void onStop() {
        super.onStop();

        disconnect();
    }

    public void disconnect() {
        if (bluetoothAdapter == null || gattClient == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        gattClient.disconnect();
    }

    // BLUETOOTH SCAN
    private void startScan(){
        Log.i(TAG,"startScan()");
        bluetoothScanCallback = new BluetoothScanCallback();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(bluetoothScanCallback);
    }

    // BLUETOOTH CONNECTION
    private void connectDevice(BluetoothDevice device) {
        if (device == null) Log.i(TAG,"Device is null");
        GattClientCallback gattClientCallback = new GattClientCallback();
        gattClient = device.connectGatt(this,false,gattClientCallback);
    }

    private class BluetoothScanCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG, "onScanResult");
            if (result.getDevice().getName() != null){
                if (result.getDevice().getName().equals("Spec") ||result.getDevice().getName().equals("TEST_FEATHER")) {
                    // When find your device, connect.
                    connectDevice(result.getDevice());
                    bluetoothLeScanner.stopScan(bluetoothScanCallback); // stop scan
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.i(TAG, "onBathScanResults");
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.i(TAG, "ErrorCode: " + errorCode);
        }
    }

    // Bluetooth GATT Client Callback
    private class GattClientCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG,"onConnectionStateChange");

            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.i(TAG, "onConnectionStateChange GATT FAILURE");
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onConnectionStateChange != GATT_SUCCESS");
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange CONNECTED");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange DISCONNECTED");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.i(TAG,"onServicesDiscovered");
            if (status != BluetoothGatt.GATT_SUCCESS) return;

            // Reference your UUIDs
            characteristicID = gatt.getService(RX_SERVICE_UUID).getCharacteristic(RX_CHAR_UUID);
            String topicCode = topic_table.get(topic);
            byte[] value = topicCode.getBytes(StandardCharsets.US_ASCII);
            characteristicID.setValue(value);
            boolean writeStatus = gatt.writeCharacteristic(characteristicID);
            Log.d(TAG, "write TXchar - status=" + writeStatus);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i(TAG,"onCharacteristicRead");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i(TAG,"onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.i(TAG,"onCharacteristicChanged");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.i(TAG,"onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.i(TAG,"onDescriptorWrite");
        }
    }

    public void endTalking(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> newUserData = new HashMap<String, Object>() {{
            put("topic", "");
            put("match_user", "");
            put("match", false);
            put("status", "open");
            put("recent", matchUsername);
        }};

        db.collection("users").document(username)
                .set(newUserData, SetOptions.merge());

        Intent intent = new Intent(TalkingActivity.this, MainActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
}
