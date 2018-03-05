\{{javaComment 'license-header.txt'~}}
package {{targetPackage}}

\{{~#if useJUnit4}}
import org.junit.Test
import org.junit.Assert

class {{targetFileClass}} {
    @Test
    fun `one plus one should equal two`() {
        Assert.assertEquals(2, 1 + 1)
    }
}
\{{~/if}}
\{{~#if useJUnit5}}
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class {{targetFileClass}} {
    @Test
    fun one_plus_one_should_equal_two() {
        Assertions.assertEquals(2, 1 + 1)
    }
}
\{{~/if}}
\{{~#if useKotlinTest}}
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec

class {{targetFileClass}} : ShouldSpec() {
    init {
        should("correctly compute 1 + 1") {
            1 + 1 shouldBe 2
        }
    }
}
\{{~/if}}
\{{~#if useSpek}}
import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object {{targetFileClass}} : Spek ({
    describe("two numbers") {
        val a = 1
        val b = 2

        on("addition") {
            val sum = a + b

            it("should return the result of adding the first number to the second number") {
                sum.should.equal(3)
            }
        }
    }
})
\{{~/if}}
