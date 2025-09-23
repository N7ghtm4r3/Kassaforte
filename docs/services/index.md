The services provide generation, maintenance and usage of symmetric and asymmetric keys to ensure data protection.

The following properties allow to correct generate and to use the keys

## KeySize

Enum containing the available sizes to generate a key

|  Name   |  Length   |       Algorithm        |
|:-------:|:---------:|:----------------------:|
| `S128`  | 128 bits  | Widely used with `AES` |
| `S192`  | 192 bits  | Widely used with `AES` |
| `S224`  | 224 bits  | Widely used with `EC`  |
| `S256`  | 256 bits  | Widely used with `AES` |
| `S384`  | 384 bits  | Widely used with `EC`  |
| `S512`  | 512 bits  | Widely used with `AES` |
| `S521`  | 521 bits  | Widely used with `EC`  |
| `S1024` | 1024 bits | Widely used with `RSA` |
| `S2048` | 2048 bits | Widely used with `RSA` |
| `S4096` | 4096 bits | Widely used with `RSA` |

## EncryptionPadding

Enum containing the available paddings to apply with the keys to protect the data

|    Name     | Padding        | Workflow                                                                                                                             |  Algorithm  |
|:-----------:|:---------------|:-------------------------------------------------------------------------------------------------------------------------------------|:-----------:|
|   `NONE`    | `NoPadding`    | No padding is applied                                                                                                                | `AES`, `EC` |
|   `PKCS7`   | `PKCS7Padding` | Padding scheme used with block ciphers, it fills the last block with bytes all set to the value of the number of padding bytes       |    `AES`    |
| `RSA_PKCS1` | `PKCS1Padding` | Padding scheme defined in `PKCS#1`, is less secure compared to `RSA_OAEP`                                                            |    `RSA`    |
| `RSA_OAEP`  | `OAEPPadding`  | **Optimal Asymmetric Encryption Padding**, provides better security than `RSA_PKCS1` by incorporating randomness and a hash function |    `RSA`    |

## Digest

Enum containing the available digest to use with the keys to protect the data

|   Name   | Algorithm | Purposes                                                                                                |
|:--------:|:---------:|:--------------------------------------------------------------------------------------------------------|
|  `NONE`  |  `NONE`   | No digest is applied, data is left un-hashed                                                            |
|  `MD5`   |   `MD5`   | Produces a `128-bit` hash, historically used for checksums and signatures, but insecure                 |
|  `SHA1`  |  `SHA-1`  | Produces a `160-bit` hash, used for data integrity and digital signatures, but vulnerable to collisions |
| `SHA224` | `SHA-224` | Produces a `224-bit` hash, used for secure hashing where shorter output is sufficient                   |
| `SHA256` | `SHA-256` | Produces a `256-bit` hash, widely used for integrity checks and digital signatures                      |
| `SHA384` | `SHA-384` | Produces a `384-bit` hash, used for high-security integrity verification                                |
| `SHA512` | `SHA-512` | Produces a `512-bit` hash, used for maximum security in hashing and digital signatures                  |

!!! Warning

    The usage of `SHA1` and `MD5` is discouraged due to known collision vulnerabilities and they are considered obsolete. 
    However, for compatibility, they are retained in this version but may be removed in the stable release