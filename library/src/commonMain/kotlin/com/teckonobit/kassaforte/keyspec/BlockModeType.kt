package com.teckonobit.kassaforte.keyspec;

enum class BlockModeType(
    val value: String
) {
    
    ECB("ECB"),
    
    CBC("CBC"),
    
    CTR("CTR"),
    
    GCM("GCM");
}