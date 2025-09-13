package com.tecknobit.kassaforte.key.genspec;

enum class BlockModeType(
    val value: String
) {
    
    CBC("CBC"),
    
    CTR("CTR"),
    
    GCM("GCM");
}