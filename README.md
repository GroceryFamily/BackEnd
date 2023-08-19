# BackEnd

[![Build Status](https://github.com/GroceryFamily/BackEnd/workflows/build/badge.svg)](https://github.com/GroceryFamily/BackEnd/actions/workflows/build.yml)

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