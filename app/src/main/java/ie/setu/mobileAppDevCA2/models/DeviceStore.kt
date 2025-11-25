package ie.setu.mobileAppDevCA2.models;


interface DeviceStore {
    fun update(device: DeviceModel)
    fun findAll(): List<DeviceModel>
    fun create(device: DeviceModel)
    fun delete(device: DeviceModel)

    fun findById(id:Long) : DeviceModel?



}
