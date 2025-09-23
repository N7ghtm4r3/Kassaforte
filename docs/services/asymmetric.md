# Asymmetric Service

## Supported algorithms

The supported asymmetric algorithms to generate and then use the keys are the following:

- `RSA` asymmetric encryption algorithm based on the hardness of factoring large integers. Are supported two padding
  modes:
    - `PKCS#1` RSA signature scheme with `PKCS#1 v1.5 padding` (on the web just signing and verifying only)
    - `RSA_OAEP` RSA encryption using OAEP padding
- `EC` symmetric algorithm based on elliptic curve cryptography (ECC),
  commonly used for digital signatures (ECDSA) and key exchange (ECDH, unsupported at the moment)

## Architecture

### Android

The keys are generated with
the [KeyPairGenerator](https://developer.android.com/reference/java/security/KeyPairGenerator) API
and securely stored inside the [Keystore](https://developer.android.com/privacy-and-security/keystore) provided by
Android.
The keys are used by the [Cipher](https://developer.android.com/reference/javax/crypto/Cipher) to encrypt or decrypt the
data

### Apple

The keys are generated with
the [SecKeyCreateRandomKey](https://developer.apple.com/documentation/security/seckeycreaterandomkey(_:_:))
API and securely stored inside the [Keychain](https://developer.apple.com/documentation/security/keychain-services)
provided
by iOS and macOS operating systems.
The keys are used by security methods provided by Apple to encrypt or decrypt the data

### JVM

Like on `Android` the keys are generated with
the [KeyPairGenerator](https://docs.oracle.com/javase/8/docs/api/java/security/KeyPairGenerator.html) API, but
the keys are securely stored using the [java-keyring](https://github.com/javakeyring/java-keyring) library.
The keys are used by the [Cipher](https://docs.oracle.com/javase/8/docs/api/javax/crypto/Cipher.html) to encrypt or
decrypt the data

### Web

The keys are generated with the [SubtleCrypto](https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto) API and
securely stored into application's [IndexedDB](https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB_API).
The keys are used by the `SubtleCrypto` to encrypt or decrypt the data