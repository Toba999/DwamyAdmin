package com.dev.dwamyadmin.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object BiometricManager {

    fun generateKeyForBiometricAuthentication(): Boolean {
        val keyName = "biometricKey"

        // Prepare the KeyGenParameterSpec to invalidate the key when biometric enrollment changes
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setInvalidatedByBiometricEnrollment(true) // Invalidate the key if biometric enrollment changes
            .build()

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(keyGenParameterSpec)

        // Generate the key
        val secretKey: SecretKey = keyGenerator.generateKey()

        // Now let's attempt to use the key (e.g., for encryption) and check if it's invalidated
        try {
            // Attempt to use the key for encryption (or any other cryptographic operation)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            // Use the cipher (e.g., to encrypt data)

            // If successful, the key is valid
            println("Key is valid and can be used.")
            return true

        } catch (e: KeyPermanentlyInvalidatedException) {
            // This exception will be thrown if the key has been invalidated (e.g., due to biometric changes)
            println("Key has been invalidated. Biometric enrollment might have changed.")
            return true

        } catch (e: Exception) {
            // Handle other exceptions
            println("Error occurred while using the key: ${e.message}")
            return true

        }
    }
}