# Asymmetric Service

## Supported algorithms

The supported asymmetric algorithms to generate and then use the keys are the following:

- `RSA` asymmetric encryption algorithm based on the hardness of factoring large integers. Are supported two padding
  modes:
    - `PKCS#1` RSA signature scheme with `PKCS#1 v1.5 padding` (on the web just signing and verifying only)
    - `RSA_OAEP` RSA encryption using OAEP padding
- `EC` asymmetric algorithm based on elliptic curve cryptography (ECC),
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

## Usage

### Generate new key

#### Creating Gen Spec

Following the below compatibility table you can create a new asymmetric key:

| Algorithm |                    Key size                    |   Encryption padding    |                     Digest                     |
|:---------:|:----------------------------------------------:|:-----------------------:|:----------------------------------------------:|
|   `RSA`   |           `S1024`, `S2048`, `S4096`            | `RSA_PKCS1`, `RSA_OAEP` | `SHA1`, `SHA224`, `SHA256`, `SHA384`, `SHA512` |
|   `EC`    | `S128`, `S192`, `S224`, `S256`, `S384`, `S521` |         `NONE`          | `SHA1`, `SHA224`, `SHA256`, `SHA384`, `SHA512` |

!!! Warning

    The use of `SHA1` is discouraged due to known collision vulnerabilities and is considered obsolete. 
    However, on some **Android** devices, it may still be required due to hardware compatibility or limitations imposed by 
    the platform. If the device does not support, for example, `SHA256`, `SHA1` will be automatically used instead

```kotlin
val keyGenSpec = AsymmetricKeyGenSpec(
    keySize = KeySize.S4096,
    encryptionPadding = EncryptionPadding.RSA_OAEP, // not to sign or verify
    digest = Digest.SHA256
)
```

#### Declaring the purposes of the key

Following the below compatibility table you can assign the purposes to the generating key:

| Algorithm | Encryption padding |                      Purposes                      |
|:---------:|:------------------:|:--------------------------------------------------:|
|   `RSA`   |     `RSA_OAEP`     |             `canEncrypt`, `canDecrypt`             |
|   `RSA`   |    `RSA_PKCS1`     | `canEncrypt`, `canDecrypt`, `canSign`, `canVerify` |
|   `EC`    |       `NONE`       |               `canSign`, `canVerify`               |

!!! Warning

    If your project targets the `Web`, keep in mind that `RSA_PKCS1` can only be used for `signing` and `verifying`, 
    without the `canEncrypt` and `canDecrypt` purposes. For `encryption` and `decryption`, use `RSA_OAEP` instead

The generating key will be used just for the specified purposes, when a purpose had not been assigned will
be thrown an exception

```kotlin
val purposes = KeyPurposes(
    canEncrypt = true,
    canDecrypt = true,
    -- and / or --
    canSign = true,
    canVerify = true
)
```

#### Generate the key

```kotlin
KassaforteAsymmetricService.generateKey(
    alias = "toIdentifyTheKey",
    algorithm = Algorithm.RSA,
    keyGenSpec = keyGenSpec,
    purposes = purposes
)
```

The generated key or key pair will be automatically securely stored by the library

### Use the key

The methods which use the key must be executed inside a `Coroutine`

#### Encrypt

```kotlin
val scope = MainScope()
scope.launch {
    // data to encrypt
    val dataToEncrypt = "PLAINTEXT"

    // encrypt the data 
    val encryptedData = KassaforteAsymmetricService.encrypt(
        alias = "toIdentifyTheKey",
        padding = EncryptionPadding.RSA_OAEP,
        digest = Digest.SHA256,
        data = dataToDecrypt
    )

    println(encryptedData)
}
```

!!! Info

    The `padding` and the `digest` values must match the values used to generate the using key, otherwise the encryption
    will fail

#### Decrypt

```kotlin
val scope = MainScope()
scope.launch {
    // data to decrypt
    val dataToDecrypt = "...some encrypted data..."

    // decrypt the data 
    val decryptedData = KassaforteAsymmetricService.decrypt(
        alias = "toIdentifyTheKey",
        padding = EncryptionPadding.RSA_OAEP,
        digest = Digest.SHA256,
        data = dataToEncrypt
    )

    println(decryptedData) // PLAINTEXT
}
```

!!! Info

    The `padding` and the `digest` values must match the values used to generate the using key, otherwise the decryption
    will fail

#### Sign

```kotlin
val scope = MainScope()
scope.launch {
    // data to sign
    val message = "My message"

    // sign the message 
    val signedMessage = KassaforteAsymmetricService.sign(
        alias = "toIdentifyTheKey",
        digest = Digest.SHA256,
        message = message
    )

    println(signedMessage)
}
```

#### Verify

```kotlin
val scope = MainScope()
scope.launch {
    // data to verify
    val messageToVerify = "My message"

    // signature
    val signedMessage = "...signed message.."

    // verify the data 
    val result = KassaforteSymmetricService.verify(
        alias = "toIdentifyTheKey",
        digest = Digest.SHA256,
        message = messageToVerify,
        signature = signedMessage
    )

    println(result) // true or false
}
```

### Delete a key

```kotlin
KassaforteAsymmetricService.deleteKey(
    alias = "toIdentifyTheKey"
)
```

The key or the key pair will be removed from the secure storage and will not more available to be used