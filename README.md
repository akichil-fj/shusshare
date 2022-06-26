[![Test with Gradle](https://github.com/a-fujimt/shusshare/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/a-fujimt/shusshare/actions/workflows/gradle-build.yml)
[![codecov](https://codecov.io/gh/akichil-fj/shusshare/branch/main/graph/badge.svg?token=3GHLEMN1UK)](https://codecov.io/gh/akichil-fj/shusshare)

# Shusshare(出社share)
## What's this?
自分の出社状況をシェアできるアプリケーション。
今日誰が出社しているかを一目で確認することができます。 

## 動作要件
- Java 17+

### 動作時の注意
- `application.yml`には、DB接続情報が含まれていないため、環境変数orプロファイルを追加する
```
#　以下2つ
spring.datasource.user
spring.datasource.password
```

## 使用技術
- SpringBoot
- Mybatis
- Thymeleaf
