package com.codebumble.intrav

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.net.InetAddress

class ServerAdapter(private val servers: MutableList<Server>,
                    private val onEditClick: (Server) -> Unit,
                    private val onDeleteClick: (Server) -> Unit) : RecyclerView.Adapter<ServerAdapter.ServerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.server_item, parent, false)
        return ServerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        val server = servers[position]
        holder.nicknameTextView.text = server.nickname
        holder.ip.text = server.ip
        val indicatorDrawable = if (server.isUp) R.drawable.circle_green else R.drawable.circle_red
        holder.statusIndicator.setBackgroundResource(indicatorDrawable)

        holder.editButton.setOnClickListener { onEditClick(server) }
        holder.deleteButton.setOnClickListener { onDeleteClick(server) }
    }

    override fun getItemCount() = servers.size

    inner class ServerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusIndicator: ImageView = itemView.findViewById(R.id.server_status_indicator)
        val nicknameTextView: TextView = itemView.findViewById(R.id.server_nickname)
        val ip: TextView = itemView.findViewById(R.id.server_ip)
        val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }
}

