package com.idrolife.app.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import javax.inject.Inject

class TcpClient @Inject constructor() {
    private var serverMessage: String = ""
    private var socket: Socket? = null
    private var isRunning = false
    private var onResponseReceived: ((String) -> Unit)? = null

    companion object {
        const val SERVER_IP = "192.168.4.1"
        const val SERVER_PORT = 23
        const val TCP_EXCEPTION_ERROR = "TCP_EXCEPTION_ERROR"
    }

    fun initialize(callback: (String) -> Unit) {
        onResponseReceived = callback
    }

    fun run() {
        isRunning = true

        try {
            socket = Socket(SERVER_IP, SERVER_PORT)

            val reader = BufferedReader(InputStreamReader(socket?.getInputStream()))

            // Launch a coroutine to read from the socket
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    while (isRunning) {
                        serverMessage = reader.readLine() ?: ""
                        if (serverMessage.isNotEmpty()) {
                            withContext(Dispatchers.Main) {
                                onResponseReceived?.invoke(serverMessage)
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onResponseReceived?.invoke(TCP_EXCEPTION_ERROR)
                    }
                }
            }

        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                onResponseReceived?.invoke(TCP_EXCEPTION_ERROR)
            }
        }
    }

    fun sendMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val writer = PrintWriter(BufferedWriter(OutputStreamWriter(socket?.getOutputStream())), true)
                writer.println(message)
                writer.flush()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResponseReceived?.invoke(TCP_EXCEPTION_ERROR)
                }
            }
        }
    }

    fun stopClient() {
        isRunning = false
        socket?.close()
    }
}