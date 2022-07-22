package hr.algebra.teamymobileapp.framework

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hr.algebra.teamymobileapp.R
import hr.algebra.teamymobileapp.models.InviteUserInfo
import org.json.JSONObject

class AdapterInvites(private val context: Context, private val invitesUserInfo: InviteUserInfo) :
    RecyclerView.Adapter<AdapterInvites.ViewHolderAdapter>() {
    var volleyRequestQueue: RequestQueue? = null
    val TAG = "Adapter_InviteActivity"

    class ViewHolderAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamName: TextView = itemView.findViewById(R.id.tvTeamName)
        val btnDeny = itemView.findViewById<FloatingActionButton>(R.id.btnDeny)
        val btnAccept = itemView.findViewById<FloatingActionButton>(R.id.btnCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAdapter {
        val view = LayoutInflater.from(context).inflate(
            R.layout.model_invite_item,
            parent, false
        )
        return ViewHolderAdapter(view)
    }

    override fun onBindViewHolder(holder: AdapterInvites.ViewHolderAdapter, position: Int) {
        holder.teamName.text = invitesUserInfo[position].teamName
        holder.btnDeny.setOnClickListener {
            declineInvite(position)
        }
        holder.btnAccept.setOnClickListener {
            acceptInvite(position)
        }
    }

    private fun declineInvite(position: Int) {

        val item = invitesUserInfo[position]
        // google volley delete team on server
        volleyRequestQueue = Volley.newRequestQueue(context)

        val postData = JSONObject()
        postData.put("UserId", context.toString())
        postData.put("TeamName", item.teamName)
        val uri = "https://oicarapi.azurewebsites.net/api/Teams/DismissJoinTeamThroughInvite"

        Log.e(TAG, postData.toString())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, uri, postData,
            {
                // TODO: fix com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of
                Toast.makeText(context, context.getString(R.string.sending_decline), Toast.LENGTH_LONG).show()
                invitesUserInfo.removeAt(position)
                notifyDataSetChanged() // notify observer
            }
        ) { error ->
            error.printStackTrace()
            Toast.makeText(context, context.getString(R.string.sending_accept), Toast.LENGTH_LONG).show()
            invitesUserInfo.removeAt(position)
            notifyDataSetChanged() // notify observer
        }
        // Adding request to request queue
        volleyRequestQueue?.add(jsonObjectRequest)
    }

    private fun acceptInvite(position: Int) {

        val item = invitesUserInfo[position]
        // google volley delete team on server
        volleyRequestQueue = Volley.newRequestQueue(context)

        val postData = JSONObject()
        postData.put("UserId", context.toString())
        postData.put("TeamName", item.teamName)
        val uri = "https://oicarapi.azurewebsites.net/api/Teams/JoinTeamThroughInvite"

        Log.e(TAG, postData.toString())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, uri, postData,
            {
                // TODO: fix com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of
                Toast.makeText(context, context.getString(R.string.sending_accept), Toast.LENGTH_LONG).show()
                invitesUserInfo.removeAt(position)
                notifyDataSetChanged() // notify observer
            }
        ) { error ->
            error.printStackTrace()
            Toast.makeText(context, context.getString(R.string.sending_accept), Toast.LENGTH_LONG).show()
            invitesUserInfo.removeAt(position)
            notifyDataSetChanged() // notify observer
        }
        // Adding request to request queue
        volleyRequestQueue?.add(jsonObjectRequest)
    }

    override fun getItemCount(): Int {
        return invitesUserInfo.size
    }

}