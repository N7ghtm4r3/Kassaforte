# Symmetric Service

## Supported algorithms

### AES

Algorithm used to `encrypt` and `decrypt` data the following block modes:

- `CBC` - _**Cipher Block Chaining**_  where each block of plaintext is XORed with the previous ciphertext block before
  being encrypted. Requires an initialization vector (IV) of the block size
- `CTR` - _**Counter mode**_ turns a block cipher into a stream cipher by encrypting successive values of a counter and
  XORing them with the plaintext. Provides parallelizable encryption and decryption
- `GCM` - _**Galois/Counter Mode**_ it is based on `CTR` mode for encryption, but also provides authentication (AEAD)
  using Galois field multiplication.
  Requires a nonce, typically 12 bytes for efficiency (on Apple at the moment is not supported)

### HMAC

Algorithm used to `sign` and `verify` messages with the following digests:

- `SHA1`
- `SHA256`
- `SHA384`
- `SHA512`

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