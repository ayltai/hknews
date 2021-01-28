variable "aws_access_key" {
  description = "AWS access key"
  type        = string
}

variable "aws_secret_key" {
  description = "AWS secret key"
  type        = string
}

variable "aws_region" {
  description = "AWS region"
  default     = "ap-southeast-1"
}

variable "aws_cloudfront_price_class" {
  description = "The AWS CloudFront price class option"
  default     = "PriceClass_200"
}

variable "api_throttle_limit" {
  description = "The AWS Lambda throttling rate limit per second"
  default     = 1000
}

variable "api_stage_name" {
  description = "The API Gateway stage name"
  default     = "api"
}

variable "api_package_file" {
  description = "The AWS Lambda deployment package file path"
  default     = "../../../build/distributions/hknews.zip"
}

variable "app_version" {
  default = "3.0.0"
}

variable "app_domain" {
  default = "hknews.dev"
}

variable "tag" {
  description = "The default project name for tagging all project resources to be created"
  default     = "hknews"
}
