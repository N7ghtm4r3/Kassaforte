The secure storage of sensitive data in Compose Multiplatform applications and on the backend by
leveraging each platform’s native security APIs is provided by Kassaforte

## Architecture

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

## Usage

#### Create an instance

```kotlin
val kassaforte = Kassaforte(
    name = "YourApplicationName" // suggested name
)
```

### Sync methods

#### safeguard

This method securely store sensitive data

```kotlin
kassaforte.safeguard(
    key = "keyToRepresentData",
    data = // sensitive data to safeguard
)
```

#### refresh

This method refresh sensitive data previously safeguarded

```kotlin
kassaforte.refresh(
    key = "keyToRepresentData",
    data = // sensitive refreshed data to safeguard
)
```

#### remove

This method remove from the secure storage sensitive data previously safeguarded

```kotlin
kassaforte.remove(
    key = "keyToRepresentData"
)
```

### Async methods

The following methods required to be executed inside a `Coroutine`

#### withdraw

This method withdraw from the secure storage sensitive data previously safeguarded decrypting it

```kotlin
val scope = MainScope()
scope.launch {
    val safeguardedData: String = kassaforte.withdraw(
        key = "keyToRepresentData"
    )

    println(safeguardedData)
}
```