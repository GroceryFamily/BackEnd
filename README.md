# BackEnd

[![Build Status](https://github.com/GroceryFamily/BackEnd/workflows/build/badge.svg)](https://github.com/GroceryFamily/BackEnd/actions/workflows/build.yml)

## News

* `GroceryDad` now talks to `GroceryMom` via the API client, so the server must be running when scraping

## Cache

It seemed like a good idea to use [GitHub LFS](https://docs.github.com/en/repositories/working-with-files/managing-large-files/about-git-large-file-storage) for our cache, so we can reuse it in tests. However, the bandwidth limit is just meager, and it turns out that the better solution is to keep the scraped data as regular repository files.

## Secrets

Create `secrets/application-secrets.yaml` in the project's root directory with the following content:

```yaml
spring.datasource:
  username: USERNAME
  password: PASSWORD
```

Replace `USERNAME` and `PASSWORD` with actual values.