resource "tls_private_key" "this" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "local_file" "private_key" {
  filename        = "${var.tag}.pem"
  file_permission = "0600"
  content         = tls_private_key.this.private_key_pem
}
