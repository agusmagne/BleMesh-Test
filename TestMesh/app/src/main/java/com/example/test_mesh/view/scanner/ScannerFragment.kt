package com.example.test_mesh.view.scanner

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.transition.TransitionManager
import com.example.test_mesh.R
import com.example.test_mesh.Utils
import com.example.test_mesh.adapter.DevicesAdapter
import com.example.test_mesh.ble.BleMeshManager
import com.example.test_mesh.di.viewmodels.ViewModelFactoryProviders
import com.example.test_mesh.model.datamodels.scanner.ExtendedBluetoothDevice
import com.example.test_mesh.model.datamodels.scanner.ScannerStateLiveData
import com.example.test_mesh.viewmodel.scanner.ScannerViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_scanner.*
import no.nordicsemi.android.log.LogContract
import no.nordicsemi.android.log.LogSession
import no.nordicsemi.android.log.Logger
import javax.inject.Inject

class ScannerFragment : DaggerFragment() {

    @Inject
    lateinit var providersFactory: ViewModelFactoryProviders
    private lateinit var viewmodel: ScannerViewModel
    private val REQUEST_ACCESS_FINE_LOCATION = 1022
    private lateinit var devicesAdapter: DevicesAdapter
    private var scanWithProxyService: Boolean = true
    private var logSession: LogSession? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewmodel =
            ViewModelProvider(this, providersFactory).get(ScannerViewModel::class.java)

        arguments?.let {
            scanWithProxyService = it.getBoolean(Utils.EXTRA_DATA_PROVISIONING_SERVICE, true)
        }

        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        logSession = Logger.newSession(requireContext(), "key", "ScannerFragment")

        devicesAdapter =
            DevicesAdapter(
                viewLifecycleOwner,
                viewmodel.scannerRepository.getScanner()
            ) { device -> onDeviceClickListener(device) }

        nodes_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = devicesAdapter
            (this.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }

        viewmodel.scannerRepository.getScannerState().observe(viewLifecycleOwner,
            { scannerState -> onScannerStateChange(scannerState) }
        )

        scanBtn.setOnClickListener {
            startScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Utils.PROVISIONING_SUCCESS -> {
                if (resultCode == RESULT_OK) {
                    Utils.toast(requireContext(), "PRIVISIONING SUCCESS")
                }
            }
        }
    }

    override fun onStop() {
        stopScan()
        super.onStop()
    }

    private fun startScan() {
        if (Utils.isLocationPermissionsGranted(requireContext())) {
            val state = viewmodel.scannerRepository.getScannerState()
            if (state.isBluetoothEnabled()) {
                if (!state.isScanning()) {
                    viewmodel.nrfMeshRepository.meshManagerApi.loadMeshNetwork()
                    viewmodel.scannerRepository
                        .startScan()
                } else {
                    stopScan()
                }

                showProgress(state.isScanning())

            } else {
                Utils.toast(requireContext(), "BLUETOOTH NOT ENABLED")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.BLUETOOTH), 0
                )
            }
        } else {
            Utils.toast(requireContext(), "LOCATION NOT PERMITTED")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ACCESS_FINE_LOCATION
            )

        }
    }

    private fun stopScan() {
        showProgress(false)
        viewmodel.scannerRepository.stopScan()
    }

    private fun onScannerStateChange(scannerState: ScannerStateLiveData) {
        if (!scannerState.isEmpty()) {
            showNoDevices(false)
        }
    }

    private fun showNoDevices(show: Boolean) {
        TransitionManager.beginDelayedTransition(fragment_scanner)
        val v = if (show) View.VISIBLE else View.INVISIBLE
        no_devices.visibility = v
    }

    private fun showProgress(isScanning: Boolean) {
        if (isScanning) {
            scanBtn.text = "STOP SCAN"
            scan_progressbar.visibility = View.VISIBLE
        } else {
            scanBtn.text = "START SCAN"
            scan_progressbar.visibility = View.INVISIBLE
        }
    }

    private fun onDeviceClickListener(device: ExtendedBluetoothDevice) {
        if (viewmodel.nrfMeshRepository.bleMeshManager.isConnected) {
            viewmodel.disconnect()
        }
        val intent: Intent
        if (scanWithProxyService) {
            intent = Intent(requireContext(), ProvisioningActivity::class.java)
            intent.putExtra(Utils.EXTRA_DEVICE, device)
            startActivityForResult(intent, Utils.PROVISIONING_SUCCESS)
        } else {
//            intent = Intent(requireContext(), ReconnectActivity::class.java)
//            intent.putExtra(Utils.EXTRA_DEVICE, device)
//            startActivityForResult(intent, Utils.CONNECT_TO_NETWORK)
        }
    }
}