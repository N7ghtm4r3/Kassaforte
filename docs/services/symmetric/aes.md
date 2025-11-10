## Generate new key

### Creating Gen Spec

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

### Declaring the purposes of the key

The generating key will be used just for the specified purposes, when a purpose had not been assigned will
be thrown an exception

```kotlin
val purposes = KeyPurposes(
    canEncrypt = true,
    canDecrypt = true
)
```

### Generate the key

```kotlin
KassaforteSymmetricService.generateKey(
    alias = "toIdentifyTheKey",
    algorithm = Algorithm.AES,
    keyGenSpec = keyGenSpec,
    purposes = purposes
)
```

The generated key will be automatically securely stored by the library

## Use the key

The methods which use the key must be executed inside a `Coroutine`

### Encrypt

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

### Decrypt

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

## Delete a key

```kotlin
KassaforteSymmetricService.deleteKey(
    alias = "toIdentifyTheKey"
)
```

The key will be removed from the secure storage and will not more available to be used