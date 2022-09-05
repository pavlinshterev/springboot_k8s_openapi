# Microservices with SpringBoot, Kubernetes and OpenAPI

This is a sample project that demonstrates simple microservices implemented using SpringBoot and running in Kubernetes. [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator) is used for generating Java clients from OpenAPI specification files produced by each service. At the end we get a simple and generic way for inter-service communication that also benefits from Java compilation checks for cient APIs and objects.

## Table of contents

- [Microservices with SpringBoot, Kubernetes and OpenAPI](#microservices-with-springboot-kubernetes-and-openapi)
  - [1. How to run](#1-how-to-run)
    - [1.1 Create databases](#11-create-databases)
    - [1.2 Create Kubernetes cluster](#12-create-kubernetes-cluster)
    - [1.3 Install kubectl](#13-install-kubectl)
    - [1.4 Configure K8s cluster](#14-configure-k8s-cluster)
      - [1.4.1 Configure SSL certificates for K8s ingress](#141-configure-ssl-certificates-for-k8s-ingress)
      - [1.4.2 Modify k8s_cluster_config.bash](#142-modify-k8s_cluster_configbash)
      - [1.4.3 Run k8s_cluster_config.bash script](#143-run-k8s_cluster_configbash-script)
    - [1.5 Install Java 17, Docker, Maven](#15-install-java-17-docker-maven)
    - [1.6 Configure k8s_redeploy.bash script](#16-configure-k8s_redeploybash-script)
      - [1.6.1 Copy k8s_redeploy.bash to your K8s cluster machine](#161-copy-k8s_redeploybash-to-your-k8s-cluster-machine)
      - [1.6.2 Modify k8s_redeploy.bash](#162-modify-k8s_redeploybash)
    - [1.7 Modify do_it_all.bash](#17-modify-do_it_allbash)
    - [1.8 Run do_it_all.bash](#18-run-do_it_allbash)
  - [2. Description](#2-description)
    - [2.1 Project hierarchy and build configuration](#21-project-hierarchy-and-build-configuration)
    - [2.2 SpringBoot configuration](#22-springboot-configuration)
    - [2.3 SpringDoc-OpenAPI configuration](#23-springdoc-openapi-configuration)
    - [2.4 OpenAPI configuration](#24-openapi-configuration)
    - [2.5 OpenAPI-Generator configuration](#25-openapi-generator-configuration)
    - [2.6 Docker images](#26-docker-images)
    - [2.7 Kubernetes deployment and configuration](#27-kubernetes-deployment-and-configuration)


## [1. How to run](#table-of-contents)

### [1.1 Create databases](#table-of-contents)

This project has two microservices (Author and Book services) that uses RDB to store and access data. So the first task is to create 2 databases:

For MySQL DB server you can do so with:
```shell
mysqladmin -p -u root create authorsdb
```
```shell
mysql -u root -p
```
to enter MySQL console and then (make sure to replace 'YOUR_SECURE_PASSWORD'):
```shell
GRANT ALL ON authorsdb.* TO 'authorsdbuser'@'%' IDENTIFIED BY 'YOUR_SECURE_PASSWORD';
```

You can use authorsdb DB for the Author service and then create another DB for the Book service.

### [1.2 Create Kubernetes cluster](#table-of-contents)

This project uses K3d cluster. Make sure port 30001 is forwarded to the cluster. For example you can create your K3d cluster with the following command:
```bash
k3d cluster create samplecluster -p "30000-30040:30000-30040@server:0"
```

### [1.3 Install kubectl](#table-of-contents)

### [1.4 Configure K8s cluster](#table-of-contents) 

K8s cluster should be configured using the <i>./scripts/k8s_cluster_config.bash</i> script but before running the script. Follow the steps below.

#### [1.4.1 Configure SSL certificates for K8s ingress](#table-of-contents)

These certificates will be used by K8s nginx ingress controller for SSL connections for all configured services. k8s_cluster_config.bash script expects:

>/certs/privkey.pem

containing your private key

and 

>/certs/fullchain.pem

containing the full certificate chain (your certificate along all intermediate and root certificates) 

#### [1.4.2 Modify k8s_cluster_config.bash](#table-of-contents)

Replace:
 * DOCKER_REGISTRY_ADDRESS
 * DOCKER_REGISTRY_USER
 * DOCKER_REGISTRY_PASS - 

with details about your Docker registry.

Replace:
* DB_SERVER_ADDRESS
* AUTHOR_DB_NAME
* AUTHOR_DB_USER
* AUTHOR_DB_PASS
* BOOK_DB_NAME
* BOOK_DB_USER
* BOOK_DB_PASS 

with details about the DBs created at step 1.1.

Note that k8s_cluster_config.bash creates a K8s secret that is used by the SpringBoot services to configure the DB access. The script configures the DB connection string and presumes MySQL DB, so if you use another DB type then update the script with the correct jdbc connection string.

#### [1.4.3 Run k8s_cluster_config.bash script](#table-of-contents)

```shell
./scripts/k8s_cluster_config.bash
```

It will configure the K8s cluster with secrets that are used for:
- server SSL certificates
- accessing Docker repo where the Docker images for the services are kept
- configuring DB credentials and address

By using these K8s secrets we can remove all sensitive configuration data from the source code.

### [1.5 Install Java 17, Docker, Maven](#table-of-contents)

### [1.6 Configure k8s_redeploy.bash script](#table-of-contents)

<i>./scripts/k8s_redeploy.bash</i> script is used for recreating K8s objects from K8s deployment files. Before running it follow the steps below.

#### [1.6.1 Copy k8s_redeploy.bash to your K8s cluster machine](#table-of-contents)

Add the k8s.redeploy.bash in ~/scripts directory on your server where K8s cluster is running

#### [1.6.2 Modify k8s_redeploy.bash](#table-of-contents)

Replace:
* DOCKER_REGISTRY_ADDRESS - your Docker registry. E.g yourdockerregistry.com:5001
* YOUR_SERVER_HOSTNAME - your K8s cluster hostname. E.g yourdomain.com

### [1.7 Modify do_it_all.bash](#table-of-contents)

<i>./scripts/do_it_all.bash</i> is used for building, uploading, and redeploying the project.

Replace:
* YOUR_SERVER_ADDRESS:PORT_NUM - the address of your server. E.g https://yourdomain.com:30001
* APP_VERSION - app version. E.g 1.0. It will be used for API versioning in the generated OpenAPI spec
* DOCKER_REGISTRY_ADDRESS - your Docker registry. E.g yourdockerregistry.com:5001
* YOUR_SERVER_IP - the address of the server running the K8s cluster. It is used for uploading the K8s deployment files.
* YOUR_SERVER_PASS - root user password for your server. SSH should be enabled. Modify the script if you want to use a different user.

### [1.8 Run do_it_all.bash](#table-of-contents)

```shell
./scripts/do_it_all.bash
```

The script will do:

* 1. Run Maven and build Java artefacts
* 2. Build Docker images
* 3. Push Docker images to configured Docker registry
* 4. Upload Kubernetes deployment files
* 5. Delete and recreate Kubernetes objects

## [2. Description](#table-of-contents)

### [2.1 Project hierarchy and build configuration](#table-of-contents)

All projects are build using Maven. All projects have a common parent project so that they can be built at once and have the same versions.

* author-service - SpringBoot microservice that provide APIs for CRUD operations over authors. During build generates an OpenAPI specification with all provided APIs. 
* author-service-client - uses [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator) to generate REST client from the OpenAPI spec file produced by author-service
* book-service - SpringBoot microservice that provide APIs for CRUD operations over books. During build generates an OpenAPI specification with all provided APIs.
* book-service-client - uses [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator) to generate REST client from the OpenAPI spec file produced by book-service
* ui-service - SpringBoot microservice that demonstrate usage of author-service-client and book-service-client.
* common - library project

Key points:
* There is no source code dependency between the services projects but at the same time client services benefit from Java compilation checks.
* The autogenerated client projects do not depend on the services projects but only accept a single OpenAPI spec file as an input. This means that the microservices could be in a totally different source tree. You could also call a 3rd party service. The only thing you need is it's OpenAPI spec.
* Generated OpenAPI spec is fully functional. No additional configuration is required, you can just import it in Postman and call the APIs. This is possible because it is generated with the correct server URL.

### [2.2 SpringBoot configuration](#table-of-contents)

Since we're having our own parent Maven project

```xml
<parent>
   <groupId>com.pshterev.microservices</groupId>
   <artifactId>project-root</artifactId>
   <version>0.1</version>
</parent>
```

we can't set springboot-starter-parent as a parent we need to add

```xml
<dependencyManagement>
   <dependencies>
      <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-dependencies</artifactId>
         <version>${spring.boot.version}</version>
         <type>pom</type>
         <scope>import</scope>
      </dependency>
   </dependencies>
</dependencyManagement>
```

to automatically resolve SpringBoot dependencies versions

and also

```xml
<execution>
   <goals>
      <goal>repackage</goal>
   </goals>
</execution>
```

in <b>spring-boot-maven-plugin</b> configuration

### [2.3 SpringDoc-OpenAPI configuration](#table-of-contents)

SpringDoc-OpenAPI Maven plugin is used to save the OpenAPI specification for all provided REST APIs

```xml
<plugin>
   <groupId>org.springdoc</groupId>
   <artifactId>springdoc-openapi-maven-plugin</artifactId>
   <version>${spring.doc.maven.plugin.version}</version>
   <executions>
      <execution>
         <id>pre-integration-test</id>
         <goals>
            <goal>generate</goal>
         </goals>
      </execution>
   </executions>
   <configuration>
      <outputFileName>openapi.json</outputFileName>
      <outputDir>${project.basedir}/openapi-output</outputDir>
      <apiDocsUrl>http://localhost:30001/authors/v3/api-docs</apiDocsUrl>
   </configuration>
</plugin>
```
It's important to specify the output file and the correct api-docs URL - note the port and the first segment 'authors'. This is because we have the following config in <b>application.properties</b>

```properties
server.servlet.context-path=/authors
server.port=30001
```

This plugin integrates with SpringBoot-Maven plugin which needs to have an additional configuration as well

```xml
<plugin>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-maven-plugin</artifactId>
   <version>${spring.boot.version}</version>
   ...
   <executions>
      <execution>
         <id>pre-integration-test</id>
         <goals>
            <goal>start</goal>
         </goals>
         <configuration>
            <profiles>local</profiles>
         </configuration>
      </execution>
      <execution>
         <id>post-integration-test</id>
         <goals>
            <goal>stop</goal>
         </goals>
      </execution>
      <execution>
         <goals>
            <goal>repackage</goal>
         </goals>
      </execution>
   </executions>
</plugin>
```

Note the <i>executions</i> section. These are required in order for the SpringDoc-OpenAPI plugin to access the api docs and produce the OpenAPI specification file. And this means that we're actually running the SpringBoot application during the build. Because of this note this important part

```xml
<execution>
   <id>pre-integration-test</id>
   <goals>
      <goal>start</goal>
   </goals>
   <configuration>
      <profiles>local</profiles>
   </configuration>
</execution>
```

It instructs the application to run with <b>local</b> Maven profile during build. And because of this configuration

```xml
<profiles>
   <profile>
      <id>local</id>
      <activation>
         <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
         <spring-boot.run.profiles>local</spring-boot.run.profiles>
      </properties>
   </profile>
   <profile>
      <id>prod</id>
      <properties>
         <spring-boot.run.profiles>prod</spring-boot.run.profiles>
      </properties>
   </profile>
</profiles>
```

we're actually activating the <b>local</b> SpringBoot profile when <b>local</b> Maven profile is activated. Then to be able to run the SpringBoot application locally without accessing a remote database we have <b>application-local.properties</b> configuration file that is used with the <b>local</b> SpringBoot profile with in-memory DB configuration

```properties
spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=sa
```

while the <b>prod</b> SpringBoot profile has the real DB connection

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL57Dialect
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

and note that the jdbc connection string and DB credentials are configured not in the application.properties but with environment variables. More on that later.

Last related configuration bit is activating the <b>prod</b> SpringBoot profile when running the application in production environment and this is done in the <b>dockerfile</b>

```shell
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app.jar"]
```

### [2.4 OpenAPI configuration](#table-of-contents)

You could annotate your APIs with all kind of metadata such as descriptions, examples, errors, etc. In this project no additional data is added though for simplicity and still we have a fully functional OpenAPI spec. One this is needed though

```java
@Bean
public OpenAPI customOpenAPI() throws MalformedURLException {
  return new OpenAPI()
          .addServersItem(new Server().url(new URL(new URL(serverAddress), SERVICE_PATH).toString()))
          .info(new Info().title("Author service").version(version));
}
```

Here we're adding a server with a correct URL address. The <b>serverAddress</b> and <b>version</b> are Spring properties

```java
@Value("${serverAddress:https://default}")
private String serverAddress; // base server address for example 'example.com'
@Value("${version:0.0}")
private String version;
```

and these properties only makes sense when generating the OpenAPI spec which is when we're running the application during the build. Values are coming from Maven

```xml
<plugin>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-maven-plugin</artifactId>
   <version>${spring.boot.version}</version>
   <configuration>
      <mainClass>com.pshterev.microservices.authorservice.AuthorServiceApplication</mainClass>
      <systemPropertyVariables>
         <serverAddress>should_be_overriden</serverAddress>
         <version>should_be_overriden</version>
      </systemPropertyVariables>
   </configuration>
   ...
</plugin>
```

And are passed to Maven from the build scripts like this

```shell
./mvnw clean install -Pprod -Dspring-boot.run.jvmArguments="-DserverAddress=https://YOUR_SERVER_ADDRESS:PORT_NUM -Dversion=APP_VERSION" "$@"
```

Note that when running the application in production we don't use these properties so default values are used.

### [2.5 OpenAPI-Generator configuration](#table-of-contents)

There are empty *-client projects that only run the OpenAPI-Generator which generates a whole Java project with REST client for the specified OpenAPI spec input file. It also installs the generated project as a Maven artifact. Whole OpenAPI-Generator config is

```xml
<plugin>
   <groupId>org.openapitools</groupId>
   <artifactId>openapi-generator-maven-plugin</artifactId>
   <version>${openapi.generator.maven.plugin.version}</version>
   <executions>
      <execution>
         <goals>
            <goal>generate</goal>
         </goals>
         <configuration>
            <inputSpec>${project.basedir}/../author-service/openapi-output/openapi.json</inputSpec>
            <generatorName>java</generatorName>
            <generateApiTests>false</generateApiTests>
            <generateModelTests>false</generateModelTests>
            <configOptions>
               <sourceFolder>src/gen/java/main</sourceFolder>
               <groupId>com.pshterev.microservices</groupId>
               <artifactId>author-service-client</artifactId>
               <invokerPackage>com.pshterev.microservices.authorservice.client.app</invokerPackage>
               <apiPackage>com.pshterev.microservices.authorservice.client.api</apiPackage>
               <modelPackage>com.pshterev.microservices.authorservice.client.model</modelPackage>
            </configOptions>
         </configuration>
      </execution>
   </executions>
</plugin>
```
We specify a filepath to the openapi.json which is obviously the OpenAPI spec input file. Also specify artifact and group ids for the Maven artifact and package names.

### [2.6 Docker images](#table-of-contents)

Each of the microservices - author, book and ui builds a Docker container that is then used to run in Kubernetes cluster. The Docker container is uploaded to the specified Docker registry and then Kubernetes cluster is configured to access this registry and pull the required images. Docker image build and upload is done in <i>./scripts/build_docker_image.bash</i> which uses a <b>dockerfile</b> for image configuration.

### [2.7 Kubernetes deployment and configuration](#table-of-contents)

All K8s deployment files are in <b>k8s</b> directory. Deployment contains:
 * nginx configuration and deployment in <b>ingress-controller</b> directory
 * <i>microservices</i> namespace creation
 * one deployment file for each service
 * ingress deployment file

author, book and ui microservices deployment files are pretty similar. Here is how DB configuration is done

```yaml
env:
- name: SPRING_DATASOURCE_URL
  valueFrom:
    secretKeyRef:
      name: db-creds
      key: author_db_conn
- name: SPRING_DATASOURCE_USERNAME
  valueFrom:
    secretKeyRef:
      name: db-creds
      key: author_db_user
- name: SPRING_DATASOURCE_PASSWORD
  valueFrom:
    secretKeyRef:
      name: db-creds
      key: author_db_pass
```

Spring checks these environment variables and configures the datasource. The values are taken from db-creds secret that we previously created and configured in step: 1.4.3.

{{AUTHOR_DOCKER_IMAGE}} is replaced in <i>k8s_redeploy.bash</i>

```shell
cat author-service-deployment.yaml | sed "s/{{AUTHOR_DOCKER_IMAGE}}/DOCKER_REGISTRY_ADDRESS\/author-service-docker/g" | kubectl apply -f -
```

Again this is to keep private info out of the source code.

Here is the K8s Service configuration

```yaml
kind: Service
metadata:
  creationTimestamp: null
  labels:
    app: author-service
  name: author-service
  namespace: microservices
spec:
  ports:
  - port: 8001
    protocol: TCP
    targetPort: 30001
  selector:
    app: author-service-app
  type: ClusterIP
status:
  loadBalancer: {}
```

Here we're configuring the service to listen on port 8001 and redirect to port 30001 on the pod which should match the port in SpringBoot configuration - <b>application.properties</b>

```properties
server.port=30001
```
And finally let's look at the ingress configuration

```yaml
spec:
  tls:
  - hosts:
    - {{HOSTNAME}}
    secretName: tls-certs
  rules:
  - host: {{HOSTNAME}}
    http:
      paths:
      - path: /books
        pathType: Prefix
        backend:
          service:
            name: book-service
            port: 
              number: 8001
  - host: {{HOSTNAME}}
    http:
      paths:
        - path: /authors
          pathType: Prefix
          backend:
            service:
              name: author-service
              port:
                number: 8001
  - host: {{HOSTNAME}}
    http:
      paths:
      - path: /ui
        pathType: Prefix
        backend:
          service:
            name: ui-service
            port: 
              number: 8001
```

Here we're setting a secret which should contain certificates for SSL configuration and also mapping an URL path to a K8s service that should be called

Finally in <i>ingress-controller/services.yaml</i> we can see the load balancer being configured to listen on machine port 30001

```yaml
apiVersion: v1
kind: Service
metadata:
  name: ingress-nginx-controller-lb
  namespace: ingress-nginx
spec:
  type: LoadBalancer
  ports:
    - name: https
      port: 30001
      targetPort: https
  selector:
    app.kubernetes.io/name: ingress-nginx
    app.kubernetes.io/instance: ingress-nginx
    app.kubernetes.io/component: controller
```

Note that machine port (30001) could be a totally different port than the port on which the SpringBoot application(Tomcat) runs in the container defined in the <b>application.properties</b> file

```properties
server.port=30001
```
But for simplicity and since we can choose whatever valid port in the container these ports match in this example.