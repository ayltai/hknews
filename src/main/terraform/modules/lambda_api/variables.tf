variable "aws_region" {
  type    = string
  default = "ap-southeast-1"
}

variable "aws_iam_role_arn" {
  type = string
}

variable "function_name" {
  type = string
}

variable "filename" {
  type = string
}

variable "handler" {
  type = string
}

variable "runtime" {
  type    = string
  default = "java11"
}

variable "memory" {
  type    = number
  default = 768
}

variable "timeout" {
  type    = number
  default = 30
}

variable "rest_api_id" {
  type = string
}

variable "rest_api_root_id" {
  type = string
}

variable "rest_api_execution_arn" {
  type = string
}

variable "path_part" {
  type = string
}

variable "log_retention" {
  type    = number
  default = 14
}

variable "tag" {
  type = string
}
