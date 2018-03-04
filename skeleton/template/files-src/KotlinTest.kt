\{{javaComment 'license-header.txt'~}}
package {{targetPackage}}

import org.junit.Test
import org.junit.Assert

class {{targetFileClass}} {
    @Test
    fun `one plus one should equal two`() {
        Assert.assertEquals(2, 1 + 1)
    }
}
