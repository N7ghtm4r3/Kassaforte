# Key Derivation

Using the `PBKDF2` algorithm, it is possible to derive a cryptographic key from an arbitrary password:

!!! Warning

    The selected `password` is not automatically persisted by **Kassaforte**, but it can be manually stored 
    using the [safeguard](../../secure_storage.md#safeguard) method

```kotlin
val scope = MainScope()
scope.launch {
    // arbitrary password
    val password: CharArray = "helloworld!".toCharArray()

    // arbitrary salt 
    val randomSalt = Random.nextBytes(512)

    val derivedKey: KassaforteDerivedKey = KassaforteSymmetricService.deriveKey(
        password = password,
        salt = randomSalt,
        keySize = KeySize.S512,
        digest = Digest.SHA512,
    )

    // e.g. Ynl0ZUFaycmF5T2YoOCwgMjEsIDE4LCAyMiwgMjcsIDE2KQ==
    println(derivedKey.key)
}
```

!!! Note

    The `salt`, `keySize`, and `digest` parameters are not sensitive and do not need to be kept secret

The derived key returned by this method is represented as a `KassaforteDerivedKey` object, which contains both the
derived `Base64` encoded key and the parameters used during the derivation process.