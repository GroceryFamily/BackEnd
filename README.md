# BackEnd

[![Build Status](https://github.com/GroceryFamily/BackEnd/workflows/build/badge.svg)](https://github.com/GroceryFamily/BackEnd/actions/workflows/build.yml)

`GroceryDad` now talks to `GroceryMom` via API client, so the server must be running.

## Secrets

Create `secrets/application-secrets.yaml` in the project's root directory with the following content:

```yaml
spring.datasource:
  username: USERNAME
  password: PASSWORD
```

Replace `USERNAME` and `PASSWORD` with actual values.