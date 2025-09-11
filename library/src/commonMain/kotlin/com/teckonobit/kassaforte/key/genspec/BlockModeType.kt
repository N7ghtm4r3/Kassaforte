package com.teckonobit.kassaforte.key.genspec;

enum class BlockModeType(
    val value: String
) {
    
    ECB("ECB"),
    
    CBC("CBC"),
    
    CTR("CTR"),
    
    GCM("GCM");
}