terraform {
  required_version = ">=0.12"
}

provider "aws" {
  access_key = var.access_key
  secret_key = var.secret_key
  region     = var.region
  version    = "~> 2.70"
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
