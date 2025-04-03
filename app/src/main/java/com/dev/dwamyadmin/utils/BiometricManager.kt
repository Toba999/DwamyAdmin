package com.dev.dwamyadmin.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import java.security.InvalidKeyException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object BiometricManager {

    private const val KEY_NAME = "biometricKey"
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"

    fun generateSecretKey() {
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }

            if (keyStore.containsAlias(KEY_NAME)) return // If key exists, do nothing

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(true) // Key gets invalidated if biometrics change
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCipher(): Cipher? {
        return try {
            Cipher.getInstance("AES/CBC/PKCS7Padding")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getSecretKey(): SecretKey? {
        return try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            keyStore.getKey(KEY_NAME, null) as? SecretKey
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isBiometricKeyInvalidated(): Boolean {
        return try {
            val secretKey = getSecretKey()
            if (secretKey == null) {
                generateSecretKey()
                return false
            }

            val cipher = getCipher()
            cipher?.init(Cipher.ENCRYPT_MODE, secretKey)

            false // No exception means the key is still valid

        } catch (e: KeyPermanentlyInvalidatedException) {
            e.printStackTrace()
            println("Key has been invalidated. Biometric enrollment might have changed.")
            true

        } catch (e: InvalidKeyException) {
            e.printStackTrace()
            true

        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }
}