package hr.algebra.teamymobileapp.models

import java.io.Serializable

data class TeamInfoItem (
    val dateCreated: String,
    val id: Int,
    val name: String,
    val ownerID: Int,
    var ownerName: Any?,
    val teacherID: Int,
    val teacherName: Any?
) : Serializable {
    override fun toString(): String {
        return name
    }
}

