package com.idrolife.app.service

import com.idrolife.app.data.api.auth.AuthData
import com.idrolife.app.data.api.auth.AuthRequest

interface AuthService {
    fun resetToken()
    suspend fun login(request: AuthRequest): Pair<AuthData?, String>
}
