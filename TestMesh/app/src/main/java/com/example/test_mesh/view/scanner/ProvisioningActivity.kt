package com.example.test_mesh.view.scanner

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.test_mesh.R
import com.example.test_mesh.Utils
import com.example.test_mesh.di.viewmodels.ViewModelFactoryProviders
import com.example.test_mesh.model.datamodels.scanner.ExtendedBluetoothDevice
import com.example.test_mesh.viewmodel.scanner.ProvisioningViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_provisioning.*
import no.nordicsemi.android.log.LogContract
import no.nordicsemi.android.log.LogSession
import no.nordicsemi.android.log.Logger
import no.nordicsemi.android.mesh.utils.AuthenticationOOBMethods
import javax.inject.Inject

class ProvisioningActivity : DaggerAppCompatActivity() {

    private var logSession: LogSession? = null

    @Inject
    lateinit var providersFactory: ViewModelFactoryProviders

    private lateinit var viewmodel: ProvisioningViewModel

    private lateinit var mDevice: ExtendedBluetoothDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provisioning)

        if (logSession == null) logSession =
            Logger.newSession(application, "Provisioning Activity", "Provisioning Activity")

        val device = intent.getParcelableExtra<ExtendedBluetoothDevice>(Utils.EXTRA_DEVICE)
        val deviceName = device?.name
        val deviceAddress = device?.device?.address

        device_name.text = deviceName
        device_address.text = deviceAddress

        device?.let { mDevice = it }

        viewmodel = ViewModelProvider(this, providersFactory)[ProvisioningViewModel::class.java]

        viewmodel.nrfMeshRepository.isConnected.observe(
            this,
            { isConnected -> onConnectionChange(isConnected) })

        viewmodel.nrfMeshRepository.connectionState.observe(this,
            { connectionState -> onConnectionStateChange(connectionState) })

        viewmodel.nrfMeshRepository.unprovisionedMeshNodeLiveData.observe(this, {
            if (it != null) {
                val capabilities = it.provisioningCapabilities
                Logger.log(logSession, LogContract.Log.Level.DEBUG, "CAPABILITIES")
                if (capabilities != null) {
                    Logger.log(logSession, LogContract.Log.Level.DEBUG, "CAPABILITIES - NOT NULL")
                    val network = viewmodel.nrfMeshRepository.meshNetworkLiveData.getNetwork()
                    if (network != null) {
                        Logger.log(logSession, LogContract.Log.Level.DEBUG, "NETWORK - NOT NULL")
                        val elementCount = capabilities.numberOfElements.toInt()
                        val provisioner = network.selectedProvisioner
                        val unicast = network.nextAvailableUnicastAddress(elementCount, provisioner)
                        network.assignUnicastAddress(unicast)
                    }
                }
            }
        })

        device?.let {
            mDevice = device
            viewmodel.connect(this, it, false)
        }

        identifyBtn.setOnClickListener {
            val node = viewmodel.nrfMeshRepository.unprovisionedMeshNodeLiveData.value
            if (node == null) {
                viewmodel.nrfMeshRepository.identifyNode(mDevice)
                return@setOnClickListener
            }
            Logger.log(logSession, LogContract.Log.Level.DEBUG, "NODE != NULL")
            if (node.provisioningCapabilities != null) {
                onNoOOBSelected()
//                if (node.provisioningCapabilities.availableOOBTypes.size == 1 &&
//                    node.provisioningCapabilities.availableOOBTypes[0] ==
//                    AuthenticationOOBMethods.NO_OOB_AUTHENTICATION
//                ) {
//                    onNoOOBSelected()
//                }
            }

        }

    }

    private fun onConnectionChange(isConnected: Boolean) {
        val text = if (isConnected) "Connected!" else "Not connected"
        Utils.toast(this, text)

    }

    private fun onConnectionStateChange(connectionState: String) {
        Utils.toast(this, connectionState)
    }

    private fun onNoOOBSelected() {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onNoOOBSelected")
        val node = viewmodel.nrfMeshRepository.unprovisionedMeshNodeLiveData.value
        if (node != null) {
            try {
                viewmodel.nrfMeshRepository.meshManagerApi.startProvisioning(node)
            } catch (e: IllegalArgumentException) {
                Logger.log(logSession, LogContract.Log.Level.DEBUG, e.stackTraceToString())


            }
        } else {
            Logger.log(logSession, LogContract.Log.Level.DEBUG, "NODE == NULL")
        }
    }
}