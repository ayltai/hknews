terraform {
  required_version = ">=0.14"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.0"
    }

    null = {
      source  = "hashicorp/null"
      version = "~> 3.0"
    }
  }
}

provider "aws" {
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  region     = var.aws_region
}

provider "aws" {
  alias      = "acm_certificate_provider"
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  region     = "us-east-1"
}
