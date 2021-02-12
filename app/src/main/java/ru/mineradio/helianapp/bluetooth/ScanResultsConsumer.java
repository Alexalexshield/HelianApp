package ru.mineradio.helianapp.bluetooth;

import android.bluetooth.BluetoothDevice;
public interface ScanResultsConsumer {

    public void candidateBleDevice(BluetoothDevice device, byte[] scan_record, int rssi);
    public void scanningStarted();
    public void scanningStopped();
}
