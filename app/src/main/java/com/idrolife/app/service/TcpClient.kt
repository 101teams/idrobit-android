package com.idrolife.app.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class TcpClient @Inject constructor() {
    private val serverIp: String = "192.168.1.1"
    private val serverPort: Int = 1884
    private val timeoutMillis: Int = 10_000

    sealed class Result {
        data class Success(val response: String) : Result()
        data class Error(val exception: Exception) : Result()
    }

    suspend fun sendAndReceive(message: String): Result = withContext(Dispatchers.IO) {
        val socket = Socket()
        return@withContext try {
            socket.connect(InetSocketAddress(serverIp, serverPort), timeoutMillis)
            socket.soTimeout = timeoutMillis

            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            writer.print(message)
            writer.flush()

            val buffer = CharArray(1024)
            val charsRead = reader.read(buffer)
            val response = if (charsRead != -1) String(buffer, 0, charsRead) else ""

            Result.Success(response)

        } catch (e: Exception) {
            Result.Error(e)
        } finally {
            socket.close()
        }
    }
}
