package com.x8bit.bitwarden.authenticator.data.authenticator.datasource.disk.entity

/**
 * Enum class representing SHA algorithms an authenticator item may be hashed with.
 */
enum class AuthenticatorItemAlgorithm {

    /**
     * Authenticator item verification code uses SHA1 hash.
     */
    SHA1,

    /**
     * Authenticator item verification code uses SHA256 hash.
     */
    SHA256,

    /**
     * Authenticator item verification code uses SHA512 hash.
     */
    SHA512
}
