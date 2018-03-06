{{~def 'prjId' (asJavaId ghProjectId)~}}
{{~#def 'githubProjectUrl'}}https://github.com/{{ghProjectOwner}}/{{ghProjectId}}{{/def~}}
{{~#def 'githubRepoUrl'}}{{githubProjectUrl}}.git{{/def~}}

import groovy.util.Node
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask
import org.gradle.api.Task
{{~#if checkLicenseHeader}}
import nl.javadude.gradle.plugins.license.LicenseExtension
{{~/if}}
import org.gradle.jvm.tasks.Jar
{{~#if supportBintray}}
import org.gradle.plugins.signing.Sign
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.swing.*
{{~/if}}

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.13")
    }
}

plugins {
    application
    idea
    eclipse
    kotlin("jvm") version "1.2.21"
    {{~#if supportBintray}}
    id ("maven-publish")
    id ("com.jfrog.bintray") version "1.7.2"
    id ("net.saliman.properties") version "1.4.6"
    {{~/if}}
    id ("com.github.ethankhall.semantic-versioning") version "1.1.0"
    {{~#if checkLicenseHeader}}
    id ("com.github.hierynomus.license") version "0.12.1"
    {{~/if}}
}

val {{prjId}}VersionMajor by project
val {{prjId}}VersionMinor by project
val {{prjId}}VersionPatch by project
val {{prjId}}ReleaseBuild by project
val releaseBuild = {{prjId}}ReleaseBuild.toString().toBoolean()
val {{prjId}}Version = "" + {{prjId}}VersionMajor + "." + {{prjId}}VersionMinor + "." + {{prjId}}VersionPatch + (if(releaseBuild) "" else "-SNAPSHOT")

repositories {
  jcenter()
  mavenCentral()
}

apply {
  plugin("application")
  plugin("eclipse")
  plugin("idea")
  {{~#if supportBintray}}
  plugin("signing")
  {{~/if}}
  plugin("org.jetbrains.dokka")
  {{~#if checkLicenseHeader}}
  plugin("com.github.hierynomus.license")
  {{~/if}}
}
{{~#ifb (or useJUnit5 useSpek)}}
val test by tasks.getting(Test::class) {
    useJUnitPlatform()
}
{{~/ifb}}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
group = "{{group}}"
version = {{prjId}}Version

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}
{{~#if checkLicenseHeader}}

license {
    header = file("license-header.txt")
    skipExistingHeaders = true
    ignoreFailures = false
}
{{~/if}}
{{~#if supportBintray}}

tasks.withType<Sign> {
    sign(configurations.archives)
    onlyIf { gradle.taskGraph.allTasks.any{task: Task -> isPublishTask(task)} }
}
{{~/if}}

dependencies {
  compile(kotlin("reflect"))
  compile(kotlin("stdlib"))
  compile("org.slf4j:slf4j-api:1.7.21")
  runtime ("ch.qos.logback:logback-classic:1.1.7")
  {{~#if useJUnit4}}
  testCompile("junit:junit:4.12")
  {{~/if}}
  {{~#if useJUnit5}}
  testCompile("org.junit.jupiter:junit-jupiter-api:5.1.0")
  testCompile("org.junit.jupiter:junit-jupiter-engine:5.1.0")
  testCompile("org.junit.jupiter:junit-jupiter-params:5.1.0")
  {{~/if}}
  {{~#if useKotlinTest}}
  testCompile("io.kotlintest:kotlintest:2.0.7") {
      exclude(module = "kotlin-reflect")
  }
  {{~/if}}
  {{~#if useSpek}}
  testCompile("com.winterbe:expekt:0.5.0")
  testCompile("org.jetbrains.spek:spek-api:1.1.5"){
      exclude("org.jetbrains.kotlin")
  }
  testRuntime("org.jetbrains.spek:spek-junit-platform-engine:1.1.5") {
      exclude("org.junit.platform")
      exclude("org.jetbrains.kotlin")
  }
  testRuntime("org.junit.jupiter:junit-jupiter-engine:5.1.0")
  {{~/if}}
  testCompile("ch.qos.logback:logback-classic:1.1.7")
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
            "Implementation-Title" to "{{prjId}}",
            "Main-Class" to "{{appModule.basePackage}}.{{appMainClass}}",
            "Implementation-Version" to {{prjId}}Version
        ))
    }
}

application {
    mainClassName = "{{appModule.basePackage}}.{{appMainClass}}"
}

val sourcesJar = task<Jar>("sourcesJar") {
    dependsOn("classes")
    from(sourceSets("main").allSource)
    classifier = "sources"
}

val dokkaJar = task<Jar>("dokkaJar") {
    dependsOn("dokka")
    classifier = "javadoc"
    from((tasks.getByName("dokka") as DokkaTask).outputDirectory)
}

artifacts {
    add("archives", sourcesJar)
    add("archives", dokkaJar)
}
{{~#if supportBintray}}

publishing {
    (publications) {
        "{{prjId}}".invoke(MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar) { classifier = "sources" }
            artifact(dokkaJar) { classifier = "javadoc" }
            groupId = "{{group}}"
            artifactId = project.name
            version = {{prjId}}Version

            pom.withXml {
                val root = asNode()
                root.appendNode("name", "Module ${project.name}")
                root.appendNode("description", "The ${project.name} artifact")
                root.appendNode("url", "{{githubProjectUrl}}")

                val scm = root.appendNode("scm")
                scm.appendNode("url", "{{githubProjectUrl}}")
                scm.appendNode("connection", "{{githubRepoUrl}}")
                scm.appendNode("developerConnection", "{{githubRepoUrl}}")

                val developers = root.appendNode("developers")
                var developer : Node
                {{~#each developers}}
                developer = developers.appendNode("developer")
                developer.appendNode("id", "{{id}}")
                developer.appendNode("name", "{{name}}")
                {{#if email}}developer.appendNode("email", "{{email}}"){{/if}}
                {{~/each}}

                val licenseNode = root.appendNode("licenses").appendNode("license")
                licenseNode.appendNode("name", "{{ext.licenseName}}")
                licenseNode.appendNode("url", "{{ext.licenseUrl}}")
                licenseNode.appendNode("distribution", "repo")
            }
        }
    }
}


fun readPasswordFromConsole(title: String, prompt: String) : String{
    val panel = JPanel()
    val label = JLabel(prompt)
    val pass = JPasswordField(24)
    panel.add(label)
    panel.add(pass)
    val options = arrayOf("OK", "Cancel")
    val option = JOptionPane.showOptionDialog(null, panel, title,
            JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null)
    if(option != 0) throw InvalidUserDataException("Operation cancelled by the user.")
    return String(pass.password)
}

fun isPublishTask(task: Task): Boolean {
  return task.name.startsWith("publish")
}

gradle.taskGraph.whenReady {
    if (gradle.taskGraph.allTasks.any {task : Task -> isPublishTask(task)}) {
        val signingKeyId = propertyOrElse("signingKeyId", "")
        val signingSecretKeyRingFile = propertyOrElse("signingSecretKeyRingFile", "")
        if(signingKeyId.isEmpty() || signingSecretKeyRingFile.isEmpty())
            throw InvalidUserDataException("Please configure your signing credentials in gradle-local.properties.")
        val password = readPasswordFromConsole("Please enter your PGP credentials", "PGP Private Key Password")
        allprojects { ext["signing.keyId"] = signingKeyId }
        allprojects { ext["signing.secretKeyRingFile"] = signingSecretKeyRingFile }
        allprojects { ext["signing.password"] = password }
    }
}

bintray {
    user = propertyOrElse("bintrayUser", "unknownUser")
    key = propertyOrElse("bintrayKey", "unknownKey")
    setPublications("{{prjId}}")
    with(pkg) {
        repo = "maven"
        name = "{{prjId}}"
        userOrg = "{{ghProjectOwner}}"
        setLicenses("{{license}}")
        vcsUrl = "{{githubRepoUrl}}"
        with(version) {
            name = {{prjId}}Version
            desc = "{{prjId}} ${{prjId}}Version"
            released = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ"))
            vcsTag = {{prjId}}Version
            with(gpg) {
                sign = true
            }
        }
    }
}
{{~/if}}

fun propertyOrElse(propName: String, defVal: String) : String = if(project.hasProperty(propName)) (project.property(propName) as String) else defVal
fun sourceSets(name: String) = the<JavaPluginConvention>().sourceSets.getByName(name)
