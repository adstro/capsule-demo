package me.adamstroud.capsuledemo.di

import com.squareup.moshi.Moshi
import me.adamstroud.capsuledemo.model.ArticleResponse
import org.threeten.bp.OffsetDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MoshiTest {
    private lateinit var moshi: Moshi

    @BeforeTest
    fun beforeTest() {
        moshi = ApplicationModule().provideMoshi()
    }

    @Test
    fun testDate() {
        val actual = moshi.adapter(OffsetDateTime::class.java).fromJson(""""2020-04-06T03:39:01+0000"""")
        val expected = OffsetDateTime.parse("2020-04-06T03:39:01+00:00")
        assertEquals(expected = expected, actual = actual)
    }

    @Test
    fun testResponse() {
        val json = MoshiTest::class.java.getResource("/response.json")!!.readText()

        val response = moshi.adapter(ArticleResponse::class.java).fromJson(json)

        assertNotNull(response?.response?.docs)

        val expected = OffsetDateTime.parse("2020-04-06T03:39:01+00:00")
        assertEquals(expected, response?.response?.docs?.first()?.publishDate)
        assertEquals(10, response?.response?.docs?.size)
    }
}