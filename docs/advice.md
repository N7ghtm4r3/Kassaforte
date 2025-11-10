Following, are indicated some advice, specific for each platform, to correct and safely use **Kassaforte**

## Android

### Disabling allowBackup

It is highly recommended to disable the `allowBackup` property in the `AndroidManifest.xml` file to prevent sensitive
data,
such as the generated keys or safeguarded data, from being backed up or transferred to new devices. This can be done as
follows:

```xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android" xmlns:tools="http://schemas.android.com/tools">

    <application
            android:allowBackup="false">
        ...
    </application>

</manifest>
```

## JVM

### Services adapters

**Kassaforte** provides adapters for `symmetric` and `asymmetric` services, allowing them to be used directly in a JVM
environment,
including `Android`, without boilerplate or tricky suspend handling, below some usage examples from a `Java` class

#### symmetric

Adapter name: `KassaforteSymmetricServiceJvm`

```java
public class KassaforteServiceWrapper {

    public void storeUserEmail(String email) {
        KassaforteSymmetricServiceJvm.encrypt(
                "keyAlias",
                BlockMode.CBC,
                EncryptionPadding.PKCS7,
                email
        );
    }

}
```

#### asymmetric

Adapter name: `KassaforteAsymmetricServiceJvm`

```java
public class KassaforteServiceWrapper {

    public void storeUserEmail(String email) {
        KassaforteAsymmetricServiceJvm.encrypt(
                "keyAlias",
                EncryptionPadding.RSA_OAEP,
                Digest.SHA256,
                email
        );
    }

}
```

!!! note

    The adapters support all the operations provided by the services, as they are mapped `1:1` to the service they wrap