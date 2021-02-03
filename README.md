# HK News

[![GitHub workflow status](https://img.shields.io/github/workflow/status/ayltai/hknews/CI?style=flat)](https://github.com/ayltai/hknews/actions)
[![Codacy grade](https://img.shields.io/codacy/grade/a9257522b45d40e094649095d3a33ecd.svg?style=flat)](https://app.codacy.com/app/AlanTai/hknews/dashboard)
[![Sonar quality gate](https://img.shields.io/sonar/quality_gate/ayltai_hknews?style=flat&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_hknews)
[![Sonar violations (short format)](https://img.shields.io/sonar/violations/ayltai_hknews?style=flat&format=short&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_hknews)
[![Sonar Test Success Rate](https://img.shields.io/sonar/test_success_density/ayltai_hknews?style=flat&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_hknews)
[![Sonar Coverage](https://img.shields.io/sonar/coverage/ayltai_hknews?style=flat&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_hknews)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ayltai_hknews&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=ayltai_hknews)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ayltai_hknews&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=ayltai_hknews)
[![Sonar Tech Debt](https://img.shields.io/sonar/tech_debt/ayltai_hknews?style=flat&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_hknews)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=ayltai_hknews&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=ayltai_hknews)
[![Snyk Vulnerabilities](https://snyk.io/test/github/ayltai/hknews/badge.svg)](https://snyk.io/test/github/ayltai/hknews)
[![Release](https://img.shields.io/github/release/ayltai/hknews.svg?style=flat)](https://github.com/ayltai/hknews/releases)
![Maintenance](https://img.shields.io/maintenance/yes/2021?style=flat)
[![License](https://img.shields.io/github/license/ayltai/hknews.svg?style=flat)](https://github.com/ayltai/hknews/blob/master/LICENSE)

Serves aggregated news from 13 local news publishers in Hong Kong. [https://hknews.dev](https://hknews.dev)

[![Uptime Robot status](https://img.shields.io/uptimerobot/status/m783235303-dd3e7baceda2ae13eb1881cd)](https://stats.uptimerobot.com/8o3Erh6PyD) [![PageSpeed](https://img.shields.io/badge/PageSpeed-96-success)](https://developers.google.com/speed/pagespeed/insights/?url=https%3A%2F%2Fhknews.dev&tab=desktop)

[![Buy me a coffee](https://img.shields.io/static/v1?label=Buy%20me%20a&message=coffee&color=important&style=flat&logo=buy-me-a-coffee&logoColor=white)](https://buymeacoff.ee/ayltai)

## Features

* Serves news from 13 local news publishers
* Supports video news
* Supports daily news and real-time news
* No ads

## Supported news publishers

* [Apple Daily (蘋果日報)](http://hk.apple.nextmedia.com)
* [Oriental Daily (東方日報)](http://orientaldaily.on.cc)
* [Sing Tao (星島日報)](http://std.stheadline.com)
* [Hong Kong Economic Times (經濟日報)](http://www.hket.com)
* [Sing Pao (成報)](https://www.singpao.com.hk)
* [Ming Pao (明報)](http://www.mingpao.com)
* [Headline (頭條日報)](http://hd.stheadline.com)
* [Sky Post (晴報)](http://skypost.ulifestyle.com.hk)
* [Hong Kong Economic Journal (信報)](http://www.hkej.com)
* [RTHK (香港電台)](http://news.rthk.hk)
* [South China Morning Post (南華早報)](http://www.scmp.com/frontpage/hk)
* [The Standard (英文虎報)](http://www.thestandard.com.hk)
* [Wen Wei Po (文匯報)](http://news.wenweipo.com)

## Available APIs

[OpenAPI](https://hknews.dev/api.yaml)

## Deployment

![Architecture](diagram.svg)

The deployment of the following resources is managed by [Terraform](https://terraform.io):

* [AWS Certificate Manager](https://aws.amazon.com/certificate-manager) for managing [hknews.dev](https://hknews.dev) domain certificate
* [AWS Lambda](https://aws.amazon.com/lambda) for serverless API services for HK News website
* [Amazon API Gateway](https://aws.amazon.com/api-gateway) for REST API management
* [Amazon CloudWatch](https://aws.amazon.com/cloudwatch) for monitoring and logging of the system
* [Amazon CloudFront](https://aws.amazon.com/cloudfront) for a fast CDN service for the static files of HK News website
* [Amazon DynamoDB](https://aws.amazon.com/dynamodb) for a managed NoSQL database service for storing news records
* [Amazon EventBridge](https://aws.amazon.com/eventbridge) for a serverless event bus that triggers news parsing functions
* [Amazon Route 53](https://aws.amazon.com/route53) for a DNS service
* [Amazon S3](https://aws.amazon.com/s3) for storing the static files of HK News website

## License
[MIT](https://github.com/ayltai/hknews/blob/master/LICENSE)
