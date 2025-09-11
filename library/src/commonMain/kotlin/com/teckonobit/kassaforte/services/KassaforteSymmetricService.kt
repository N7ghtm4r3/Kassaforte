package com.teckonobit.kassaforte.services

import com.teckonobit.kassaforte.keys.SymmetricKey

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class KassaforteSymmetricService(
    alias: String
): KassaforteKeysService<SymmetricKey>