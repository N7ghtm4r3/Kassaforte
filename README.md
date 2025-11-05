# Kassaforte

![Maven Central](https://img.shields.io/maven-central/v/io.github.n7ghtm4r3/kassaforte.svg?label=Maven%20Central)

![Static Badge](https://img.shields.io/badge/android-4280511051)
![Static Badge](https://img.shields.io/badge/ios-445E91)
![Static Badge](https://img.shields.io/badge/desktop-006874)
![Static Badge](https://img.shields.io/badge/wasmjs-834C74)
![Static Badge](https://img.shields.io/badge/backend-7d7d7d?link=https%3A%2F%2Fimg.shields.io%2Fbadge%2Fandroid-4280511051)

**v1.0.0beta-02**

**Kassaforte** enables secure storage of sensitive data in Compose Multiplatform applications and on the backend by
leveraging
each platform’s native security APIs. It further supports the generation and usage of `symmetric` and `asymmetric` keys
to
ensure data protection

## Architecture

### Secure storage

- On `Android` the data are stored in
  the [SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences), encrypting the
  data before storing it
- On `iOS` and native `macOS` the data are stored in
  the [Keychain](https://developer.apple.com/documentation/security/keychain-services)
- On `JVM` to store the data are leveraged the native APIs provided by the different OSs:
    - On `Windows` the data are stored in
      the [Windows Credentials](https://learn.microsoft.com/en-us/windows/win32/secauthn/credentials-management)
    - On `Linux` the data are stored using the implementation of
      the [DBus Secret Service](https://specifications.freedesktop.org/secret-service-spec/0.2/description.html) based
      on the desktop environment between
      `GNOME` or `KDE`
    - On`MacOs` the data are stored in
      the [Keychain](https://developer.apple.com/documentation/security/keychain-services)

  This target uses the [java-keyring](https://github.com/javakeyring/java-keyring) library under the hood

- On `Web` the data are stored in
  the [LocalStorage](https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage), encrypting the data before
  storing it

### Services

#### Symmetric

The supported algorithm to generate and then use the keys is the **AES** algorithm with the following block modes:

- `CBC` - _**Cipher Block Chaining**_  where each block of plaintext is XORed with the previous ciphertext block before
  being encrypted. Requires an initialization vector (IV) of the block size
- `CTR` - _**Counter mode**_ turns a block cipher into a stream cipher by encrypting successive values of a counter and
  XORing them with the plaintext. Provides parallelizable encryption and decryption
- `GCM` - _**Galois/Counter Mode**_ it is based on `CTR` mode for encryption, but also provides authentication (AEAD)
  using Galois field multiplication.
  Requires a nonce, typically 12 bytes for efficiency (on Apple at the moment is not supported)

<h6>Android</h6>

The keys are generated with the [KeyGenerator](https://developer.android.com/reference/kotlin/javax/crypto/KeyGenerator)
API
and securely stored inside the [Keystore](https://developer.android.com/privacy-and-security/keystore) provided by
Android.
The keys are used by the [Cipher](https://developer.android.com/reference/javax/crypto/Cipher) to encrypt or decrypt the
data

<h6>Apple</h6>

The keys are generated with
the [SecRandomCopyBytes](https://developer.apple.com/documentation/security/secrandomcopybytes(_:_:_:))
API and securely stored inside the [Keychain](https://developer.apple.com/documentation/security/keychain-services)
provided
by iOS and macOS operating systems.
The keys are used by [korlibs-crypto](https://github.com/korlibs/korlibs-crypto) library to encrypt or decrypt the data

<h6>JVM</h6>

Like on `Android` the keys are generated with
the [KeyGenerator](https://docs.oracle.com/javase/8/docs/api/javax/crypto/KeyGenerator.html) API, but
the keys are securely stored using the [java-keyring](https://github.com/javakeyring/java-keyring) library.
The keys are used by the [Cipher](https://docs.oracle.com/javase/8/docs/api/javax/crypto/Cipher.html) to encrypt or
decrypt the data

<h6>Web</h6>

The keys are generated with the [SubtleCrypto](https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto) API and
securely stored into application's [IndexedDB](https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB_API).
The keys are used by the `SubtleCrypto` to encrypt or decrypt the data

#### Asymmetric

The supported asymmetric algorithms to generate and then use the keys are the following:

- `RSA` asymmetric encryption algorithm based on the hardness of factoring large integers. Are supported two padding
  modes:
    - `PKCS#1` RSA signature scheme with `PKCS#1 v1.5 padding` (on the web just signing and verifying only)
    - `RSA_OAEP` RSA encryption using OAEP padding
- `EC` symmetric algorithm based on elliptic curve cryptography (ECC),
  commonly used for digital signatures (ECDSA) and key exchange (ECDH, unsupported at the moment)

<h6>Android</h6>

The keys are generated with
the [KeyPairGenerator](https://developer.android.com/reference/java/security/KeyPairGenerator) API
and securely stored inside the [Keystore](https://developer.android.com/privacy-and-security/keystore) provided by
Android.
The keys are used by the [Cipher](https://developer.android.com/reference/javax/crypto/Cipher) to encrypt or decrypt the
data

<h6>Apple</h6>

The keys are generated with
the [SecKeyCreateRandomKey](https://developer.apple.com/documentation/security/seckeycreaterandomkey(_:_:))
API and securely stored inside the [Keychain](https://developer.apple.com/documentation/security/keychain-services)
provided
by iOS and macOS operating systems.
The keys are used by security methods provided by Apple to encrypt or decrypt the data

<h6>JVM</h6>

Like on `Android` the keys are generated with
the [KeyPairGenerator](https://docs.oracle.com/javase/8/docs/api/java/security/KeyPairGenerator.html) API, but
the keys are securely stored using the [java-keyring](https://github.com/javakeyring/java-keyring) library.
The keys are used by the [Cipher](https://docs.oracle.com/javase/8/docs/api/javax/crypto/Cipher.html) to encrypt or
decrypt the data

<h6>Web</h6>

The keys are generated with the [SubtleCrypto](https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto) API and
securely stored into application's [IndexedDB](https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB_API).
The keys are used by the `SubtleCrypto` to encrypt or decrypt the data

## Roadmap to 1.0.0

### beta-01

This release provides the `Kassaforte` API to securely store sensitive data.

Provides also the services, symmetric and asymmetric, to generate keys and perform the `encryption` and `decryption` of
the data

### beta-02

This release provides:

- `signing` and `verification` of the data
- `GCM` block mode support also on `iOS`

### beta-03

This release will provide:

- `wrap` and `unwrap` of the keys

Should be the latest `beta` version before the `stable` one

## Implementation

### Gradle short

```groovy
dependencies {
    implementation 'io.github.n7ghtm4r3:kassaforte:1.0.0beta-02'
}
```

### Gradle (Kotlin)

```kotlin
dependencies {
    implementation("io.github.n7ghtm4r3:kassaforte:1.0.0beta-02")
}
```

### Gradle (version catalog)

#### libs.versions.toml

```toml
[versions]
kassaforte = "1.0.0beta-02"

[libraries]
kassaforte = { module = "io.github.n7ghtm4r3:kassaforte", version.ref = "kassaforte" } 
```

#### build.gradle.kts

```kotlin
dependencies {
    implementation(libs.kassaforte)
}
```

## Usage

### Kassaforte

#### Sync methods

```kotlin
@Composable
fun App() {
    // create an instance of Kassaforte
    val kassaforte = Kassaforte(
        name = "YourApplicationName" // suggested name
    )

    // safeguard sensitive data
    kassaforte.safeguard(
        key = "keyToRepresentData",
        data = // sensitive data to safeguard
    )

    // refresh sensitive data previously safeguarded
    kassaforte.refresh(
        key = "keyToRepresentData",
        data = // sensitive refreshed data to safeguard
    )

    // remove sensitive data previously safeguarded
    kassaforte.remove(
        key = "keyToRepresentData"
    )
} 
```

#### Async methods

The following methods required to be executed inside a `Coroutine`

```kotlin
@Composable
fun App() {
    val scope = MainScope()
    scope.launch {
        // async withdraw a safeguarded data
        val safeguardedData: String = kassaforte.withdraw(
            key = "keyToRepresentData"
        )

        println(safeguardedData)
    }
}
```

### Services

Using the services you can generate and then use symmetric and asymmetric keys

#### Symmetric

<h6>Generate key</h6>

```kotlin
@Composable
fun App() {
    // specify the generation spec of the key
    val keyGenSpec = SymmetricKeyGenSpec(
        blockMode = BlockMode.GCM,
        keySize = S128,
        encryptionPadding = EncryptionPadding.NONE
    )

    // specify the purposes where the key can be used
    val purposes = KeyPurposes(
        canEncrypt = true,
        canDecrypt = true
    )

    // generate the key
    KassaforteSymmetricService.generateKey(
        algorithm = Algorithm.AES,
        alias = "toIdentifyTheKey",
        keyGenSpec = keyGenSpec,
        purposes = purposes
    )
} 
```

<h6>Encrypt with the key</h6>

```kotlin
@Composable
fun App() {
    val scope = MainScope()
    scope.launch {
        // data to encrypt
        val dataToEncrypt = "PLAINTEXT"

        // encrypt the data 
        val encryptedData = KassaforteSymmetricService.encrypt(
            alias = "toIdentifyTheKey",
            // must match with the value used to create the key
            blockMode = BlockMode.GCM,
            // must match with the value used to create the key
            padding = EncryptionPadding.NONE,
            data = encryptedData
        )

        println(encryptedData)
    }
} 
```

<h6>Decrypt with the key</h6>

```kotlin
@Composable
fun App() {
    val scope = MainScope()
    scope.launch {
        // data to decrypt
        val dataToDecrypt = "...some encrypted data..."

        // decrypt the data 
        val decryptedData = KassaforteSymmetricService.decrypt(
            alias = "toIdentifyTheKey",
            // must match with the value used to create the key
            blockMode = BlockMode.GCM,
            // must match with the value used to create the key
            padding = EncryptionPadding.NONE,
            data = dataToDecrypt
        )

        println(decryptedData) // PLAINTEXT
    }
} 
```

<h6>Delete a key</h6>

```kotlin
@Composable
fun App() {
    KassaforteSymmetricService.deleteKey(
        alias = "toIdentifyTheKey"
    )
} 
```

#### Asymmetric

<h6>Generate key</h6>

```kotlin
@Composable
fun App() {
    // specify the generation spec of the key
    val keyGenSpec = AsymmetricKeyGenSpec(
        keySize = KeySize.S4096,
        encryptionPadding = EncryptionPadding.RSA_OAEP, // or PKCS#1
        digest = Digest.SHA256
    )

    // specify the purposes where the key can be used
    val purposes = KeyPurposes(
        canEncrypt = true,
        canDecrypt = true
    )

    // generate the key
    KassaforteAsymmetricService.generateKey(
        algorithm = Algorithm.RSA, // or EC
        alias = "toIdentifyTheKey",
        keyGenSpec = keyGenSpec,
        purposes = purposes
    )
} 
```

<h6>Encrypt with the key</h6>

```kotlin
@Composable
fun App() {
    val scope = MainScope()
    scope.launch {
        // data to encrypt
        val dataToEncrypt = "PLAINTEXT"

        // encrypt the data 
        val encryptedData = KassaforteAsymmetricService.encrypt(
            alias = "toIdentifyTheKey",
            // must match with the value used to create the key
            padding = EncryptionPadding.RSA_OAEP,
            // must match with the value used to create the key
            digest = digest.SHA256,
            data = dataToDecrypt
        )

        println(encryptedData)
    }
} 
```

<h6>Decrypt with the key</h6>

```kotlin
@Composable
fun App() {
    val scope = MainScope()
    scope.launch {
        // data to decrypt
        val dataToDecrypt = "...some encrypted data..."

        // decrypt the data 
        val decryptedData = KassaforteAsymmetricService.decrypt(
            alias = "toIdentifyTheKey",
            // must match with the value used to create the key
            padding = EncryptionPadding.RSA_OAEP,
            // must match with the value used to create the key
            digest = digest.SHA256,
            data = dataToEncrypt
        )

        println(decryptedData) // PLAINTEXT
    }
} 
```

<h6>Delete a key</h6>

```kotlin
@Composable
fun App() {
    KassaforteAsymmetricService.deleteKey(
        alias = "toIdentifyTheKey"
    )
} 
```

## Documentation

Check out the library [documentation](https://n7ghtm4r3.github.io/Kassaforte/) for more information on how to generate
and
use keys, as well as how to correctly use the `Kassaforte` API.

## Credits

A big thanks to the repositories and their maintainers for developing the libraries that **Kassaforte** relies on to
work correctly:

- [java-keyring](https://github.com/javakeyring/java-keyring) - handles the secure storage on the `JVM` target
- [korlibs-crypto](https://github.com/korlibs/korlibs-crypto) - handles the symmetric encryption and decryption on the
  `Apple` target
- [windpapi4j](https://github.com/peter-gergely-horvath/windpapi4j) - handles (DPAPI) data protection on `Windows` to
  securely store the generated keys on the `JVM` target

## Support

If you need help using the library or encounter any problems or bugs, please contact us via the
following links:

- Support via <a href="mailto:infotecknobitcompany@gmail.com">email</a>
- Support via <a href="https://github.com/N7ghtm4r3/Kassaforte/issues/new">GitHub</a>

Thank you for your help!

## Donations

If you want support project and developer

| Crypto                                                                                              | Address                                          | Network  |
|-----------------------------------------------------------------------------------------------------|--------------------------------------------------|----------|
| ![](https://img.shields.io/badge/Bitcoin-000000?style=for-the-badge&logo=bitcoin&logoColor=white)   | **3H3jyCzcRmnxroHthuXh22GXXSmizin2yp**           | Bitcoin  |
| ![](https://img.shields.io/badge/Ethereum-3C3C3D?style=for-the-badge&logo=Ethereum&logoColor=white) | **0x1b45bc41efeb3ed655b078f95086f25fc83345c4**   | Ethereum |
| ![](https://img.shields.io/badge/Solana-000?style=for-the-badge&logo=Solana&logoColor=9945FF)       | **AtPjUnxYFHw3a6Si9HinQtyPTqsdbfdKX3dJ1xiDjbrL** | Solana   |

If you want support project and developer
with <a href="https://www.paypal.com/donate/?hosted_button_id=5QMN5UQH7LDT4">PayPal</a>

Copyright © 2025 Tecknobit
