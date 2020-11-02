output "key_pair" {
  value     = tls_private_key.this.private_key_pem
  sensitive = true
}

output "aws_public_ip" {
  value = var.aws_enabled ? aws_eip.this.0.public_ip : ""
}

output "alicloud_public_ip" {
  value = var.alicloud_enabled ? alicloud_eip.this.0.ip_address : ""
}
