# Kassaforte

![Maven Central](https://img.shields.io/maven-central/v/io.github.n7ghtm4r3/Kassaforte.svg?label=Maven%20Central)

![Static Badge](https://img.shields.io/badge/android-4280511051)
![Static Badge](https://img.shields.io/badge/ios-445E91)
![Static Badge](https://img.shields.io/badge/desktop-006874)
![Static Badge](https://img.shields.io/badge/wasmjs-834C74)
![Static Badge](https://img.shields.io/badge/backend-7d7d7d?link=https%3A%2F%2Fimg.shields.io%2Fbadge%2Fandroid-4280511051)

**v1.0.0beta-01**

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
- On `iOs` and native `macOs` the data are stored in
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

#### Asymmetric

## Integration

### Implementation

#### Version catalog

```toml
[versions]
kassaforte = "1.0.0beta-01"

[libraries]
kassaforte = { module = "io.github.n7ghtm4r3:Kassaforte", version.ref = "kassaforte" } 
```

#### Gradle

- Add the dependency

    ```groovy
    dependencies {
        implementation 'io.github.n7ghtm4r3:Kassaforte:1.0.0beta-01'
    }
    ```

  #### Gradle (Kotlin)

    ```kotlin
    dependencies {
        implementation("io.github.n7ghtm4r3:Kassaforte:1.0.0beta-01")
    }
    ```

  #### Gradle (version catalog)

    ```kotlin
    dependencies {
        implementation(libs.kassaforte)
    }
    ```

## Usage

## Documentation

Check out the library documentation [here!](https://n7ghtm4r3.github.io/Kassaforte/)

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