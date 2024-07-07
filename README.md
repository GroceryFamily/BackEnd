# BackEnd

[![Build Status](https://github.com/GroceryFamily/BackEnd/workflows/build/badge.svg)](https://github.com/GroceryFamily/BackEnd/actions/workflows/build.yml)

The project currently utilizes Java 17. Install SDKMAN! and install the same version that is used for automatic builds:

```shell
sdk install java 17.0.11-tem
```

You can set is as default version or switch to it manually:

```shell
sdk use java 17.0.11-tem
```

Install and run MySQL server TBD

Now everything is ready to start application server:

```shell
mvn -pl server -am spring-boot:run
```

## News

* `GroceryDad` now talks to `GroceryMom` via the API client, so the server must be running when scraping

## Cache

It seems like a good idea to use [GitHub LFS](https://docs.github.com/en/repositories/working-with-files/managing-large-files/about-git-large-file-storage) for our cache, so we can reuse it in tests. The installation and configuration instructions can be found [here](https://docs.github.com/en/repositories/working-with-files/managing-large-files/installing-git-large-file-storage) and [here](https://docs.github.com/en/repositories/working-with-files/managing-large-files/configuring-git-large-file-storage) respectively.

## Secrets

Create `secrets/application-secrets.yaml` in the project's root directory with the following content:

```yaml
spring.datasource:
  username: USERNAME
  password: PASSWORD
```

Replace `USERNAME` and `PASSWORD` with actual values.