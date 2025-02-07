package com.dev.dwamyadmin.data.repo

import com.dev.dwamyadmin.domain.repo.FireBaseRepo
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class FireBaseRepoImpl @Inject constructor(
    private val database: FirebaseDatabase
) : FireBaseRepo {
    
}