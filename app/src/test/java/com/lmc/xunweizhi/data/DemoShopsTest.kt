package com.lmc.xunweizhi.data

import com.lmc.xunweizhi.data.local.toDomain
import com.lmc.xunweizhi.data.repository.DemoShops
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoShopsTest {
    @Test
    fun demoDataCoversEveryCategory() {
        val typeIds = DemoShops.shops.map { it.typeId }.toSet()

        assertEquals(9, DemoShops.shops.size)
        assertEquals(setOf(1L, 2L, 3L, 4L), typeIds)
    }

    @Test
    fun tagsAreMappedToDomainAsAList() {
        val shop = DemoShops.shops.first().toDomain()

        assertTrue(shop.tags.isNotEmpty())
        assertTrue(shop.tags.none(String::isBlank))
        assertTrue(shop.score > 0)
    }
}
