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
- apm-tracing-annotation-plugin-{version}.jar
- feign-plugin-{version}.jar
- mysql-5.x-plugin-1.0.0.jar
- mysql-8.x-plugin-1.0.0.jar
- okhttp3-plugin-1.0.0.jar
- rest-template-plugin-1.0.0.jar