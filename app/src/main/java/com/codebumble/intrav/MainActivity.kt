package com.codebumble.intrav

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ServerAdapter
    private val servers = mutableListOf<Server>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        servers.addAll(ServerStorage.loadServers(this))

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        adapter = ServerAdapter(
            servers,
            onEditClick = { server -> showEditServerDialog(server) },
            onDeleteClick = { server -> deleteServer(server) }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            showAddServerDialog()
        }

        checkServersStatus()

        // Schedule periodic status checks
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                checkServersStatus()
                handler.postDelayed(this, 6000) // 6 seconds delay
            }
        }
        handler.post(runnable)
    }

    private fun showEditServerDialog(server: Server) {
        val view = layoutInflater.inflate(R.layout.dialog_add_server, null)
        val nicknameInput = view.findViewById<EditText>(R.id.nickname)
        val ipInput = view.findViewById<EditText>(R.id.ip)

        nicknameInput.setText(server.nickname)
        ipInput.setText(server.ip)

        AlertDialog.Builder(this)
            .setTitle("Edit Server")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                server.nickname = nicknameInput.text.toString()
                server.ip = ipInput.text.toString()
                server.isUp = true
                ServerStorage.saveServers(this, servers)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteServer(server: Server) {
        AlertDialog.Builder(this)
            .setTitle("Delete Server")
            .setMessage("Are you sure you want to delete this server?")
            .setPositiveButton("Delete") { _, _ ->
                servers.remove(server)
                ServerStorage.saveServers(this, servers)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddServerDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_server, null)
        val nicknameInput = view.findViewById<EditText>(R.id.nickname)
        val ipInput = view.findViewById<EditText>(R.id.ip)

        AlertDialog.Builder(this)
            .setTitle("Add Server")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val nickname = nicknameInput.text.toString()
                val ip = ipInput.text.toString()
                val server = Server(nickname, ip, false)
                servers.add(server)
                ServerStorage.saveServers(this, servers)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkServersStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            servers.forEach { server ->
                server.isUp = isServerUp(server.ip)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private suspend fun isServerUp(ip: String, port: Int = 80, timeout: Int = 5000): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(ip, port), timeout)
                    true
                }
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }
}
