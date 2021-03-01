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
  default = 384
}

variable "timeout" {
  type    = number
  default = 600
}

variable "log_retention" {
  type    = number
  default = 14
}

variable "log_filter_namespace" {
  type    = string
  default = "CronJob"
}

variable "event_name" {
  type = string
}

variable "schedule_expression" {
  type = string
}

variable "tag" {
  type = string
}
