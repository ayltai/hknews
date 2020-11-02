terraform {
  required_version = ">=0.12"
}

provider "aws" {
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  region     = var.aws_region
  version    = "~> 2.70"
}

provider "alicloud" {
  access_key = var.alicloud_access_key
  secret_key = var.alicloud_secret_key
  region     = var.alicloud_region
  version    = "~> 1.95"
}

provider "tls" {
  version = "~> 2.1"
}

provider "local" {
  version = "~> 1.4"
}

provider "random" {
  version = "~> 2.3"
}

provider "null" {
  version = "~> 2.1"
}
