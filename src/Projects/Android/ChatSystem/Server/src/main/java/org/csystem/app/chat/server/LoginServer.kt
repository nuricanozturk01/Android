package org.csystem.app.chat.server

import com.karandev.util.net.TcpUtil
import com.karandev.util.net.exception.NetworkException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.csystem.app.chat.server.configuration.constant.ERR_LOGIN
import org.csystem.app.chat.server.configuration.constant.SUC_LOGIN
import org.csystem.app.chatsystem.service.ChatSystemService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService

@Component
class LoginServer(private val threadPool: ExecutorService,
                  @Qualifier("app.chat.server.config.login.port.ServerSocket")
                     private val serverSocket: ServerSocket,
                  private val chatSystemService: ChatSystemService) {
    @Value("\${app.chat.server.config.login.timeout}")
    private var mTimeout: Int = 0;

    private fun clientOperationCallback(socket: Socket) {
        try {
            socket.soTimeout = mTimeout
            val nickname = TcpUtil.receiveLine(socket)
            val password = TcpUtil.receiveLine(socket)
            val status = chatSystemService.saveLoginInfoByNickNameAndPassword(nickname, password)
            val statusMessage = if (status) SUC_LOGIN else ERR_LOGIN

            TcpUtil.sendLine(socket, statusMessage)
            //...
        }
        catch (ex: NetworkException) {
            println("Network Error:${ex.message}")
        }
    }

    private fun handleClient(socket: Socket)
    {
        try {
            socket.use { clientOperationCallback(it) }
        }
        catch (ex: Throwable) {
            println("Error:${ex.message}")
        }
    }

    private fun serverCallback()
    {
        try {
            println("Login server waiting for a client")

            while (true) {
                val socket = serverSocket.accept()

                MainScope().launch { handleClient(socket) }
            }
        }
        catch (ex: Throwable) {
            println("Exception occurred:${ex.message}")
        }
    }

    fun run()
    {
        threadPool.execute(this::serverCallback)
    }
}