# Development

This project has the goal to develop a full-stack of applications serving news in Hong Kong:

| Component     | Framework / Language                                         |
|---------------|--------------------------------------------------------------|
| Backend       | [Spring Boot](https://spring.io/projects/spring-boot) / Java |
| Frontend      | [React](https://reactjs.org) / JavaScript                    |
| Provision     | [Terraform](https://www.terraform.io) / HCL                  | 
| Configuration | [Ansible](https://www.ansible.com) / YAML                    |

...and endless dependencies of the above!

To make things more complicated, what if someone wants to develop this project on Windows?

One would need an automated task management and build tool to manage all these complexities and avoid dependency version conflicts if several other projects co-exist on the same development machine. A natural to this is [Gradle](https://gradle.org).

In contrast with the development complexity, this project is developed with:
* [1 build tool](https://gradle.org)
* [1 IDE](https://www.jetbrains.com/idea)
* [1 developer](https://github.com/ayltai)

In this project, [Gradle](https://gradle.org) is used for:
* Downloading and installing [Terraform](https://www.terraform.io)
* Downloading and installing [Python](https://www.python.org) and [Ansible](https://www.ansible.com)
* Downloading dependencies for Ansible scripts
* Downloading dependencies for the [React](https://reactjs.org) app
* Downloading dependencies for the [Spring Boot](https://spring.io/projects/spring-boot) app
* Running unit tests for [Ansible](https://www.ansible.com) roles
* Running unit tests for the [React](https://reactjs.org) app
* Running unit tests for the [Spring Boot](https://spring.io/projects/spring-boot) server
* Building artifact for the [React](https://reactjs.org) app
* Building artifact for the [Spring Boot](https://spring.io/projects/spring-boot) server
* Provisioning the required infrastructures for deployment using [Terraform](https://www.terraform.io)

A [Docker](https://www.docker.com) based development environment is highly recommended.

### Requirements

There is only one requirement: [Docker](https://www.docker.com). Make sure [Docker](https://hub.docker.com/editions/community/docker-ce-desktop-mac/) is installed and running.

### Quick start

1. Run `./dev.sh` (or `dev.bat` on Windows) to build a [Docker](https://www.docker.com) image and get into the container
2. once you are inside your [Docker](https://www.docker.com) container, you may run the following [Gradle](https://gradle.org) tasks.
3. Run the [Spring Boot](https://spring.io/projects/spring-boot) server:
   ```shell script
   SPRING_PROFILES_ACTIVE=common,development ./gradlew bootRun
   ```
4. Run the [React](https://reactjs.org) app:
   ```shell script
   npm run start
   ```

### Gradle tasks
| Task                | Description |
|---------------------|-------------|
| `buildJava`         | Prepares Java build artifact by copying the artifacts of `buildJavaScript` into the `resources` directory. |
| `buildJavaScript`   | Cleans the contents in `$buildDir` directory and invokes `npm run build`. Depends on `installJavaScript`. |
| `testJavaScript`    | Invokes `npm run test`. Depends on `installJavaScript`. |
| `testAnsible`       | Recursively invokes `molecule test` for all the roles inside `$projectDir/src/main/ansible/roles`. Depends on `installAnsible`. |
| `checkJava`         | Invokes [Checkstyle](https://github.com/checkstyle/checkstyle) and [Spotbugs](https://github.com/spotbugs/spotbugs) linting. |
| `checkTerraform`    | Invokes `terraform validate` with the correct environment variables set. Depends on `initTerraform`. |
| `installJavaScript` | Invokes `npm install`. |
| `installAnsible`    | Installs [Docker](https://www.docker.com), [Python](https://www.python.org) 3 and [pip](https://pypi.org/project/pip) if they are not already installed. Installs [Ansible](https://www.ansible.com) roles and dependencies. |
| `installTerraform`  | Decompress [Terraform](https://www.terraform.io) package and make it executable in `$buildDir`. Depends on `downloadTerraform`.  |
| `applyTerraform`    | Invokes `terraform apply` with the correct environment variables being set. Depends on `initTerraform`. See [Deployment](README.md#deployment) for more information. |
| `planTerraform`     | Invokes `terraform plan` with the correct environment variables being set. Depends on `initTerraform`. See [Deployment](README.md#deployment) for more information. |
| `downloadTerraform` | Downloads [Terraform](https://www.terraform.io) v0.12.29 in `$buildDir`. |
| `initTerraform`     | Invokes `terraform init` with the correct environment variables being set. Depends on `installTerraform`. See [Deployment](README.md#deployment) for more information. |
