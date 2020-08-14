output "key_pair" {
  value     = tls_private_key.this.private_key_pem
  sensitive = true
}

output "public_ip" {
  value = aws_eip.this.public_ip
}
