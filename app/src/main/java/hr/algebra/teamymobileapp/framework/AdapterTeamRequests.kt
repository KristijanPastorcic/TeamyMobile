package hr.algebra.teamymobileapp.framework

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hr.algebra.teamymobileapp.R
import hr.algebra.teamymobileapp.models.TeamInfoItem
import hr.algebra.teamymobileapp.models.TeamRequestInfo
import java.util.HashMap

class AdapterTeamRequests(private val context: Context, val teams: HashMap<TeamInfoItem, TeamRequestInfo>, private val teamRequestsInfo: TeamRequestInfo) :
    RecyclerView.Adapter<AdapterTeamRequests.ViewHolderAdapter>() {

        var volleyRequestQueue: RequestQueue? = null
        val TAG = "Adapter_JoinTeamActivity"

        class ViewHolderAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val teamName: TextView = itemView.findViewById(R.id.tvTeamName)
            val userName: TextView = itemView.findViewById(R.id.tvUserName)
            val btnDeny = itemView.findViewById<FloatingActionButton>(R.id.btnDeny)
            val btnAccept = itemView.findViewById<FloatingActionButton>(R.id.btnCheck)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAdapter {
            val view = LayoutInflater.from(context).inflate(
                R.layout.model_teamreq_item,
                parent, false
            )
            return ViewHolderAdapter(view)
        }

        override fun onBindViewHolder(holder: ViewHolderAdapter, position: Int) {
            holder.teamName.text = getTeamName(teamRequestsInfo[position].teamId)
            holder.userName.text = teamRequestsInfo[position].userName
            holder.btnDeny.setOnClickListener {
                declineInvite(position)
            }
            holder.btnAccept.setOnClickListener {
                acceptInvite(position)
            }
        }

    private fun getTeamName(teamId: String): CharSequence {
        teams.forEach {
            if (it.key.id.toString() == teamId)
                return it.key.name
        }
        return ""
    }

    private fun acceptInvite(position: Int) {

    }

    private fun declineInvite(position: Int) {

    }

    override fun getItemCount(): Int {
        return teamRequestsInfo.size
    }
}