# Symmetric Service

## Supported algorithms

The supported algorithm to generate and then use the keys is the **AES** algorithm with the following block modes:

- `CBC` - _**Cipher Block Chaining**_  where each block of plaintext is XORed with the previous ciphertext block before
  being encrypted. Requires an initialization vector (IV) of the block size
- `CTR` - _**Counter mode**_ turns a block cipher into a stream cipher by encrypting successive values of a counter and
  XORing them with the plaintext. Provides parallelizable encryption and decryption
- `GCM` - _**Galois/Counter Mode**_ it is based on `CTR` mode for encryption, but also provides authentication (AEAD)
  using Galois field multiplication.
  Requires a nonce, typically 12 bytes for efficiency (on Apple at the moment is not supported)

## Architecture

### Android

The keys are generated with the [KeyGenerator](https://developer.android.com/reference/kotlin/javax/crypto/KeyGenerator)
API
and securely stored inside the [Keystore](https://developer.android.com/privacy-and-security/keystore) provided by
Android.
The keys are used by the [Cipher](https://developer.android.com/reference/javax/crypto/Cipher) to encrypt or decrypt the
data

### Apple

The keys are generated with
the [SecRandomCopyBytes](https://developer.apple.com/documentation/security/secrandomcopybytes(_:_:_:))
API and securely stored inside the [Keychain](https://developer.apple.com/documentation/security/keychain-services)
provided
by iOS and macOS operating systems.
The keys are used by [korlibs-crypto](https://github.com/korlibs/korlibs-crypto) library to encrypt or decrypt the data

### JVM

Like on `Android` the keys are generated with
the [KeyGenerator](https://docs.oracle.com/javase/8/docs/api/javax/crypto/KeyGenerator.html) API, but
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

Following the below compatibility table you can create a new `AES` key:

| Block mode |        Key size        | Encryption padding |
|:----------:|:----------------------:|:------------------:|
|   `CBC`    | `S128`, `S192`, `S256` |      `PKCS7`       |
|   `CTR`    | `S128`, `S192`, `S256` |       `NONE`       |
|   `GCM`    | `S128`, `S192`, `S256` |       `NONE`       |

```kotlin
val keyGenSpec = SymmetricKeyGenSpec(
    blockMode = BlockMode.GCM,
    keySize = S128,
    encryptionPadding = EncryptionPadding.NONE
)
```

#### Declaring the purposes of the key

The generating key will be used just for the specified purposes, when a purpose had not been assigned will
be thrown an exception

```kotlin
val purposes = KeyPurposes(
    canEncrypt = true,
    canDecrypt = true
)
```

#### Generate the key

```kotlin
KassaforteSymmetricService.generateKey(
    algorithm = Algorithm.AES,
    alias = "toIdentifyTheKey",
    keyGenSpec = keyGenSpec,
    purposes = purposes
)
```

The generated key will be automatically securely stored by the library

### Use the key

The methods which use the key must be executed inside a `Coroutine`

#### Encrypt

```kotlin
val scope = MainScope()
scope.launch {
    // data to encrypt
    val dataToEncrypt = "PLAINTEXT"

    // encrypt the data 
    val encryptedData = KassaforteSymmetricService.encrypt(
        alias = "toIdentifyTheKey",
        blockMode = BlockMode.GCM,
        padding = EncryptionPadding.NONE,
        data = encryptedData
    )

    println(encryptedData)
}
```

!!! Info

    The `blockMode` and the `padding` values must match the values used to generate the using key, otherwise the encryption
    will fail

#### Decrypt

```kotlin
val scope = MainScope()
scope.launch {
    // data to decrypt
    val dataToDecrypt = "...some encrypted data..."

    // decrypt the data 
    val decryptedData = KassaforteSymmetricService.decrypt(
        alias = "toIdentifyTheKey",
        blockMode = BlockMode.GCM,
        padding = EncryptionPadding.NONE,
        data = dataToDecrypt
    )

    println(decryptedData) // PLAINTEXT
}
```

!!! Info

    The `blockMode` and the `padding` values must match the values used to generate the using key, otherwise the decryption
    will fail

### Delete a key

```kotlin
KassaforteSymmetricService.deleteKey(
    alias = "toIdentifyTheKey"
)
```

The key will be removed from the secure storage and will not more available to be used