package hr.algebra.teamymobileapp.models

data class TeamInfoItem(
    val dateCreated: String,
    val id: Int,
    val name: String,
    val ownerID: Int,
    var ownerName: Any?,
    val teacherID: Int,
    val teacherName: Any?
) {
    override fun toString(): String {
        return name
    }
}

