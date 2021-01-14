package com.example.test_mesh.model.repositories

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.test_mesh.Utils
import com.example.test_mesh.ble.BleMeshManager
import com.example.test_mesh.model.datamodels.scanner.ExtendedBluetoothDevice
import com.example.test_mesh.model.datamodels.scanner.MeshNetworkLiveData
import no.nordicsemi.android.log.LogContract
import no.nordicsemi.android.log.LogSession
import no.nordicsemi.android.log.Logger
import no.nordicsemi.android.mesh.*
import no.nordicsemi.android.mesh.provisionerstates.ProvisioningState
import no.nordicsemi.android.mesh.provisionerstates.UnprovisionedMeshNode
import no.nordicsemi.android.mesh.transport.ControlMessage
import no.nordicsemi.android.mesh.transport.MeshMessage
import no.nordicsemi.android.mesh.transport.ProvisionedMeshNode
import javax.inject.Inject

class NrfMeshRepository @Inject constructor(
    val meshManagerApi: MeshManagerApi,
    val bleMeshManager: BleMeshManager
) : MeshManagerCallbacks, MeshProvisioningStatusCallbacks, MeshStatusCallbacks {

    private var meshNetwork: MeshNetwork? = null
    private var logSession: LogSession? = null

    private val ATTENTION_TIMER: Int = 5
    var unprovisionedMeshNode: UnprovisionedMeshNode? = null
    var provisionedNode: ProvisionedMeshNode? = null

    val isConnected = MutableLiveData<Boolean>()
    val connectionState = MutableLiveData<String>()
    val unprovisionedMeshNodeLiveData = MutableLiveData<UnprovisionedMeshNode>()
    val provisionedMeshNodeLiveData = MutableLiveData<ProvisionedMeshNode>()
    val meshNetworkLiveData = MeshNetworkLiveData()
    val provisionedNodes = MutableLiveData<List<ProvisionedMeshNode>>()

    init {
        meshManagerApi.setMeshManagerCallbacks(this)
        meshManagerApi.setProvisioningStatusCallbacks(this)
        meshManagerApi.setMeshStatusCallbacks(this)
        meshManagerApi.loadMeshNetwork()
        logSession = bleMeshManager.logSession
    }

    override fun onNetworkLoaded(meshNetwork: MeshNetwork?) {
        Logger.log(
            logSession,
            LogContract.Log.Level.DEBUG,
            "onNetworkLoaded"
        )
        loadNetwork(meshNetwork)
        loadGroup()

    }

    override fun onNetworkUpdated(meshNetwork: MeshNetwork?) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onNetworkUpdated")
    }

    override fun onNetworkLoadFailed(error: String?) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onNetworkLoadFailed")

    }

    override fun onNetworkImported(meshNetwork: MeshNetwork?) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onNetworkImported")

    }

    override fun onNetworkImportFailed(error: String?) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onNetworkImportFailed")

    }

    override fun sendProvisioningPdu(meshNode: UnprovisionedMeshNode?, pdu: ByteArray?) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "sendProvisioningPdu?")
        if (pdu != null) {
            bleMeshManager.sendPdu(pdu)
            Logger.log(logSession, LogContract.Log.Level.DEBUG, "sendProvisioningPdu? OK")
        } else {
            Logger.log(logSession, LogContract.Log.Level.DEBUG, "sendProvisioningPdu? FAIL")
        }


    }

    override fun onMeshPduCreated(pdu: ByteArray?) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onMeshPduCreated")

    }

    override fun getMtu(): Int {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "getMtu")
        return bleMeshManager.getMaximumPacketSize()
    }

    override fun onProvisioningStateChanged(
        meshNode: UnprovisionedMeshNode?,
        state: ProvisioningState.States?,
        data: ByteArray?
    ) {
        Logger.log(
            logSession,
            LogContract.Log.Level.DEBUG,
            "onProvisioningStateChanged - /${state?.name}/"
        )
        unprovisionedMeshNode = meshNode
        unprovisionedMeshNodeLiveData.postValue(meshNode)
    }

    override fun onProvisioningFailed(
        meshNode: UnprovisionedMeshNode?,
        state: ProvisioningState.States?,
        data: ByteArray?
    ) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onProvisioningFailed")

    }

    override fun onProvisioningCompleted(
        meshNode: ProvisionedMeshNode?,
        state: ProvisioningState.States?,
        data: ByteArray?
    ) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onProvisioningCompleted")
        provisionedNode = meshNode
        unprovisionedMeshNodeLiveData.postValue(null)
        provisionedMeshNodeLiveData.postValue(meshNode)
        if (state == ProvisioningState.States.PROVISIONING_COMPLETE) {
            onProvisioningCompleted(meshNode)
        }


    }

    override fun onTransactionFailed(dst: Int, hasIncompleteTimerExpired: Boolean) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onTransactionFailed")
    }

    override fun onUnknownPduReceived(src: Int, accessPayload: ByteArray?) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onUnknownPduReceived")
    }

    override fun onBlockAcknowledgementProcessed(dst: Int, message: ControlMessage) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onBlockAcknowledgementProcessed")
    }

    override fun onBlockAcknowledgementReceived(src: Int, message: ControlMessage) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onBlockAcknowledgementReceived")
    }

    override fun onMeshMessageProcessed(dst: Int, meshMessage: MeshMessage) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onMeshMessageProcessed")
    }

    override fun onMeshMessageReceived(src: Int, meshMessage: MeshMessage) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onMeshMessageReceived")
    }

    override fun onMessageDecryptionFailed(meshLayer: String?, errorMessage: String?) {
        Logger.log(logSession, LogContract.Log.Level.DEBUG, "onMessageDecryptionFailed")
    }

    fun connect(context: Context, device: ExtendedBluetoothDevice, connectToNetwork: Boolean) {
        meshNetworkLiveData.setName(device.name)
        bleMeshManager.connect(device.device).retry(3, 200).enqueue()
    }

    fun resetMeshNetwork() {
        disconnect()
        meshManagerApi.resetMeshNetwork()
    }

    fun disconnect() {
        clearProvisioningLiveData()
        bleMeshManager.disconnect().enqueue()
    }

    fun identifyNode(device: ExtendedBluetoothDevice) {
        val beacon = device.beacon as? UnprovisionedBeacon
        if (beacon != null) {
            meshManagerApi.identifyNode(beacon.uuid, ATTENTION_TIMER)
        } else {
            val serviceData =
                Utils.getServiceData(device.scanResult, BleMeshManager.MESH_PROVISIONING_UUID)
            if (serviceData != null) {
                val uuid = meshManagerApi.getDeviceUuid(serviceData)
                meshManagerApi.identifyNode(uuid, ATTENTION_TIMER)
            }
        }
    }

    fun clearProvisioningLiveData() {
        provisionedMeshNodeLiveData.value = null
        unprovisionedMeshNodeLiveData.value = null
    }

    private fun onProvisioningCompleted(meshNode: ProvisionedMeshNode?) {

    }

    @SuppressLint("RestrictedApi")
    private fun loadNetwork(meshNetwork: MeshNetwork?) {
        this.meshNetwork = meshNetwork
        if (meshNetwork != null) {

            if (!meshNetwork.isProvisionerSelected) {
                val provisioner = meshNetwork.provisioners[0]
                provisioner.isLastSelected = true
                meshNetwork.selectProvisioner(provisioner)
            }

            meshNetworkLiveData.loadNetworkInformation(meshNetwork)
            loadNodes()


        }

    }

    private fun loadGroup() {

    }

    @SuppressLint("RestrictedApi")
    private fun loadNodes() {
        val nodes = mutableListOf<ProvisionedMeshNode>()
        meshNetwork?.nodes?.forEach {
            if (!it.uuid.equals(meshNetwork?.selectedProvisioner?.provisionerUuid, true)) {
                nodes.add(it)
            }
        }
        provisionedNodes.postValue(nodes)
    }

//    fun clearProvisioningLiveData() {
//        stopScan()
//        mHandler.removeCallbacks(mReconnectRunnable)
//        mSetupProvisionedNode = false
//        mIsReconnectingFlag = false
//        mUnprovisionedMeshNodeLiveData.setValue(null)
//        mProvisionedMeshNodeLiveData.setValue(null)
//    }
}