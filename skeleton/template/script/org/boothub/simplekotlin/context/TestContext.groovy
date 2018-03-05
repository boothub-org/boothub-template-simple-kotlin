/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.boothub.simplekotlin.context

import groovy.transform.SelfType
import org.boothub.context.ConfiguredBy
import org.boothub.context.ProjectContext
import org.boothub.context.TextIOConfigurator
import org.beryx.textio.TextIO

@SelfType(ProjectContext)
@ConfiguredBy(TestContext.Configurator)
trait TestContext {
    static enum TestFramework {
        JUNIT4('JUnit 4'), JUNIT5('JUnit 5'), KOTLIN_TEST('kotlin.test'), SPEK('Spek')

        String name

        TestFramework(String name) { this.name = name }

        @Override
        String toString() { name }
    }

    TestFramework testFramework = TestFramework.KOTLIN_TEST

    boolean isUseJUnit4() { testFramework == TestFramework.JUNIT4 }
    boolean isUseJUnit5() { testFramework == TestFramework.JUNIT5 }
    boolean isUseKotlinTest() { testFramework == TestFramework.KOTLIN_TEST}
    boolean isUseSpek() { testFramework == TestFramework.SPEK}

    static class Configurator extends TextIOConfigurator  {
        @Override
        void configureWithTextIO(ProjectContext context, TextIO textIO) {
            def ctx = context as TestContext
            ctx.testFramework = textIO.newEnumInputReader(TestFramework)
                    .withDefaultValue(ctx.testFramework)
                    .read("Test Framework")
        }
    }
}
