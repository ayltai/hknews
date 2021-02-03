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

variable "api_runtime" {
  type    = string
  default = "java11"
}

variable "api_memory" {
  type    = number
  default = 1024
}

variable "api_timeout" {
  type    = number
  default = 30
}

variable "api_throttle_limit" {
  description = "The AWS Lambda throttling rate limit per second"
  default     = 1000
}

variable "api_minimum_compression_size" {
  description = "The minimum response size to enable API response compression"
  default     = 256
}

variable "api_stage_name" {
  description = "The API Gateway stage name"
  default     = "api"
}

variable "api_log_retention" {
  description = "The number of days to retain API Gateway logs"
  default     = 14
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
