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
package org.boothub.simplekotlin

import org.boothub.context.ProjectContext

import java.nio.file.Paths
import org.boothub.Initializr
import org.boothub.gradle.*
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

class BoothubTemplateSimpleKotlinSpec extends Specification {

    private static final String TEMPLATE_DIR = getPath("/template")

    private static final String BASE_PATH = 'org/bizarre_soft'
    private static final APP_MAIN_CLASS = 'WeirdAppMain'
    private static final APP_MAIN_CLASS_PATH = "$BASE_PATH/weird_app/${APP_MAIN_CLASS}.class"

    private static final String CONTEXT_SINGLE = getPath("/base-context-single.yml")


    @Unroll
    def "should create a valid artifact using {#flags}"() {
        when:
        def artifacts = new GradleTemplateBuilder(TEMPLATE_DIR)
                .withContext(context)
                .runGradleBuild()
                .artifacts
        def jars = artifacts['jar']

        then:
        jars.size() == 1
        jars[0].getEntry(APP_MAIN_CLASS_PATH) != null

        where:
        context << contexts
        flags = context.dumpFlags()
    }

    @Unroll
    def "should create a valid application using {#flags}"() {
        expect:
        new OutputChecker(TEMPLATE_DIR, context)
                .checkOutput("Hello from $context.appMainClass!")

        where:
        context << contexts
        flags = context.dumpFlags()
    }

    private static String getPath(String resourcePath) {
        def resource = BoothubTemplateSimpleKotlinSpec.class.getResource(resourcePath)
        assert resource: "Resource not available: $resourcePath"
        Paths.get(resource.toURI()).toAbsolutePath().toString()
    }

    private static Collection<ProjectContext> getContexts() {
        def builder = new ProjectContextStreamBuilder({new Initializr(TEMPLATE_DIR).createContext(CONTEXT_SINGLE)})
                .withFlagNames('testFramework', 'checkLicenseHeader')
        builder.stream().collect(Collectors.toList())
    }

}
