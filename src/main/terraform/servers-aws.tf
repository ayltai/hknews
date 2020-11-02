resource "aws_instance" "this" {
  count         = var.aws_enabled ? 1 : 0
  ami           = data.aws_ami.this.id
  instance_type = var.aws_instance_type
  subnet_id     = aws_subnet.this.id
  key_name      = aws_key_pair.this.key_name

  vpc_security_group_ids = [
    aws_security_group.this.id,
  ]

  root_block_device {
    volume_size = var.aws_storage_size
  }

  tags = {
    Name = var.tag
  }
}
