package hmd.ec.a22143_project

data class Request(
    val id: Int,
    val serviceName: String,
    val note: String,
    var status: String
)
