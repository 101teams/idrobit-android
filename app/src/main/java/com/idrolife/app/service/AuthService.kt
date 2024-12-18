package com.idrolife.app.service

import com.idrolife.app.data.api.auth.AuthData
import com.idrolife.app.data.api.auth.AuthRequest
import com.idrolife.app.data.api.forgot_password.ForgotPasswordRequest
import com.idrolife.app.data.api.forgot_password.ForgotPasswordResponse
import com.idrolife.app.data.api.register.RegisterData
import com.idrolife.app.data.api.register.RegisterRequest

interface AuthService {
    fun resetToken()
    suspend fun login(request: AuthRequest): Pair<AuthData?, String>
    suspend fun register(request: RegisterRequest): Pair<RegisterData?, String>
    suspend fun forgotPassword(request: ForgotPasswordRequest, language: String): Pair<ForgotPasswordResponse?, String>
}
