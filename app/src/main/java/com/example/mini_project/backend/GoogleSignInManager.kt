package com.example.mini_project.backend

import android.content.Context
import com.example.mini_project.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object GoogleSignInManager {

    fun getClient(context: Context) =
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
            )
                .requestIdToken(
                    context.getString(
                        R.string.default_web_client_id
                    )
                )
                .requestEmail()
                .build()
        )
}