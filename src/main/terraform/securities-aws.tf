resource "aws_key_pair" "this" {
  count      = var.aws_enabled ? 1 : 0
  key_name   = var.tag
  public_key = tls_private_key.this.public_key_openssh

  tags = {
    Name = var.tag
  }
}

resource "aws_security_group" "this" {
  count       = var.aws_enabled ? 1 : 0
  name        = var.tag
  description = "HK News security group"
  vpc_id      = aws_vpc.this.0.id

  tags = {
    Name = var.tag
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"

    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }

  ingress {
    from_port = -1
    to_port   = -1
    protocol  = "icmp"

    cidr_blocks = [
      "0.0.0.0/0",
    ]

    ipv6_cidr_blocks = [
      "::/0",
    ]
  }

  dynamic "ingress" {
    for_each = var.firewall_ports

    content {
      from_port = ingress.value
      to_port   = ingress.value
      protocol  = "tcp"

      cidr_blocks = [
        "0.0.0.0/0",
      ]
    }
  }
}
