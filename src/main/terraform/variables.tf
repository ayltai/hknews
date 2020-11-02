variable "aws_enabled" {
  description = "Enable the creation of AWS resources"
  type        = bool
  default     = true
}

variable "alicloud_enabled" {
  description = "Enable the creation of Alibaba Cloud resources"
  type        = bool
  default     = false
}

variable "aws_access_key" {
  description = "AWS access key"
  type        = string
}

variable "alicloud_access_key" {
  description = "Alibaba Cloud access key"
  type        = string
}

variable "aws_secret_key" {
  description = "AWS secret key"
  type        = string
}

variable "alicloud_secret_key" {
  description = "Alibaba Cloud secret key"
  type        = string
}

variable "tag" {
  description = "The tag for all resources used in this project"
  default     = "hknews"
}

variable "aws_region" {
  description = "AWS region"
  default     = "us-west-2"
}

variable "alicloud_region" {
  description = "Alibaba Cloud region"
  default     = "ap-southeast-3"
}

variable "aws_zone" {
  description = "AWS availability zone"
  default     = "us-west-2a"
}

variable "alicloud_zone" {
  description = "Alibaba Cloud availability zone"
  default     = "ap-southeast-3b"
}

variable "vpc_cidr_block" {
  description = "CIDR block for VPC"
  default     = "10.0.0.0/24"
}

variable "subnet_cidr_block" {
  description = "CIDR block for subnet"
  default     = "10.0.0.0/28"
}

variable "ami_filter" {
  description = "AMI filter"
  default     = "ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"
}

variable "alicloud_image_filter" {
  description = "Alibaba Cloud image filter"
  default     = "^ubuntu"
}

variable "ami_owner" {
  description = "AMI owner is Canonical"
  default     = "099720109477"
}

variable "alicloud_image_owner" {
  description = "Alibaba Cloud image owner"
  default     = "system"
}

variable "aws_instance_type" {
  description = "EC2 instance type"
  default     = "t3a.micro"
}

variable "alicloud_instance_type" {
  description = "ECS instance type"
  default     = "ecs.t6-c2m1.large"
}

variable "aws_storage_size" {
  description = "The size of the storage attached to EC2 instances"
  type        = number
  default     = 10
}

variable "alicloud_storage_size" {
  description = "The size of the storage attached to ECS instances"
  type        = number
  default     = 20
}

variable "firewall_ports" {
  description = "The ports to be opened"
  type        = list(number)

  default = [
    22,
    80,
    443,
  ]
}

variable "username" {
  description = "VM user"
  default     = "ubuntu"
}

variable "timeout" {
  description = "The maximum amount of time allowed for post deployment configurations"
  default     = "30m"
}

variable "timezone" {
  description = "The timezone of HK News servers"
  default     = "Asia/Hong_Kong"
}

variable "domain_name" {
  description = "The public facing domain name to access HK News servers"
  default     = "hknews.dev"
}

variable "key_store_password" {
  description = "The password to access the key store with the domain SSL certificate"
  type        = string
}

variable "certbot_email" {
  description = "The email address for generating Let's Encrypt SSL certificate"
  type        = string
}

variable "instrumental_api_key" {
  description = "The API key for instrumentald"
  type        = string
}

variable "logzio_token" {
  description = "The Logz.io API token"
  type        = string
}
