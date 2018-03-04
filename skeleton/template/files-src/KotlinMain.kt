\{{javaComment 'license-header.txt'~}}
@file:JvmName("{{targetFileClass}}")
package {{targetPackage}}

/**
 * Prints "Hello from {{targetFileClass}}!"
 */
fun main(args: Array<String>) {
    println("Hello from {{targetFileClass}}!")
}
