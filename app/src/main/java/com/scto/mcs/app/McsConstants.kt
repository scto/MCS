package com.scto.mcs.app

object McsConstants {
    private const val BASE_URL = "https://raw.githubusercontent.com/Xed-Editor/Karbon-PackagesX/main"

    const val PROOT_ARM = "$BASE_URL/arm/proot"
    const val PROOT_ARM64 = "$BASE_URL/aarch64/proot"
    const val PROOT_X64 = "$BASE_URL/x86_64/proot"

    const val TALLOC_ARM = "$BASE_URL/arm/libtalloc.so.2"
    const val TALLOC_ARM64 = "$BASE_URL/aarch64/libtalloc.so.2"
    const val TALLOC_X64 = "$BASE_URL/x86_64/libtalloc.so.2"

    private const val SANDBOX_BASE = "https://github.com/Xed-Editor/Karbon-PackagesX/releases/download/ubuntu"

    const val SANDBOX_ARM = "$SANDBOX_BASE/ubuntu-base-24.04.3-base-armhf.tar.gz"
    const val SANDBOX_AARCH64 = "$SANDBOX_BASE/ubuntu-base-24.04.3-base-arm64.tar.gz"
    const val SANDBOX_X64 = "$SANDBOX_BASE/ubuntu-base-24.04.3-base-amd64.tar.gz"
}