# Overview

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
to ensure data protection

## Roadmap to 1.0.0

### beta-01

This release provides the `Kassaforte` API to securely store sensitive data.

Provides also the services, symmetric and asymmetric, to generate keys and perform the `encryption` and `decryption` of
the data

### beta-02

This release will provide:

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
