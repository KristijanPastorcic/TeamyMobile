package hr.algebra.teamymobileapp.framework

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hr.algebra.teamymobileapp.R
import hr.algebra.teamymobileapp.models.TeamInfo
import org.json.JSONObject


class Adapter(private val context: Context, private val teamInfo: TeamInfo) :
    RecyclerView.Adapter<Adapter.ViewHolderAdapter>() {
    var volleyRequestQueue: RequestQueue? = null
    val TAG = "Adapter_LoginActivity"

    class ViewHolderAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamName: TextView = itemView.findViewById(R.id.etTeamName)
        val dateCreated: TextView = itemView.findViewById(R.id.tvDate)
        val owner: TextView = itemView.findViewById(R.id.tvOwner)
        val btnDelete = itemView.findViewById<FloatingActionButton>(R.id.btn_delete_teams)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAdapter {
        val view = LayoutInflater.from(context).inflate(
            R.layout.model_team_list_item,
            parent, false
        )
        view.setOnClickListener{
            val activity = context as Activity
            activity.goToMain()
        }
        return ViewHolderAdapter(view)
    }

    override fun onBindViewHolder(holder: ViewHolderAdapter, position: Int) {
        holder.teamName.text = teamInfo[position].name
        holder.dateCreated.text = teamInfo[position].dateCreated
        holder.owner.text = teamInfo[position].ownerName.toString()
        val id = teamInfo[position].id
        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle(context.getString(R.string.delete))
                setMessage("'${teamInfo[position].name}?'")
                setIcon(R.drawable.delete)
                setCancelable(true)
                setPositiveButton(context.getString(R.string.yes)) { _, _ -> deleteItem(position) }
                setNegativeButton(context.getString(R.string.cancel), null)
                show()
            }
            true
        }
    }

    private fun deleteItem(position: Int) {
        val item = teamInfo[position]
        // google volley delete team on server
        volleyRequestQueue = Volley.newRequestQueue(context)

        val postData = JSONObject()
        postData.put("TeamId", item.id.toString())
        postData.put("UserName", "")
        val uri = "https://oicarapi.azurewebsites.net/api/Teams/DeleteTeamWithID"

        Log.e(TAG, postData.toString())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, uri, postData,
            {
                // TODO: fix com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of
                Toast.makeText(context, context.getString(R.string.successful_delete), Toast.LENGTH_LONG).show()
            }
        ) { error ->
            error.printStackTrace()
            Toast.makeText(context, context.getString(R.string.successful_delete), Toast.LENGTH_LONG).show()
            teamInfo.removeAt(position)
            notifyDataSetChanged() // notify observer
        }
        // Adding request to request queue
        volleyRequestQueue?.add(jsonObjectRequest)

    }

    override fun getItemCount(): Int {
        return teamInfo.size
    }
}