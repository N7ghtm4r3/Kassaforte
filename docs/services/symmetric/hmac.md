## Generate new key

### Creating Gen Spec

Following the below compatibility table you can create a new `HMAC` key:

|  Digest  |    Key size     |
|:--------:|:---------------:|
|  `SHA1`  |     `S192`      |
| `SHA256` |     `S256`      |
| `SHA384` |     `S384`      |
| `SHA512` | `S512`, `S1024` |

!!! Note

    The key sizes listed are suggested for use with the indicated SHA values, but this depends on your implementation, 
    as there are no strict requirements for the key size in `HMAC`

```kotlin
val keyGenSpec = SymmetricKeyGenSpec(
    keySize = S256
)
```

### Declaring the purposes of the key

The generating key will be used just for the specified purposes, when a purpose had not been assigned will
be thrown an exception

```kotlin
val purposes = KeyPurposes(
    canSign = true,
    canVerify = true
)
```

### Generate the key

```kotlin
KassaforteSymmetricService.generateKey(
    alias = "toIdentifyTheKey",
    algorithm = Algorithm.HMAC_SHA256,
    keyGenSpec = keyGenSpec,
    purposes = purposes
)
```

The generated key will be automatically securely stored by the library

## Use the key

The methods which use the key must be executed inside a `Coroutine`

### Sign

```kotlin
val scope = MainScope()
scope.launch {
    // data to sign
    val message = "My message"

    // sign the message 
    val signedMessage = KassaforteSymmetricService.sign(
        alias = "toIdentifyTheKey",
        message = data
    )

    println(signedMessage)
}
```

### Verify

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
        message = messageToVerify,
        signature = signedMessage
    )

    println(result) // true or false
}
```

## Delete a key

```kotlin
KassaforteSymmetricService.deleteKey(
    alias = "toIdentifyTheKey"
)
```

The key will be removed from the secure storage and will not more available to be used