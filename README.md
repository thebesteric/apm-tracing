# APM-TRACING
A non-intrusive full link log plug-in

# License
[Apache 2.0 License.](https://www.apache.org/licenses/LICENSE-2.0.txt)

# Quick Start
The javaagent technology is used to complete the log link
```bash
java -javaagent:/xxx/xxx/apm-agent-{version}-jar
```
## Used plugins
If you want to use certain plug-in features, create a plugins directory in your project and add the required plug-ins to the plugins directory
- apache-http-client-plugin-{version}.jar
- feign-plugin-{version}.jar
- mysql-5.x-plugin-{version}.jar
- mysql-8.x-plugin-{version}.jar
- okhttp3-plugin-{version}.jar
- rest-template-plugin-{version}.jar
- apm-tracing-annotation-plugin-{version}.jar
## Used Annotation plugin in Spring Boot
Add apm-agent-extension dependencies to maven or gradle and add apm-tracing-annotation-plugin-{version}.jar to the plugins directory
https://central.sonatype.com/artifact/io.github.thebesteric.framework.apm/apm-agent-extension
```xml
<dependency>
    <groupId>io.github.thebesteric.framework.apm</groupId>
    <artifactId>apm-agent-extension</artifactId>
    <version>{version}</version>
</dependency>
```
Done, you can now use `@ApmTracing` to modify the classes and methods that need logging
```java
@RestController
@RequestMapping("/hello")
public class HelloController {
    @ApmTracing
    @GetMapping
    public String hello(String name) {
        return "Hello " + name;
    }

    @ApmTracing
    @GetMapping("/say")
    public String say(String name) {
        return name + " said hello";
    }
}
```
